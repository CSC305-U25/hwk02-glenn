package assignment;

import java.util.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Parses Java source files to extract class and relation information.
 * Uses JavaParser to analyze source code and populate the Blackboard with
 * class and dependency/aggregation relations.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private final Blackboard blackboard;

    public Parser(Blackboard bb) {
        this.blackboard = bb;
        logger.debug("Parser created");
    }

    public void parseAll(Map<String, String> sources) {
        logger.debug("parseAll: parsing {} source(s)", sources.size());
        Set<String> classNames = new LinkedHashSet<>();
        Map<String, CompilationUnit> cMap = new LinkedHashMap<>();

        for(Map.Entry<String, String> e : sources.entrySet()) {
            String key = e.getKey();
            try {
                CompilationUnit cu = StaticJavaParser.parse(e.getValue());
                cMap.put(key, cu);
                Set<String> names = JavaRelationExtractor.getClassNames(cu);
                classNames.addAll(JavaRelationExtractor.getClassNames(cu));
                logger.trace("Parsed '{}' -> {} class/interface declaration(s): {}",
                    key, names.size(), names);
            } catch (Exception ex) {
                logger.warn("Parse error in '{}' : {}", key,
                (ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()), ex);
            }
        }

        Map<String, ClassDesc> classMap = new LinkedHashMap<>();
        for (CompilationUnit cu : cMap.values()) {
            for (ClassOrInterfaceDeclaration d : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String name = d.getNameAsString();
                boolean isInterface = d.isInterface();
                boolean isAbstract  = d.isAbstract();

                ClassDesc prev = classMap.get(name);
                classMap.put(name,
                    (prev == null)
                        ? new ClassDesc(name, isInterface, isAbstract)
                        : new ClassDesc(name,
                                        prev.isInterface || isInterface,
                                        prev.isAbstract  || isAbstract));
            }
        }
        List<ClassDesc> classes = new ArrayList<>(classMap.values());
        logger.debug("Collected {} unique classDesc entries (from {} parsed file(s))",
            classes.size(), cMap.size());
        List<Relation> relations = new ArrayList<>();
        for (CompilationUnit c : cMap.values()) {
            List<Relation> rels = JavaRelationExtractor.giveRelations(c, classNames);
            relations.addAll(rels);
            logger.trace("Added {} relation(s) from a ocmpilation unit", rels.size());
        }
        List<Relation> deduped = dedupe(relations);
        logger.debug("Relations: raw={}, deduped={}", relations.size(), deduped.size());
        blackboard.setClassesAndRelations(classes, deduped);
    }

    private static List<Relation> dedupe(List<Relation> in) {
        Set<String> seen = new HashSet<>();
        List<Relation> out = new ArrayList<>();
        for (var r : in) {
            String key = r.src + "->" + r.dst + ":" + r.kind;
            if (seen.add(key))
                out.add(r);
        }
        return out;
    }
}
