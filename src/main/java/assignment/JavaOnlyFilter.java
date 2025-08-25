package assignment;

public final class JavaOnlyFilter implements FilePathFilter{
    @Override
    public boolean accept(String path) {
        return path != null && path.endsWith(".java");
    }
}
