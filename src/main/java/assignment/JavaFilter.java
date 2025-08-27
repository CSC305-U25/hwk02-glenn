package assignment;

import java.util.function.Predicate;

public class JavaFilter implements Predicate<FileInfo>{
    public static final JavaFilter INSTANCE = new JavaFilter();

    private JavaFilter(){}

    @Override
    public boolean test(FileInfo f) {
        return f != null && f.name != null && f.name.endsWith(".java");
    }
}
