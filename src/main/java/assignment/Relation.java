package assignment;

/**
 * Represents a relationship between two classes in the codebase.
 * Used for visualizing dependencies and aggregations in the relations panel.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Relation {
    public enum Kind {
        AGGREGATION, COMPOSITION, DEPENDENCY, INHERITANCE, IMPLEMENTATION
    }

    public final String src, dst;
    public final Kind kind;

    public Relation(String src, String dst, Kind kind) {
        this.src = src;
        this.dst = dst;
        this.kind = kind;
    }

    public String getSrc() { return src; }
    public String getDst() { return dst; }
    public Kind getKind() { return kind; }

    public String plantUmlArrow() {
        return switch (kind) {
            case AGGREGATION    -> " o-- ";
            case COMPOSITION    -> " *-- ";
            case DEPENDENCY     -> " ..> ";
            case INHERITANCE    -> " <|-- ";
            case IMPLEMENTATION -> " <|.. ";
        };
    }
}
