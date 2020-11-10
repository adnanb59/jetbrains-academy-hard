package advisor.operation;

import com.google.gson.JsonObject;

public interface Pageable {
    public JsonObject getPrevious();
    public JsonObject getNext();
}
