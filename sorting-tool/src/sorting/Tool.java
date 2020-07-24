package sorting;

import java.util.*;
import sorting.util.*;

public class Tool {
    private ToolType tt;
    private SortType st;

    /** Tool constructor
     *
     * @param tt - ToolType (the type of data stored in the tool)
     * @param st - SortType (how the data stored will be sorted by the tool)
     */
    public Tool(ToolType tt, SortType st) {
        this.tt = tt;
        this.st = st;
    }

    /** Tool's main function, which completes the sorting action (based on SortType) and
    *  returns a formatted string result.
    *
    * @param lst - User data to be sorted by the tool
    * @param <T> - Type of data in the collection to be sorted
    * @return Formatted string with the result of the sorting action
    */
    public <T extends Comparable<T>> String act(List<T> lst) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total %ss: %d.\n", tt.toString(), lst.size()));

        // If you're sorting by appearance count, maintain a map consisting of entry counts
        if (st == SortType.BY_COUNT) {
            Map<T, Integer> entry_counts = new LinkedHashMap<>();
            for (T k : lst) {
                if (entry_counts.containsKey(k)) {
                    entry_counts.put(k, entry_counts.get(k) + 1);
                } else {
                    entry_counts.put(k, 1);
                }
            }
            Collections.sort(lst); // For auto-marker, sort entry data before sorting map
            lst.sort(Comparator.comparingInt(entry_counts::get)); // Sort entries by appearance count
            sb.append(byCountRepr(entry_counts, lst));
        } else {
            // For natural sort, line is sorted by length while word/long is sorted by value
            if (tt != ToolType.LINE) Collections.sort(lst);
            else lst.sort(Comparator.comparingInt(e -> e.toString().length()));
            sb.append(naturalRepr(lst));
        }

        return sb.toString().trim();
    }

    /** Get the string representation of data after sorting by appearance count
    *
    * @param counts - map of data entries and how much they appeared in the collection
    * @param entries - data in the collection
    * @param <T> - Type of data in the collection to be sorted
    * @return Formatted string displaying data elements and how much they appear in the coll'n
    */
    private <T> String byCountRepr(Map<T, Integer> counts, List<T> entries) {
        StringBuilder sb = new StringBuilder();
        int size = entries.size();
        entries.stream().distinct().forEach(e -> {
            int curr = counts.get(e);
            sb.append(String.format("%s: %d time(s), %d%%\n", e, curr, Math.round(curr*100.0/size)));
        });
        return sb.toString().trim();
    }

    /** Get the string representation of data after sorting "naturally"
    *
    * @param entries - data in the collection
    * @param <T> - Type of data in the collection to be sorted
    * @return Formatted string displaying data elements after being sorted
    */
    private <T> String naturalRepr(List<T> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sorted Data:");
        for (T entry : entries) {
            sb.append(tt == ToolType.LINE ? "\n" : " ").append(entry);
        }
        return sb.toString().trim();
    }
}