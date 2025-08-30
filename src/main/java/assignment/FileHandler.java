package assignment;

import java.util.*;
import javiergs.tulip.GitHubHandler;
import javiergs.tulip.URLHelper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
                boolean isJava = JavaFilter.isJavaFileName(path);
                String name = Names.baseName(path);
                int lines = 0;
                if (isJava) {
                    String content = gh.getFileContent(uh.owner, uh.repo, path, uh.ref);
                    lines = 1 + (int) content.chars().filter(ch -> ch == '\n').count();
                    sources.put(name,content);
                }
                infos.add(new FileInfo(name, path, lines));
            }
        } else {
            String folder = (uh.kind.equals("root")) ? "" : (uh.path == null ? "" : uh.path);
            for (String path : gh.listFiles(uh.owner, uh.repo, folder, uh.ref)) {
                if (!filter.accept(path)) continue;
                boolean isJava = JavaFilter.isJavaFileName(path);
                String name = Names.baseName(path);
                int lines = 0;
                if(isJava) {
                    String content = gh.getFileContent(uh.owner, uh.repo, path, uh.ref);
                    lines = 1 + (int) content.chars().filter(ch -> ch == '\n').count();
                    sources.put(name,content);
                }
                infos.add(new FileInfo(name, path, lines));
            }
            List<String> folders = gh.listFolders(uh.owner, uh.repo, uh.ref, folder);
            for (String f : folders) {
                String name = Names.baseName(f);
                infos.add(new FileInfo(name, f, 0));
            }
        }
        bb.setFiles(infos);
        parser.parseAll(sources);
        logger.info("fetch complete ({} ms)", (System.nanoTime() - t0)/1_000_000);
    }
}
