package advisor.operation;

import com.google.gson.JsonObject;

/** Interface for a Pageable Operation */
public interface Pageable {

    /**
     * Get previous page of results for a given operation
     *
     * @return Json containing page of data (or error if there was an issue)
     */
    JsonObject getPrevious();

    /**
     * Get next page of results for an operation
     *
     * @return Json containing page of data (or error on issue)
     */
    JsonObject getNext();
}
