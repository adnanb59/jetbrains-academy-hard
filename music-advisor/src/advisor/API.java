package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class API {
    private ConnectionService conn;
    private int limit;

    public API(ConnectionService conn, int limit) {
        this.conn = conn;
        this.limit = limit;
    }

    public JsonObject prune_result(JsonObject result, String category) {
        JsonObject ret = new JsonObject();
        ret.addProperty("pages", (result.getAsJsonObject(category).get("total").getAsInt() / limit) + (result.getAsJsonObject(category).get("total").getAsInt() % limit == 0 ? 0 : 1));
        // TODO: remove a
        JsonArray a = new JsonArray();
        for (int i = result.getAsJsonObject(category).get("offset").getAsInt(); i < result.getAsJsonObject(category).get("offset").getAsInt() + limit; i++) {
            if (i < result.getAsJsonObject(category).get("total").getAsInt())
                a.add(result.getAsJsonObject(category).getAsJsonArray("items").get(i));
        }
        ret.add("data", a);
        return ret;
    }

    public JsonObject getFeatured(int page) {
        JsonObject result = conn.getFeaturedPlaylists(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "playlists");
    }

    public JsonObject getNewReleases(int page) {
        JsonObject result = conn.getNewReleases(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "albums");
    }

    public JsonObject getCategories(int page) {
        JsonObject result = conn.getCategories(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "categories");
    }

    public JsonObject getCategoryPlaylists(String identifier, int page, boolean isId) {
        JsonObject result = isId ? conn.getPlaylistFromId(identifier, page, limit) : conn.getPlaylistsFromCategory(identifier, page, limit);
        if (result.get("error") != null) return result;
        else {
            JsonObject ret = prune_result(result, "playlists");
            ret.addProperty("id", result.get("id").getAsString());

            return ret;
        }
    }

    public boolean authorize() {
        if (!conn.isAuthorized()) {
            return conn.get_authorization();
        }
        return true;
    }
}
