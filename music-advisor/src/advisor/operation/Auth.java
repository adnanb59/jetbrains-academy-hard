package advisor.operation;

import advisor.API;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Auth extends Operation {
    public Auth(API api) {
        super(api);
    }

    @Override
    public String format(JsonObject obj) {
        return obj.get("error") == null ? "---SUCCESS---" : "---FAILURE---";
    }

    @Override
    public JsonObject execute() {
        if (!api.authorize()) return JsonParser.parseString("{\"error\":{\"message\": \"Auth failed\"}}").getAsJsonObject();
        else return JsonParser.parseString("{\"success\":{\"message\": \"Auth successful\"}}").getAsJsonObject();
    }
}
