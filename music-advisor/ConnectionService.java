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
    private HttpClient cli;
    private JsonObject cached_categories;
    private int server_port;

    public ConnectionService(String access, String resource, Properties p) {
        access_url = (access.lastIndexOf(47) == access.length()-1) ? access.substring(0, access.length()-1) : access;
        resource_url = (resource.lastIndexOf(47) == resource.length()-1) ? resource.substring(0, resource.length()-1) : resource;
        props = p;
        access_code = null;
        access_tokens = null;
        cached_categories = null;
        server_port = (new Random()).nextInt(6000) + 8000;
        cli = HttpClient.newBuilder().build();
        create_authorize_server();
    }

    private void create_authorize_server() {
        try {
            String error = "Authorization code not found. Try again.",
                    success = "Got the code. Return back to your program.";
            server = HttpServer.create();
            server.bind(new InetSocketAddress(server_port), 0);
            ConnectionService t = this;
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange ex) throws IOException {
                    String query = ex.getRequestURI().getQuery();
                    //System.out.println(ex.getRequestURI().getQuery());
                    //System.out.println(ex.getRequestURI().toString());
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
            return null;
        }
    }

    private void refreshTokens() {
        //access_tokens.addProp
        HttpRequest req =  HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", Base64.getEncoder().encodeToString((props.getProperty("client_id") + ":" + props.getProperty("client_secret")).getBytes()))
                .uri(URI.create(access_url + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token&refresh_token=" + access_tokens.get("refresh_token").getAsString()))
                .build();
        try {
            HttpResponse<String> res = cli.send(req, HttpResponse.BodyHandlers.ofString());
            JsonObject fresh_token = JsonParser.parseString(res.body()).getAsJsonObject();
            access_tokens.addProperty("access_token", fresh_token.get("access_token").getAsString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthorized() {
        return access_code != null;
    }

    public JsonObject getNewReleases() {
        return get("/new-releases");
    }

    public JsonObject getFeaturedPlaylists() {
        return get("/featured-playlists");
    }

    public JsonObject getCategories() {
        if (cached_categories == null) {
            JsonObject res = get("/categories");
            if (res != null && res.get("error") != null) {
                cached_categories = res;
            }
            else return res;
        }
        return cached_categories;
    }

    public JsonObject getPlaylistsFromCategory(String c_name) {
        JsonObject res = cached_categories;
        if (res == null) {
            res = getCategories();
            if (res == null || res.get("error") != null) return res;
        }
        System.out.println(res);
        for (JsonElement o : res.getAsJsonObject("categories").getAsJsonArray("items")) {
            if (c_name.trim().equalsIgnoreCase(o.getAsJsonObject().get("name").getAsString())) {
                return get("/categories/" + o.getAsJsonObject().get("id").getAsString() + "/playlists");
            }
        }

        return JsonParser.parseString("{\"error\":\"Unknown category name.\"}").getAsJsonObject();
    }

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

    public void get_authorization() {
        getAccessCode();
        System.out.println("code received");
        HttpResponse<String> response = getTokens();
        if (response != null) access_tokens = JsonParser.parseString(response.body()).getAsJsonObject();
        //System.out.println("response:");
        //System.out.println(response != null ? response.body() : "");
    }
}
