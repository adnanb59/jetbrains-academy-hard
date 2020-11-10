package advisor.operation;

import advisor.API;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Categories extends Operation implements Pageable {
    public Categories(API api) {
        super(api);
    }

    @Override
    public String format(JsonObject obj) {
        if (obj.get("error") != null) return obj.getAsJsonObject("error").get("message").getAsString();
        StringBuilder sb = new StringBuilder();
        for (JsonElement e : obj.getAsJsonArray("data")) {
            sb.append(e.getAsJsonObject().get("name").getAsString()).append("\n");
        }
        sb.append(String.format("---PAGE %d OF %d---\n", page+1, pages));
        return sb.toString();
    }

    @Override
    public JsonObject execute() {
        JsonObject res = api.getCategories((page=0));
        if (res.get("error") == null) pages = res.get("pages").getAsInt();
        return res;
    }

    @Override
    public JsonObject getPrevious() {
        if (page == 0) return error_message;
        else return api.getCategories(--page);
    }

    @Override
    public JsonObject getNext() {
        if (page+1 == pages) return error_message;
        else return api.getCategories(++page);
    }
}
