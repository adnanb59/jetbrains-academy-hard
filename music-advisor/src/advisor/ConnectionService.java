package advisor;

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

public class ConnectionService {
    public final String access_url, resource_url;
    private final Properties props;
    private String code;
    private HttpServer server;

    public ConnectionService(String access, String resource, Properties p) {
        access_url = (access.lastIndexOf(47) == access.length()-1) ? access.substring(0, access.length()-1) : access;
        resource_url = (resource.lastIndexOf(47) == resource.length()-1) ? resource.substring(0, resource.length()-1) : resource;
        this.props = p;
        this.code = null;
        create_authorize_server();
    }

    public boolean isAuthorized() {
        return code != null;
    }

    private void create_authorize_server() {
        try {
            String error = "Authorization code not found. Try again.",
                    success = "Got the code. Return back to your program.";
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            ConnectionService t = this;
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange ex) throws IOException {
                    String query = ex.getRequestURI().getQuery();
                    System.out.println(ex.getRequestURI().getQuery());
                    System.out.println(ex.getRequestURI().toString());
                    if (query != null && query.contains("code=")) {
                        t.code = query.split("[&=]")[1];
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

    private void getAccessCode() {
        System.out.println("use this link to request the access code:");
        System.out.println(access_url + "/authorize?client_id=" + props.getProperty("client_id") + "&redirect_uri=http://localhost:8080&response_type=code");
        server.start();
        System.out.println("waiting for code...");
        while (code == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private HttpResponse<String> getTokens() {
        try {
            HttpClient cli = HttpClient.newBuilder().build();
            HttpRequest access_req = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", Base64.getEncoder().encodeToString((props.getProperty("client_id") + ":" + props.getProperty("client_secret")).getBytes()))
                    .uri(URI.create(access_url + "/api/token"))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:8080"))
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
        //System.out.println("response:");
        //System.out.println(response != null ? response.body() : "");
    }
}