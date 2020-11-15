package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;

public class ConnectionService {
    public final String access_url, resource_url;
    private final Properties props;
    private String access_code;
    private HttpServer server;
    private JsonObject access_tokens;
    private final HttpClient cli;
    private final int server_port;

    /**
     * ConnectionService constructor
     *
     * @param access - access url (for authorization)
     * @param resource - resource url (to access data)
     * @param p - properties object containing app info (for API authentication)
     */
    public ConnectionService(String access, String resource, Properties p) {
        access_url = (access.lastIndexOf(47) == access.length()-1) ? access.substring(0, access.length()-1) : access;
        resource_url = (resource.lastIndexOf(47) == resource.length()-1) ? resource.substring(0, resource.length()-1) : resource;
        props = p;
        access_code = null;
        access_tokens = null;
        server_port = 8080;
        cli = HttpClient.newBuilder().build();
        create_authorize_server();
    }

    /**
     * Create server that will collect the access code after attempting to authorize user
     * TODO: change method of getting code from Jetbrains method
     */
    private void create_authorize_server() {
        try {
            String error = "Authorization code not found. Try again.",
                    success = "Got the code. Return back to your program.";
            server = HttpServer.create();
            server.bind(new InetSocketAddress(server_port), 0);
            ConnectionService t = this;
            // Create server context for "/"
            server.createContext("/", new HttpHandler() {
                /**
                 * Server handler method for "/" path (so essentially any call to server).
                 * Attempts to get access code and save to object
                 *
                 * @param ex - http object
                 * @throws IOException - for reading query and writing response
                 */
                @Override
                public void handle(HttpExchange ex) throws IOException {
                    String query = ex.getRequestURI().getQuery();
                    if (query != null && query.contains("code=")) {
                        t.access_code = query.split("[&=]")[1];
                        ex.sendResponseHeaders(200, success.length());
                        ex.getResponseBody().write(success.getBytes());
                        ex.getResponseBody().close();
                        server.stop(5);
                    } else {
                        ex.sendResponseHeaders(200, error.length());
                        ex.getResponseBody().write(error.getBytes());
                        ex.getResponseBody().close();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method that makes actual Http request to Spotify server
     *
     * @param url - url to make request to
     * @return Data if call was successful or error object if not
     */
    private JsonObject get(String url) {
        HttpRequest req = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + access_tokens.get("access_token").getAsString())
                .uri(URI.create(resource_url + "/v1/browse" + url))
                .GET()
                .build();

        try {
            HttpResponse<String> resp = cli.send(req, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(resp.body()).getAsJsonObject();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return JsonParser.parseString("{\"error\": {\"message\": \"Error connecting to server.\"}}").getAsJsonObject();
        }
    }

    /**
     * Method to get refreshed access tokens from Spotify (after original tokens expire)
     */
    private void refreshTokens() {
        HttpRequest req =  HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", Base64.getEncoder().encodeToString((props.getProperty("client_id") + ":" + props.getProperty("client_secret")).getBytes()))
                .uri(URI.create(access_url + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token&refresh_token=" + access_tokens.get("refresh_token").getAsString()))
                .build();
        try {
            // Make call for new tokens and if successful, save them to object
            HttpResponse<String> res = cli.send(req, HttpResponse.BodyHandlers.ofString());
            JsonObject fresh_token = JsonParser.parseString(res.body()).getAsJsonObject();
            access_tokens.addProperty("access_token", fresh_token.get("access_token").getAsString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether or not user is authorized by checking if object has access code
     *
     * @return Whether or not object has code
     */
    public boolean isAuthorized() {
        return access_code != null;
    }

    /**
     * Using parameters, craft url to get new releases from Spotify and then send to get() helper to make request
     *
     * @param page - Specific page of data to get
     * @param limit - Number of data items to get
     * @return Json data result from request or error json object
     */
    public JsonObject getNewReleases(int page, int limit) {
        return get("/new-releases?limit=" + limit + "&offset=" + (page*limit));
    }

    /**
     * Using parameters, craft url to get featured playlists from Spotify and then send to get() helper to make request
     *
     * @param page - Specific page of data to get
     * @param limit - Number of data items to get
     * @return Json data result from request or error json object
     */
    public JsonObject getFeaturedPlaylists(int page, int limit) {
        return get("/featured-playlists?limit=" + limit + "&offset=" + (page*limit));
    }

    /**
     * Using parameters, craft url to get categories from Spotify and then send to get() helper to make request
     *
     * @param page - Specific page of data to get
     * @param limit - Number of data items to get
     * @return Json data result from request or error json object
     */
    public JsonObject getCategories(int page, int limit) {
        return get("/categories?limit=" + limit + "&offset=" + (page*limit));
    }

    /**
     * This method gets playlists for a category given it's NAME (useful when the ID is unavailable).
     * The method of finding playlists by category name is fairly inefficient because Spotify requires
     * the category id for the API call to get a category's playlists. All the categories need to be searched
     * to find one that has the same name, from there the id can be extracted and the call for playlists can be done.
     * To alleviate this issue, the id is stored in the result, so a user can refer to the other method to get playlists (by id).
     *
     *
     * @param c_name - category name of playlists to get
     * @param page - Specific page of data to get
     * @param limit - Number of data items to get
     * @return Json data result from request or error json object
     */
    public JsonObject getPlaylistsFromCategory(String c_name, int page, int limit) {
        JsonObject res = get("/categories");
        if (res.get("error") != null) return res;
        // Search for category with name that matches argument
        for (JsonElement o : res.getAsJsonObject("categories").getAsJsonArray("items")) {
            // If found, get corresponding id and make API call to get playlists
            if (c_name.trim().equalsIgnoreCase(o.getAsJsonObject().get("name").getAsString())) {
                JsonObject ret = get("/categories/" + o.getAsJsonObject().get("id").getAsString() + "/playlists?limit=" + limit + "&offset=" + (page*limit));
                ret.addProperty("id", o.getAsJsonObject().get("id").getAsString()); // store id in result
                return ret;
            }
        }

        // Not found, unknown category name
        return JsonParser.parseString("{\"error\": {\"message\": \"Unknown category name.\"}}").getAsJsonObject();
    }

    /**
     * Get a specific category's playlists using the category's id
     *
     * @param id - category id for category from which the playlists will be returned
     * @param page - Specific page of data to get
     * @param limit - Number of data items to get
     * @return Json data result from request or error json object
     */
    public JsonObject getPlaylistFromId(String id, int page, int limit) {
        JsonObject ret = get("/categories/" + id + "/playlists?limit=" + limit + "&offset=" + (page*limit));
        ret.addProperty("id", id);
        return ret;
    }

    /**
     * Method to get access code from Spotify during authorization.
     * It busy-waits while the code hasn't been received
     */
    private void getAccessCode() {
        System.out.println("use this link to request the access code:");
        System.out.println(access_url + "/authorize?client_id=" + props.getProperty("client_id") + "&redirect_uri=http://localhost:" + server_port + "&response_type=code");
        server.start();
        System.out.println("waiting for code...");
        while (access_code == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Once access code is received, this method requests resource access tokens (as part of (OAuth's Auth Code flow)
     * from Spotify
     *
     * @return The http response containing the access token (as well as refresh token) or error if invalid info provided; null if request fails to send
     */
    private HttpResponse<String> getTokens() {
        try {
            HttpRequest access_req = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", Base64.getEncoder().encodeToString((props.getProperty("client_id") + ":" + props.getProperty("client_secret")).getBytes()))
                    .uri(URI.create(access_url + "/api/token"))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + access_code + "&redirect_uri=http://localhost:" + server_port))
                    .build();
            System.out.println("making http request for access token...");
            return cli.send(access_req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Main method taking care of object's authorization with Spotify, it makes calls to previously
     * defined methods for access codes & tokens.
     * Essentially, you could say that this method represents the steps in an Auth code flow before
     * making resource requests to Spotify.
     *
     * @return Whether or not authorization was successful
     */
    public boolean get_authorization() {
        getAccessCode();
        System.out.println("code received");
        HttpResponse<String> response = getTokens();
        if (response != null) {
            access_tokens = JsonParser.parseString(response.body()).getAsJsonObject();
        } else return false;
        return true;
    }
}
