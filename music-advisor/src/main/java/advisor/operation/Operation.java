package advisor.operation;

import advisor.API;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Abstract class representing an operation that user can do */
public abstract class Operation {
    protected int page, pages;
    protected JsonObject error_message = JsonParser.parseString("{\"error\":{\"message\":\"No more pages.\"}}").getAsJsonObject();
    protected API api;


    /** Operation constructor */
    public Operation(API api) {
        this.api = api;
        page = 0;
        pages = 0;
    }

    /**
     * Helper method to format operation data into output (w.r.t the specific operation)
     *
     * @param obj - Json data resulting from operation (usually called after getting result from execute())
     * @return Resultant string of data, extracting important info and formatting it for user display
     */
    public abstract String format(JsonObject obj);

    /**
     * Main operational method for object, gets called to do a specific command.
     * This method serves as a middle layer as it abstracts the API from the UI
     *
     * @return Json object result from API call
     */
    public abstract JsonObject execute();
}
