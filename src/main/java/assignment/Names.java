package assignment;

import java.util.*;

public class Names {
    private Names(){}

    public static String resolveLabel(Map<String, String> map, String raw) {
        if(raw == null || raw.isEmpty()) return null;
        String s1 = map.get(raw);
        if (s1 != null) return s1;
        String s2 = map.get(sanitize(raw));
        if(s2 != null) return s2;
        String s3 = map.get(sanitize(baseName(raw)));
        return s3;
    }

    public static String sanitize(String s) {
        String out = s.replaceAll("[^A-Za-z0-9]", "_");
        if(!out.isEmpty() && Character.isDigit(out.charAt(0))) out = "_" + out;
        return out.isEmpty() ? "Class_" + UUID.randomUUID().toString().replace("-", "") : out;
    }

    public static String uniquify(Set<String> used, String candidate) {
        String name = candidate;
        int n = 2;
        while(!used.add(name)) name = candidate + "_" + n++;
        return name;
    }

    public static String baseName(String fileName) {
        String name = fileName;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if(slash >= 0) name = name.substring(slash + 1);
        int dot = name.lastIndexOf('.');
        if (dot > 0) name = name.substring(0, dot);
        return name;
    }
}
