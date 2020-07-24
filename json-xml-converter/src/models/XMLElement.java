package converter.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class XMLElement {
    private String tag;
    private Map<String, String> attributes;
    private String content;
    private List<XMLElement> children;
    public static final Pattern attribute_pattern = Pattern.compile("(\\S+)\\s*=\\s*\"(.*?)\"");
    public static final Pattern element_pattern = Pattern.compile("<\\s*([a-zA-Z]+)\\s*(\\S+\\s*=\\s*\".*?\")*\\s*(\\/>|>(.*?)<\\/\\1>)");

    public XMLElement() {
        tag = "";
        content = null;
        attributes = new LinkedHashMap<>();
        children = new ArrayList<>();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addAttribute(String k, String v) {
        this.attributes.put(k, v);
    }

    /*public void addChild(XMLElement x) {
        this.children.add(x);
    }*/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<" + tag);
        if (!attributes.isEmpty()) {
            for (Map.Entry<String, String> a : attributes.entrySet()) {
                sb.append(" ").append(a.getKey()).append("=").append(String.format("\"%s\"", a.getValue()));
            }
        }
        if (content == null && children.size() == 0) sb.append("/>");
        else {
            sb.append(">");
            if (content != null) sb.append(content);
            else {
                for (XMLElement c : children) {
                    sb.append(c.toString());
                }
            }
            sb.append("</").append(tag).append(">");
        }
        return sb.toString();
    }
}
