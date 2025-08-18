package assignment;

import java.util.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.Type;

public class Parser {

    private final Blackboard blackboard;

    public Parser(Blackboard bb) {
        this.blackboard = bb;
    }

    public void parseAll(Map<String, String> sources) {
        Set<String> classNames = new LinkedHashSet<>();
        Map<String, CompilationUnit> cMap = new LinkedHashMap<>();

        for (var e : sources.entrySet()) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(e.getValue());
                cMap.put(e.getKey(), cu);
                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(x ->
                    classNames.add(x.getNameAsString()));
            } catch (Exception ex) {
                System.err.println("Parse error in " + e.getKey() + ": " + ex.getMessage());
            }
        }

        List<Blackboard.ClassDesc> classes = new ArrayList<>();
        for(String name : classNames) {
            classes.add(new Blackboard.ClassDesc(name));
        }

        List<Blackboard.Relation> relations = new ArrayList<>();
        for(CompilationUnit cu : cMap.values()) {
            for(ClassOrInterfaceDeclaration c : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String src = c.getNameAsString();

                for (FieldDeclaration f : c.getFields()) {
                    String t = simpleType(f.getElementType());
                    if(classNames.contains(t) && !t.equals(src)) {
                        relations.add(new Blackboard.Relation(src, t, Blackboard.Relation.Kind.AGGREGATION));
                    }
                }

                for (MethodDeclaration m : c.getMethods()) {
                    for(var p : m.getParameters()) {
                        String t = simpleType(p.getType());
                        if(classNames.contains(t) && !t.equals(src)) {
                            relations.add(new Blackboard.Relation(src, t, Blackboard.Relation.Kind.DEPENDENCY));
                        }
                    }
                }
            }
        }
        blackboard.setClassesAndRelations(classes, dedupe(relations));
    }

    private static String simpleType(Type t) {
        String s = t.asString();
        int lt = s.indexOf('<');
        if (lt >= 0) s = s.substring(0, lt);
        int dot = s.lastIndexOf('.');
        if(dot >= 0 && dot + 1 < s.length()) s = s.substring(dot + 1);
        return s;
    }

    private static List<Blackboard.Relation> dedupe(List<Blackboard.Relation> in) {
        Set<String> seen = new HashSet<>();
        List<Blackboard.Relation> out = new ArrayList<>();
        for (var r : in) {
            String key = r.src + "->" + r.dst + ":" + r.kind;
            if(seen.add(key)) out.add(r);
        }
        return out;
    }
}
