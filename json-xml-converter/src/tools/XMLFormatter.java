package converter.tools;

import converter.models.JSONObject;
import converter.models.XMLElement;

import java.util.*;
import java.util.regex.Matcher;

public class XMLFormatter implements Formatter {

    @Override
    public String convert(String content) {
        JSONObject root = new JSONObject();
        Matcher m = XMLElement.element_pattern.matcher(content);
        if (m.find()) {
            root.setKey(m.group(1));
            if (m.group(2) != null) {
                Matcher n = XMLElement.attribute_pattern.matcher(m.group(2));
                while (n.find()) {
                    root.addProperty("@" + n.group(1), n.group(2));
                }
            }
            if (root.hasNoProperties()) root.setValue(m.group(3).startsWith("/") ? null : m.group(4));
            else root.addProperty("#" + m.group(1), m.group(3).startsWith("/") ? null : m.group(4));
        }
        return root.toString();
    }

    @Override
    public String process(String content) {
        List<String> container = new ArrayList<>();
        container.add(content);
        List<String> path = new ArrayList<>();
        Map<Integer, Integer> pathPortions = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        // Go through elements until stack empty
        while (!container.isEmpty()) {
            // remove the last element from the container
            int removalPoint = container.size() - 1;
            String curr = container.remove(removalPoint);
            Matcher m = XMLElement.element_pattern.matcher(curr);
            // Parse XML Element for components
            if (m.matches()) {
                sb.append("Element:\n");
                // Print path of current element
                sb.append("path = ");
                for (String p : path) sb.append(p);
                sb.append(m.group(1) + "\n");

                // If there is a value in the element, check for nested elements or value
                if (!m.group(3).equals("/>")) {
                    int children = 0;
                    Matcher o = XMLElement.element_pattern.matcher(m.group(4));
                    // If there are nested elements, add them to the container at the last point of removal
                    while (o.find()) {
                        container.add(removalPoint, o.group());
                        children++;
                    }
                    // If there were no nested elements, print the value
                    if (children == 0) {
                        sb.append("value = " + "\"" + m.group(4) + "\"\n");
                    } else {
                        // Otherwise, add parent to path and update level of ancestry
                        path.add(m.group(1) + ", ");
                        if (pathPortions.containsKey(removalPoint)) {
                            pathPortions.computeIfPresent(removalPoint, (k, v) -> v + 1);
                        } else {
                            pathPortions.put(removalPoint, 1);
                        }
                    }
                } else {
                    sb.append("value = " + null + "\n");
                }

                // If there are attributes in the element, print them
                if (m.group(2) != null) {
                    sb.append("attributes:\n");
                    Matcher n = XMLElement.attribute_pattern.matcher(m.group(2));
                    while (n.find()) {
                        sb.append(n.group(1) + " = " + "\"" + n.group(2) + "\"\n");
                    }
                }

                // Remove parents from path if element has been processed
                if (pathPortions.containsKey(container.size())) {
                    int count = pathPortions.remove(container.size());
                    for (int i = 0; i < count; i++) path.remove(path.size()-1);
                }

                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
