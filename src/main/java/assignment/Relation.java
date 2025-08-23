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
        AGGREGATION, DEPENDENCY
    }

    public final String src, dst;
    public final Kind kind;

    public Relation(String src, String dst, Kind kind) {
        this.src = src;
        this.dst = dst;
        this.kind = kind;
    }
}