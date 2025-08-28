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

        sources.forEach((name, src) -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(src);
                cMap.put(name, cu);
                classNames.addAll(JavaRelationExtractor.getClassNames(cu));
            } catch (Exception ex) {
                System.err.println("Parse error in " + name + ": " + ex.getMessage());
            }
        });
        List<ClassDesc> classes = new ArrayList<>();
        for (CompilationUnit cu : cMap.values()) {
            for (ClassOrInterfaceDeclaration decl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String n = decl.getNameAsString();
                boolean isInterface = decl.isInterface();
                boolean isAbstract = decl.isAbstract();
                classes.add(new ClassDesc(n, isInterface, isAbstract));
            }
        }

        List<Relation> rel = new ArrayList<>();
        for (CompilationUnit c : cMap.values()) {
            rel.addAll(JavaRelationExtractor.giveRelations(c, classNames));
        }
        blackboard.setClassesAndRelations(classes, dedupe(rel));
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
