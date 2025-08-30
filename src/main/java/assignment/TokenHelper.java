package assignment;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for retrieving a GitHub API token from environment variables or
 * a local properties file.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public final class TokenHelper {
    private TokenHelper() {
    }

    public static String getToken() {
        String t = System.getenv("token");
        if (t != null && !t.isBlank())
            return t.trim();

        try (InputStream in = TokenHelper.class.getClassLoader()
                .getResourceAsStream("local.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                t = props.getProperty("token");
                if (t != null && !t.isBlank())
                    return t.trim();
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}
