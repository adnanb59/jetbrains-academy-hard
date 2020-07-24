package converter.tools;

import converter.models.JSONObject;
import converter.models.XMLElement;
import converter.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class JSONFormatter implements Formatter {

    @Override
    public String convert(String content) {
        XMLElement root = new XMLElement();
        Matcher m = JSONObject.object_pattern.matcher(content);
        if (m.find()) {
            root.setTag(m.group(1));
            if (m.group(2).startsWith("\"")) root.setContent(m.group(2));
            else {
                Matcher n = JSONObject.keyVal_pattern.matcher(m.group(2));
                while (n.find()) {
                    if (n.group(1).equals("@")) root.addAttribute(n.group(2), n.group(4) == null ? n.group(3) : n.group(4));
                    else if (n.group(1).equals("#")) {
                        String res = n.group(4) == null ? (n.group(3).equals("null") ? null : n.group(3)) : n.group(4);
                        root.setContent(res);
                    }
                }
            }
        }
        return root.toString();
    }

    @Override
    public String process(String content) {
        StringBuilder sb = new StringBuilder();
        List<Pair<Integer>> deque = new ArrayList<>();
        deque.add(new Pair<>(0, content.length()));
        List<String> path = new ArrayList<>();
        Map<Integer, Integer> pathPortions = new HashMap<>();

        while (!deque.isEmpty()) {
            int removalPoint = deque.size() - 1;
            Pair<Integer> curr = deque.remove(removalPoint);
            Matcher findObject = JSONObject.object_pattern.matcher(content.substring(curr.getFrom(), curr.getTo()));
            if (findObject.matches()) {
                sb.append("Element:\n");
                sb.append("path = ");
                for (String p : path) sb.append(p);
                sb.append(findObject.group(1) + "\n");
                if (findObject.group(2).startsWith("{")) {

                } else {
                    sb.append("value = " + (findObject.group(2).equals("null") ? null : "\"" + findObject.group(2) + "\""));
                }
            }
        }
        return sb.toString();
    }
}
