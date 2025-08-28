package assignment;

/**
 * Represents a class found in the parsed Java source files.
 * Used for class-relation visualization and analysis.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class ClassDesc {
    public final String name;
    public final boolean isInterface;
    public final boolean isAbstract;

    public ClassDesc(String name) {
        this(name, false, false);
    }

    public ClassDesc(String name, boolean isInterface, boolean isAbstract) {
        this.name = name;
        this.isInterface = isInterface;
        this.isAbstract = isAbstract;
    }
}
