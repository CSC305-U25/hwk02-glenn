package assignment;

import java.io.InputStream;
import java.util.Properties;

public final class TokenHelper {
    private TokenHelper() {}

    public static String getToken() {
        String t = System.getenv("token");
        if (t != null && !t.isBlank()) return t.trim();

        try (InputStream in = TokenHelper.class.getClassLoader()
                                               .getResourceAsStream("local.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                t = props.getProperty("token");
                if (t != null && !t.isBlank()) return t.trim();
            }
        } catch (Exception ignore) {}
        return null;
    }
}
