package assignment;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for normalizing and resolving names.
 * Provides methods to extract base filenames, sanitize names for PlantUML,
 * and convert type names to simple forms for analysis and display.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class Names {
    private static final Logger logger = LoggerFactory.getLogger(Names.class);

    private Names() {
    }

    public static String resolveLabel(Map<String, String> map, String raw) {
        if (raw == null || raw.isEmpty()) {
            logger.trace("resolveLabel: raw is null/empty");
            return null;
        }
        String s1 = map.get(raw);
        if (s1 != null) {
            logger.debug("resolveLabel: matched raw key '{}'", raw);
            return s1;
        }
        String key2 = sanitize(raw);
        String s2 = map.get(key2);
        if (s2 != null) {
            logger.debug("resolveLabel: mathced sanitize key '{}'", key2);
            return s2;
        }
        String key3 = sanitize(baseName(raw));
        String s3 = map.get(key3);
        if (s3 != null) {
            logger.debug("resolveLabel: mathced-basename key '{}'", key3);
        }
        return s3;
    }

    public static String sanitize(String s) {
        String out = s.replaceAll("[^A-Za-z0-9]", "_");
        if (!out.isEmpty() && Character.isDigit(out.charAt(0)))
            out = "_" + out;
        return out.isEmpty() ? "Class_" + UUID.randomUUID().toString().replace("-", "") : out;
    }

    public static String uniquify(Set<String> used, String candidate) {
        String name = candidate;
        int n = 2;
        while (!used.add(name))
            name = candidate + "_" + n++;
        logger.debug("uniquify: '{}' -> '{}'", candidate, name);
        return name;
    }

    public static String baseName(String fileName) {
        if (fileName == null || fileName.isEmpty())
            return "";
        String path = fileName.replace('\\', '/');
        while (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        int slash = path.lastIndexOf('/');
        String name = (slash >= 0) ? path.substring(slash + 1) : path;

        int dot = name.lastIndexOf('.');
        if (dot > 0)
            name = name.substring(0, dot);
        logger.trace("baseName: '{}' -> '{}'", fileName, name);
        return name;
    }

    public static String simpleType(String typeName) {
        if (typeName == null || typeName.isEmpty())
            return "";
        String s = typeName;
        int lt = s.indexOf('<');
        if (lt >= 0)
            s = s.substring(0, lt);
        while (s.endsWith("]"))
            s = s.substring(0, s.length() - 2);
        int dot = s.lastIndexOf('.');
        if (dot >= 0 && dot + 1 < s.length())
            s = s.substring(dot + 1);
        logger.trace("simpleType: '{}' -> '{}'", typeName, s);
        return s;
    }
}
