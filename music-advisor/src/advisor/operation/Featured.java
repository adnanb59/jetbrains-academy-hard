package advisor.operation;

import advisor.API;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Featured extends Operation implements Pageable {
    public Featured(API api) {
        super(api);
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

    @Override
    public JsonObject execute() {
        JsonObject o = api.getFeatured((page=0));
        if (o.get("error") == null) pages = o.get("pages").getAsInt();
        return o;
    }


    @Override
    public JsonObject getPrevious() {
        if (page == 0) return error_message;
        return api.getFeatured(--page);
    }

    @Override
    public JsonObject getNext() {
        if (page+1 == pages) return error_message;
        return api.getFeatured(++page);
    }

}
