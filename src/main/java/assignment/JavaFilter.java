package assignment;

import java.util.function.Predicate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Predicate implementation for filtering Java source files.
 * Provides utility methods for recognizing Java files and for filtering
 * ClassDesc and FileInfo objects based on Java file criteria.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class JavaFilter implements Predicate<FileInfo> {
    public static final JavaFilter INSTANCE = new JavaFilter();
    private static final Logger logger = LoggerFactory.getLogger(JavaFilter.class);

    private JavaFilter() {
    }

    public static Predicate<ClassDesc> forClasses(Collection<FileInfo> files) {
        logger.debug("Building class filter for {} files", files.size());
        Set<String> allowedClassNames = new LinkedHashSet<>();
        for (FileInfo fileinfo : files) {
            if (JavaFilter.isJava(fileinfo) && fileinfo != null) {
                String baseName = Names.baseName(fileinfo.name);
                allowedClassNames.add(baseName);
                logger.trace("Allowed class: {}", baseName);
            } else {
                logger.trace("Skipping Non-Java files: {}", fileinfo != null ? fileinfo.name : "null");
            }
        }
        logger.info("Class filter created with {} allowed class names", allowedClassNames.size());
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
        boolean result = isJava(f);
        logger.debug("Testing File Info '{}' -> {}", f != null ? f.name : "null", result);
        return isJava(f);
    }
}
