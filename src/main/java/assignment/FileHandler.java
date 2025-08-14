package assignment;

import java.util.ArrayList;
import java.io.*;
import javiergs.tulip.GitHubHandler;

/**
 * Utility class for handling file operations and directory management.
 * Provides static methods for reading file names from directories, choosing
 * folders
 * through a GUI file chooser, and performing file I/O operations. This class
 * serves as the bridge between the file system and the grid board application,
 * enabling users to select and process files for display.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 3.0
 */

public class FileHandler {
    public static ArrayList<String> getFileNames(String folderPath) {
        ArrayList<String> names = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    names.add(f.getAbsolutePath());
                }
            }
        }
        return names;
    }

    public static ArrayList<Square> fetchFromGithub(String repoUrl) throws IOException {
        ArrayList<Square> out = new ArrayList<>();

        String cleaned = repoUrl.trim();
        if (cleaned.endsWith(".git")){
            cleaned = cleaned.substring(0, cleaned.length()- 4);
        }

        String[] parts = cleaned.split("/");
        if(parts.length < 5) {
            return out;
        }
        String owner = parts[3];
        String repo = parts[4];

        String folderPath = extractFolderPathFromUrl(cleaned);
        folderPath = normalizeFolder(folderPath);

        GitHubHandler gh = new GitHubHandler(owner, repo);
        if (folderPath == null) {
            folderPath = "";
        }
        listRecursively(gh, folderPath, out);

        System.out.println("Scanned files: " + out.size() + " from folder " + folderPath);
        return out;
    }

    private static void listRecursively(GitHubHandler gh, String folder, ArrayList<Square> out) throws IOException{
        var entries = gh.listFiles(folder);
        for (String path : entries) {
            if(path.endsWith("/")) {
                listRecursively(gh, path.substring(0, path.length() - 1), out);
            } else {
                String content = gh.getFileContent(path);
                int lines = countLines(content);
                out.add(new Square(path, lines));
            }
        }
    }

    private static String normalizeFolder(String p) {
        if (p == null) {
            return "";
        }
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        while(p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    private static String extractFolderPathFromUrl(String url) {
        int idx = url.indexOf("/tree/");
        if (idx != -1) {
            String tail = url.substring(idx + 6);
            int slash = tail.indexOf('/');
            if(slash != -1) {
                return tail.substring(slash + 1) + "/";
            }
        }
        return null;
    }

    private static int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int lines = 1;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '\n') {
                lines ++;
            }
        }
        return lines;
    }
}
