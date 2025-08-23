package assignment;

import java.util.ArrayList;
import java.util.*;
import java.io.IOException;

import javiergs.tulip.GitHubHandler;

/**
 * Utility class for handling file operations and directory management.
 * Provides static methods for reading file names and line counts from GitHub
 * repositories,
 * normalizing folder paths, and recursively listing files for display in the
 * grid board.
 * Serves as the bridge between the file system (specifically GitHub) and the
 * grid board application.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class FileHandler {
    private final Blackboard bb;
    private final Parser parser;

    public FileHandler(Blackboard bb) {
        this.bb = bb;
        this.parser = new Parser(bb);
    }

    public void fetchFromGithub(String repoUrl) throws Exception {
        String cleaned = repoUrl.trim();
        if (cleaned.endsWith(".git")) {
            cleaned = cleaned.substring(0, cleaned.length() - 4);
        }

        String[] parts = cleaned.split("/");
        if (parts.length < 5)
            throw new IllegalArgumentException("Bad Github URL: " + repoUrl);
        String owner = parts[3];
        String repo = parts[4];

        String folderPath = extractFolderPathFromUrl(cleaned);
        folderPath = normalizeFolder(folderPath);

        loadFromGithub(owner, repo, folderPath);
    }

    public void loadFromGithub(String owner, String repo, String path) throws Exception {
        GitHubHandler gh = new GitHubHandler(owner, repo);
        List<FileInfo> infos = new ArrayList<>();
        Map<String, String> sources = new LinkedHashMap<>();

        listRecursively(gh, (path == null ? "" : path), infos, sources);
        bb.setFiles(infos);
        parser.parseAll(sources);
    }

    private static void listRecursively(GitHubHandler gh,
            String folder,
            List<FileInfo> infos,
            Map<String, String> sources) throws IOException {
        if (folder == null)
            folder = "";
        var entries = gh.listFiles(folder);
        for (String path : entries) {
            if (path.endsWith("/")) {
                listRecursively(gh, path.substring(0, path.length() - 1), infos, sources);
                continue;
            }
            if (!path.endsWith(".java"))
                continue;
            String content = gh.getFileContent(path);
            String simple = simpleName(path);
            int lines = countLines(content);

            infos.add(new FileInfo(simple, path, lines));
            sources.put(simple, content);
        }
    }

    private static String normalizeFolder(String p) {
        if (p == null)
            return "";
        while (p.startsWith("/"))
            p = p.substring(1);
        while (p.endsWith("/"))
            p = p.substring(0, p.length() - 1);
        return p;
    }

    private static String extractFolderPathFromUrl(String url) {
        int idx = url.indexOf("/tree/");
        if (idx != -1) {
            String tail = url.substring(idx + 6);
            int slash = tail.indexOf('/');
            if (slash != -1) {
                return tail.substring(slash + 1) + "/";
            }
        }
        return null;
    }

    private static int countLines(String text) {
        if (text == null || text.isEmpty())
            return 0;
        int lines = 1;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '\n')
                lines++;
        }
        return lines;
    }

    private static String simpleName(String path) {
        int i = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return (i >= 0 && i + 1 < path.length()) ? path.substring(i + 1) : path;
    }
}
