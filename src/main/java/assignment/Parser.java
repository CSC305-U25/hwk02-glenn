package assignment;

import java.util.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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
    private final Blackboard blackboard;

    public Parser(Blackboard bb) {
        this.blackboard = bb;
    }

    public void parseAll(Map<String, String> sources) {
        Set<String> classNames = new LinkedHashSet<>();
        Map<String, CompilationUnit> cMap = new LinkedHashMap<>();

        for(Map.Entry<String, String> e : sources.entrySet()) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(e.getValue());
                cMap.put(e.getKey(), cu);
                classNames.addAll(JavaRelationExtractor.getClassNames(cu));
            } catch (Exception ex) {
                System.err.println("Parse error in " + e.getKey() + ": " + ex.getMessage());
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
        List<Relation> relations = new ArrayList<>();
        for (CompilationUnit c : cMap.values()) {
            relations.addAll(JavaRelationExtractor.giveRelations(c, classNames));
        }
        blackboard.setClassesAndRelations(classes, dedupe(relations));
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
