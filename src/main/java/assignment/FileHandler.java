package assignment;

import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Scanner;
import javax.swing.*;

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
 * @version 2.0
 */

public class FileHandler {
    private int fileCount = 0;

    public static String[] readFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        ArrayList<String> names = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    names.add(f.getName());
                }
            }
        }

        return names.toArray(String[]::new);
    }

    public void cntFiles(String directoryPath) {
        File dir = new File(directoryPath);
        String[] files = dir.list();

        if (files == null) {
            System.out.println("Directory not found or cannot be read");
            this.fileCount = 0;
            return;
        }
        fileCount = files.length;
        System.out.println("Number of files: " + files.length);
    }

    public int getFileCount() {
        return fileCount;
    }

    public static String chooseFolder() {
        JFileChooser folder = new JFileChooser();
        folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folder.setDialogTitle("Select a Folder");

        int result = folder.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folder.getSelectedFile();
            System.out.println("Selected folder: " + selectedFolder.getAbsolutePath());
            return selectedFolder.getAbsolutePath();
        } else {
            System.out.println("No folder selected.");
            return null;
        }
    }

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
    public static ArrayList<Square> fetch(String input) throws IOException {
        ArrayList<Square> out = new ArrayList<>();
        if (input == null || input.isBlank()) return out;

        if (input.startsWith("file://")) {
            Path root = Paths.get(URI.create(input));
            return scanLocalFolder(root);
        }
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return fetchFromGithub(input);
        }

        Path p = Paths.get(input);
        if (Files.exists(p)) {
            return scanLocalFolder(p);
        }

        return out;
    }

    private static ArrayList<Square> fetchFromGithub(String repoUrl) throws IOException {
        String cleaned = repoUrl.trim();
        if (cleaned.endsWith(".git")) cleaned = cleaned.substring(0, cleaned.length() - 4);

        String[] parts = cleaned.split("/");
        if (parts.length < 5) return new ArrayList<>();
        String owner = parts[3];
        String repo  = parts[4];

        String branch = extractBranchFromUrl(cleaned);

        if (branch == null) branch = queryDefaultBranch(owner, repo);

        if (branch == null) {
            for (String b : new String[]{"main", "master"}) {
                if (urlExists("https://codeload.github.com/" + owner + "/" + repo + "/zip/refs/heads/" + b)) {
                    branch = b; break;
                }
            }
        }
        if (branch == null) {
            throw new FileNotFoundException("Could not determine branch for " + owner + "/" + repo +
                    " (repo may be private or not found).");
        }

        String zipUrl = "https://codeload.github.com/" + owner + "/" + repo + "/zip/refs/heads/" + branch;
        Path tempZip = Files.createTempFile("repo-", ".zip");
        download(zipUrl, tempZip);

        Path tempDir = Files.createTempDirectory("repo-unzip-");
        Path root = unzipFirstFolder(tempZip, tempDir);
        if (root != null) return scanLocalFolder(root);
        return new ArrayList<>();
    }

    private static String extractBranchFromUrl(String url) {
        int i = url.indexOf("/tree/");
        if (i == -1) i = url.indexOf("/blob/");
        if (i != -1) {
            String tail = url.substring(i + 6);
            int slash = tail.indexOf('/');
            return (slash == -1) ? tail : tail.substring(0, slash);
        }
        return null;
    }

    private static String queryDefaultBranch(String owner, String repo) {
        try {
            URL u = new URL("https://api.github.com/repos/" + owner + "/" + repo);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("User-Agent", "FileGridApp/1.0");
            String token = System.getenv("GITHUB_TOKEN");
            if (token != null && !token.isBlank()) c.setRequestProperty("Authorization", "token " + token);
            if (c.getResponseCode() >= 400) return null;
            try (InputStream in = c.getInputStream()) {
                String json = new String(in.readAllBytes());
                int idx = json.indexOf("\"default_branch\"");
                if (idx != -1) {
                    int colon = json.indexOf(':', idx);
                    int q1 = json.indexOf('"', colon + 1);
                    int q2 = json.indexOf('"', q1 + 1);
                    return (q1 != -1 && q2 != -1) ? json.substring(q1 + 1, q2) : null;
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private static boolean urlExists(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("HEAD");
            c.setRequestProperty("User-Agent", "FileGridApp/1.0");
            String token = System.getenv("GITHUB_TOKEN");
            if (token != null && !token.isBlank()) c.setRequestProperty("Authorization", "token " + token);
            int code = c.getResponseCode();
            return code >= 200 && code < 400;
        } catch (IOException e) {
            return false;
        }
    }

    private static void download(String url, Path dest) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", "FileGridApp/1.0");
        String token = System.getenv("GITHUB_TOKEN");
        if (token != null && !token.isBlank()) conn.setRequestProperty("Authorization", "token " + token);

        int code = conn.getResponseCode();
        if (code >= 400) throw new FileNotFoundException(url + " (HTTP " + code + ")");

        try (InputStream in = conn.getInputStream();
            OutputStream out = Files.newOutputStream(dest, StandardOpenOption.TRUNCATE_EXISTING)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
        }
    }

    private static ArrayList<Square> scanLocalFolder(Path root) throws IOException {
        ArrayList<Square> out = new ArrayList<>();
        if (!Files.isDirectory(root)) {
            return out;
        }

        try (var paths = Files.walk(root)) {
           paths.filter(Files::isRegularFile)
                .filter(p -> !p.getFileName().toString().startsWith("."))
                .forEach(p -> {
                    int lines = countLines(p);
                    out.add(new Square(root.relativize(p).toString(), lines));
                });
        }
        System.out.println("Scanned files: " + out.size() + "under" + root);
        return out;
    }

    private static int countLines(Path file) {
        int n = 0;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            while (br.readLine() != null) n++;
        } catch (IOException ignored) {}
        return n;
    }

    private static Path unzipFirstFolder(Path zipFile, Path destDir) throws IOException {
        String top = null;

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                String name = e.getName();
                int slash = name.indexOf('/');
                if(slash > 0) {
                    String first = name.substring(0, slash);
                    if (top == null) {
                        top = first;
                    }
                }

                Path outPath = destDir.resolve(name).normalize();
                if (!outPath.startsWith(destDir)) throw new IOException("Zip traversal attack blocked");
                if (e.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        Path root = (top != null) ? destDir.resolve(top) : destDir;
        return Files.isDirectory(root) ? root : destDir;
    }
}
