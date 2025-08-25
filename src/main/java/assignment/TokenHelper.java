package assignment;

import java.nio.file.*;
import java.io.InputStream;
import java.util.Properties;

public final class TokenHelper {
    private TokenHelper() {}

    public static String getToken() {
        String t = firstNonBlank(System.getenv("GITHUB_TOKEN"), System.getenv("token"));
        if (t != null) return t.trim();

        Path p = Paths.get(".config", "local.properties");
        if (Files.isRegularFile(p)) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(p)) {
                props.load(in);
                t = firstNonBlank(
                        props.getProperty("token"),
                        props.getProperty("GITHUB_TOKEN"),
                        props.getProperty("github.token"));
                if (t != null && !t.isBlank()) return t.trim();
            } catch (Exception ignore) {}
        }
        return null;
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
