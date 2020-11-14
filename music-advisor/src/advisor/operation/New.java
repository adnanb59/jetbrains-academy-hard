package advisor.operation;

import advisor.API;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class New extends Operation implements Pageable {
    public New(API api) {
        super(api);
    }

    @Override
    public String format(JsonObject obj) {
        if (obj.get("error") != null) return obj.getAsJsonObject("error").get("message").getAsString();
        StringBuilder sb = new StringBuilder();
        // Go through new releases and collect title, artists & Spotify link
        for (JsonElement e : obj.getAsJsonArray("data")) {
            JsonObject elem = e.getAsJsonObject();
            sb.append(elem.get("name").getAsString()).append("\n");
            sb.append("[");
            // traverse artists sub-array for artist names
            JsonArray artists = elem.getAsJsonArray("artists");
            for (int i = 0; i < artists.size(); i++) {
                sb.append(artists.get(i).getAsJsonObject().get("name").getAsString());
                if (i != artists.size()-1) sb.append(", ");
            }
            sb.append("]\n");
            sb.append(elem.getAsJsonObject("external_urls").get("spotify").getAsString()).append("\n");
        }
        sb.append(String.format("---PAGE %d OF %d---\n", page+1, pages));
        return sb.toString();
    }

    @Override
    public JsonObject execute() {
        JsonObject res = api.getNewReleases((page=0));
        if (res.get("error") == null) {
            pages = res.get("pages").getAsInt();
        }
        return res;
    }

    @Override
        public JsonObject getPrevious() {
        if (page == 0) return error_message;
        return api.getNewReleases(--page);
    }

    @Override
    public JsonObject getNext() {
        if (page+1 == pages) return error_message;
        return api.getNewReleases(++page);
    }
}
