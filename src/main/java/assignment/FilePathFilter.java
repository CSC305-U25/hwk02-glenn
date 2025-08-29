package assignment;

/**
 * Filter interface for evaluating file paths.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */


public interface FilePathFilter {
    boolean accept(String path);
}
