package assignment;

/**
 * Represents metadata for a file in the repository.
 * Stores the file's simple name, full path, and line count.
 * 
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class FileInfo {
    public final String name;
    public final String path;
    public final int lines;

    public FileInfo(String name, String path, int lines) {
        this.name = name;
        this.path = path;
        this.lines = lines;
    }

    @Override
    public String toString() {
        return name + " (" + lines + " lines)";
    }
}