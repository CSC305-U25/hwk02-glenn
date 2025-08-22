package assignment;

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