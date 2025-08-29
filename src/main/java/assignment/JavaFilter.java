package assignment;

import java.util.function.Predicate;
import java.util.*;

public class JavaFilter implements Predicate<FileInfo>{
    public static final JavaFilter INSTANCE = new JavaFilter();
    private JavaFilter(){}

    public static Predicate<ClassDesc> forClasses(Collection<FileInfo> files) {
        Set<String> allowedClassNames = new LinkedHashSet<>();
        for(FileInfo fileinfo : files) {
            if(JavaFilter.isJava(fileinfo)) {
                allowedClassNames.add(Names.baseName(fileinfo.name));
            }
        }
        return classDesc -> classDesc != null
                    && allowedClassNames.contains(classDesc.name);
    }

    public static boolean isJavaFileName(String name) {
        return name != null && name.endsWith(".java");
    }

    public static boolean isJava(FileInfo f) {
        return f != null && isJavaFileName(f.name);
    }

    @Override
    public boolean test(FileInfo f) {
        return isJava(f);
    }
}
