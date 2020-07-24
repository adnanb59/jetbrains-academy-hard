package converter.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JSONObject {
    private String key, value;
    private Map<String, String> properties;
    public static final Pattern object_pattern = Pattern.compile("\\{\\s*\"(\\S+)\"\\s*:\\s*(.*)\\s*}");
    public static final Pattern keyVal_pattern = Pattern.compile("\"(@|#)(\\S+)\"\\s*:\\s*(\"(.*?\\S.*?)\"|null|[0-9]+)(,?)");

    public JSONObject() {
        key = null;
        value = null;
        properties = new LinkedHashMap<>();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addProperty(String k, String v) {
        properties.put(k, v);
    }

    public boolean hasNoProperties() {
        return properties.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n\t");

        sb.append(String.format("\"%s\": ", key));
        if (value != null) sb.append(String.format("\"%s\"", value));
        else {
            sb.append("{");
            int i = 0;
            for (Map.Entry<String, String> p : properties.entrySet()) {
                sb.append(String.format("\"%s\": ", p.getKey()));
                sb.append((p.getValue() == null ? "null" : String.format("\"%s\"", p.getValue())));
                if (i != properties.size()-1) sb.append(",");
                i++;
            }
            sb.append("}");
        }

        sb.append("}");
        return sb.toString();
    }
}
