package assignment;

import java.util.*;
import javiergs.tulip.GitHubHandler;
import javiergs.tulip.URLHelper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Utility class for handling file operations and directory management.
 * Fetches file and folder information from GitHub repositories, reads file
 * contents,
 * and populates the Blackboard with file and source data for further analysis.
 * Serves as the bridge between GitHub and the application's data model.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class FileHandler {
    private final Blackboard bb;
    private final Parser parser;
    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    public FileHandler(Blackboard bb) {
        this.bb = bb;
        this.parser = new Parser(bb);
    }

    public void fetchFromGithub(String repoUrl) throws Exception {
        fetchFromGithub(repoUrl, p -> true);
    }

    public void fetchFromGithub(String repoUrl, FilePathFilter filter) throws Exception {
        long t0 = System.nanoTime();
        logger.info("Fetching from Github: {}", repoUrl);
        URLHelper uh = URLHelper.parseGitHubUrl(repoUrl);
        String token = TokenHelper.getToken();
        GitHubHandler gh = (token == null || token.isBlank())
                ? new GitHubHandler()
                : new GitHubHandler(token);

        List<FileInfo> infos = new ArrayList<>();
        Map<String, String> sources = new LinkedHashMap<>();

        if (uh.isBlob) {
            String path = (uh.path == null) ? "" : uh.path;
            if (filter.accept(path)) {
                String content = gh.getFileContent(uh.owner, uh.repo, path, uh.ref);
                addFile(path, content, infos, sources);
            }
        } else {
            String folder = (uh.kind.equals("root")) ? "" : (uh.path == null ? "" : uh.path);
            List<String> files = gh.listFiles(uh.owner, uh.repo, folder, uh.ref);
            for (String p : files) {
                if (p.endsWith("/"))
                    continue;
                if (!filter.accept(p))
                    continue;
                String content = gh.getFileContent(uh.owner, uh.repo, p, uh.ref);
                addFile(p, content, infos, sources);
            }
            List<String> folders = gh.listFolders(uh.owner, uh.repo, uh.ref, folder);
            for (String f : folders) {
                String name = simpleName(f);
                infos.add(new FileInfo(name, f, 0));
            }
        }
        bb.setFiles(infos);
        parser.parseAll(sources);
        logger.info("fetch complete ({} ms)", (System.nanoTime() - t0) / 1_000_000);
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
