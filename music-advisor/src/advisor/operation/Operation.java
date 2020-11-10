package advisor.operation;

import advisor.API;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class Operation {
    protected int page = 0, pages = 0;
    protected JsonObject error_message = JsonParser.parseString("{\"error\":{\"message\":\"No more pages.\"}}").getAsJsonObject();
    protected API api;

    public Operation(API api) {
        this.api = api;
    }

    public abstract String format(JsonObject obj);

    public abstract JsonObject execute();
}
