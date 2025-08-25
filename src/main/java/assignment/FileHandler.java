package assignment;

import java.util.*;
import javiergs.tulip.GitHubHandler;
import javiergs.tulip.URLHelper;
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

    public void fetchFromGithub(String repoUrl, FilePathFilter filter) throws Exception {
        URLHelper uh = URLHelper.parseGitHubUrl(repoUrl);
        String token = TokenHelper.getToken();
        GitHubHandler gh = (token == null || token.isBlank())
            ? new GitHubHandler()
            : new GitHubHandler(token);

        List<FileInfo> infos = new ArrayList<>();
        Map<String, String> sources = new LinkedHashMap<>();

        if (uh.isBlob) {
            String path = (uh.path == null) ? "" : uh.path;
            if(filter.accept(path)) {
                String content = gh.getFileContent(uh.owner, uh.repo, path, uh.ref);
                addFile(path, content, infos, sources);
            }
        } else {
            String folder = (uh.kind.equals("root")) ? "" :
                            (uh.path == null ? "" : uh.path);
            List<String> files = gh.listFiles(uh.owner, uh.repo, folder, uh.ref);
            for(String p : files) {
                if (p.endsWith("/")) continue;
                if (!filter.accept(p)) continue;
                String content = gh.getFileContent(uh.owner, uh.repo, p, uh.ref);
                addFile(p, content, infos, sources);
            }
        }
        bb.setFiles(infos);
        parser.parseAll(sources);
    }

    private static void addFile(String path, String content, List<FileInfo> infos,
                                Map<String, String> sources) {
        String name = simpleName(path);
        int lines = 1 + (int) content.chars().filter(ch -> ch == '\n').count();
        infos.add(new FileInfo(name, path, lines));
        sources.put(name, content);
    }

    private static String simpleName(String path) {
        int i = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return (i >= 0 && i + 1 < path.length()) ? path.substring(i + 1) : path;
    }
}
