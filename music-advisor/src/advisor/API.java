package advisor;

import com.google.gson.JsonObject;

public class API {
    private final ConnectionService conn;
    private final int limit;

    /**
     * API constructor
     *
     * @param conn - Backend service that connects to Spotify API
     * @param limit - Number of results per page (to query on behalf of user)
     */
    public API(ConnectionService conn, int limit) {
        this.conn = conn;
        this.limit = limit;
    }

    /**
     * Helper method that takes result from Spotify and removes top-level unnecessary info and keeps
     * what will be used by other functions
     *
     * @param result - "Raw" Json data -- data that needs to be pruned
     * @param category - parameter specifying field to get data from
     * @return Pruned data (just the items and # of pages of data that exists)
     */
    public JsonObject prune_result(JsonObject result, String category) {
        JsonObject ret = new JsonObject();
        ret.addProperty("pages", (result.getAsJsonObject(category).get("total").getAsInt() / limit) + (result.getAsJsonObject(category).get("total").getAsInt() % limit == 0 ? 0 : 1));
        ret.add("data", result.getAsJsonObject(category).getAsJsonArray("items"));
        return ret;
    }

    /**
     * Get (Paginated) List of Featured Playlists from Spotify
     *
     * @param page - Page of data to get (w.r.t to limit specified earlier)
     * @return Json object containing results (or error object if error)
     */
    public JsonObject getFeatured(int page) {
        JsonObject result = conn.getFeaturedPlaylists(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "playlists");
    }

    /**
     * Get (Paginated) list of New Releases from Spotify
     *
     * @param page - Page of data to get (w.r.t to limit specified earlier)
     * @return Json object containing results (or error object if error)
     */
    public JsonObject getNewReleases(int page) {
        JsonObject result = conn.getNewReleases(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "albums");
    }

    /**
     * Get (Paginated) list of Categories from Spotify
     *
     * @param page - Page of data to get (w.r.t to limit specified earlier)
     * @return Json object containing results (or error object if error)
     */
    public JsonObject getCategories(int page) {
        JsonObject result = conn.getCategories(page, limit);
        if (result.get("error") != null) return result;
        else return prune_result(result, "categories");
    }

    /**
     * Get (Paginated) list of Playlists for a given category from Spotify
     *
     * @param identifier - Depending on _isId_, either category name or playlist id
     * @param page - Page of data to get (w.r.t to limit specified earlier)
     * @param isId - Whether or not the playlist id is being passed in
     * @return Json object containing results (or error object if error)
     */
    public JsonObject getCategoryPlaylists(String identifier, int page, boolean isId) {
        // Either get playlists from function that uses playlist id (based on flag set) or from function that uses category name
        // The latter gets used first, as id is unknown at that point
        JsonObject result = isId ? conn.getPlaylistFromId(identifier, page, limit) : conn.getPlaylistsFromCategory(identifier, page, limit);
        if (result.get("error") != null) return result;
        else {
            // grab id from the original result and store in return object
            JsonObject ret = prune_result(result, "playlists");
            ret.addProperty("id", result.get("id").getAsString());
            return ret;
        }
    }

    /**
     * Attempt to authorize user with Spotify (to be able to run commands)
     *
     * @return Whether or not authorization was successful
     */
    public boolean authorize() {
        if (!conn.isAuthorized()) {
            return conn.get_authorization();
        }
        return true;
    }
}
