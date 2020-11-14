package advisor.operation;

import advisor.API;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Playlists extends Operation implements Pageable {
    private String playlist, playlist_id;
    private final JsonObject empty_message = JsonParser
            .parseString("{\"error\":{\"message\":\"No category specified.\"}}").getAsJsonObject();

    public Playlists(API api) {
        super(api);
        playlist = null;
        playlist_id = null;
    }

    @Override
    public String format(JsonObject obj) {
        if (obj.get("error") != null) return obj.getAsJsonObject("error").get("message").getAsString();
        StringBuilder sb = new StringBuilder();
        for (JsonElement e : obj.getAsJsonArray("data")) {
            sb.append(e.getAsJsonObject().get("name").getAsString()).append("\n");
            sb.append(e.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString()).append("\n\n");
        }
        sb.append(String.format("---PAGE %d OF %d---\n", page+1, pages));
        return sb.toString();
    }

    public void setPlaylist(String c_name) {
        playlist = c_name;
    }

    @Override
    public JsonObject execute() {
        if (playlist == null) return empty_message;
        JsonObject result = api.getCategoryPlaylists(playlist, (page=0), false);
        if (result.get("error") == null) {
            // Get playlist id from result, so you don't need to search through categories again for id
            pages = result.get("pages").getAsInt();
            playlist_id = result.get("id").getAsString();
        } else {
            playlist = null;
            playlist_id = null;
        }
        return result;
    }

    @Override
    public JsonObject getPrevious() {
        if (playlist == null || playlist_id == null) return empty_message;
        else if (this.page == 0) return error_message;
        else return api.getCategoryPlaylists(playlist_id, --page, true);
    }

    @Override
    public JsonObject getNext() {
        if (playlist == null || playlist_id == null) return empty_message;
        else if (this.page + 1 == pages) return error_message;
        else return api.getCategoryPlaylists(playlist_id, ++page, true);
    }
}
