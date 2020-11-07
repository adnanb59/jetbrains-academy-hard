package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;

public class API {
    private ConnectionService conn;

    public API(ConnectionService conn) {
        this.conn = conn;
    }

    public String getFeatured() {
        JsonObject result = conn.getFeaturedPlaylists();
        if (result == null) return "Error connecting to Spotify API";
        else if (result.get("error") != null) return result.getAsJsonObject("error").get("message").getAsString();
        else {
            JsonArray playlists = result.getAsJsonObject("playlists").getAsJsonArray("items");
            StringBuilder sb = new StringBuilder();
            for (JsonElement p : playlists) {
                sb.append(p.getAsJsonObject().get("name").getAsString()).append("\n");
                sb.append(p.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString()).append("\n\n");
            }
            return sb.toString();
        }
    }

    public String getNewReleases() {
        JsonObject result = conn.getNewReleases();
        if (result == null) return "Error connecting to Spotify API";
        else if (result.get("error") != null) return result.getAsJsonObject("error").get("message").getAsString();
        else {
            JsonArray releases = result.getAsJsonObject("albums").getAsJsonArray("items");
            StringBuilder sb = new StringBuilder();
            Iterator<JsonElement> it = releases.iterator();
            while (it.hasNext()) {
                JsonObject e = it.next().getAsJsonObject();
                sb.append(e.get("name").getAsString()).append("\n");
                sb.append("[");
                JsonArray artists = e.getAsJsonArray("artists");
                for (int i = 0; i < artists.size(); i++) {
                    sb.append(artists.get(i).getAsJsonObject().get("name").getAsString());
                    if (i != artists.size()-1) sb.append(", ");
                }
                sb.append("]\n");
                sb.append(e.getAsJsonObject("external_urls").get("spotify").getAsString()).append("\n");
                if (it.hasNext()) sb.append("\n");
            }
            return sb.toString();
        }
    }

    public String getCategories() {
        JsonObject result = conn.getCategories();
        if (result == null) return "Error connecting to Spotify API";
        else if (result.get("error") != null) return result.getAsJsonObject("error").get("message").getAsString();
        else {
            JsonArray categories = result.getAsJsonObject("categories").getAsJsonArray("items");
            StringBuilder sb = new StringBuilder();
            for (JsonElement c : categories) {
                sb.append(c.getAsJsonObject().get("name")).append("\n");
            }
            return sb.toString();
        }
    }

    public String getCategoryPlaylists(String cat_name) {
        JsonObject result = conn.getPlaylistsFromCategory(cat_name);
        System.out.println(result);
        if (result == null) return "Error connecting to Spotify API";
        else if (result.get("error") != null) return result.getAsJsonObject("error").get("message").getAsString();
        else {
            System.out.println(result.toString());
            JsonArray playlists = result.getAsJsonObject("playlists").getAsJsonArray("items");
            StringBuilder sb = new StringBuilder();
            for (JsonElement p : playlists) {
                sb.append(p.getAsJsonObject().get("name").getAsString()).append("\n");
                sb.append(p.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString()).append("\n");
            }
            return sb.toString();
        }
    }

    public boolean authorize() {
        if (!conn.isAuthorized()) {
            conn.get_authorization();
        }
        return true;
    }
}
