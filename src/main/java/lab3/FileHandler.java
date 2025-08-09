package lab3;

import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;

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

        return names.toArray(new String[0]);
    }

    public void rFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("File was not found: " + filePath);
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            System.out.println("Contents of " + filePath + ":");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading File: " + e.getMessage());
        }
    }

    public void wFile(String filePath, String content) {
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("Written to File: " + filePath);
        } catch (IOException e) {
            System.out.println("Error Writing File: " + e.getMessage());
        }
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
}
