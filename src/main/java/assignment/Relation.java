    package assignment;

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