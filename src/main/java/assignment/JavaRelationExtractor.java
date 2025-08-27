package assignment;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;

import java.util.*;

public final class JavaRelationExtractor{
    private JavaRelationExtractor() {}

    public static Set<String> getClassNames(CompilationUnit cu) {
        Set<String> out = new LinkedHashSet<>();
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> out.add(c.getNameAsString()));
        return out;
    }

    public static List<Relation> giveRelations(CompilationUnit cu, Set<String> allowed) {
        List<Relation> out = new ArrayList<>();
        for(ClassOrInterfaceDeclaration c : cu.findAll(ClassOrInterfaceDeclaration.class)){
            String src = c.getNameAsString();

            c.getExtendedTypes().forEach(t -> add(out, allowed, src, simple(t), Relation.Kind.INHERITANCE));
            c.getImplementedTypes().forEach(t -> add(out, allowed, src, simple(t), Relation.Kind.IMPLEMENTATION));

            for(FieldDeclaration f : c.getFields()) {
                add(out, allowed, src, simple(f.getElementType()),
                    f.isFinal() ? Relation.Kind.COMPOSITION : Relation.Kind.AGGREGATION);
            }

            for (MethodDeclaration m : c.getMethods()) {
                m.getParameters().forEach(p ->
                            add(out, allowed, src, simple(p.getType()), Relation.Kind.DEPENDENCY));
                m.getBody().ifPresent(b -> {
                    b.findAll(VariableDeclarationExpr.class).forEach(v ->
                            add(out, allowed, src, simple(v.getElementType()), Relation.Kind.DEPENDENCY));
                    b.findAll(ObjectCreationExpr.class).forEach(n ->
                            add(out, allowed, src, simple(n.getType()), Relation.Kind.DEPENDENCY));
                });
            }
        }
        return out;
    }

    private static void add(List<Relation> out, Set<String> allowed, String src, String dst, Relation.Kind k) {
        if (dst == null || src.equals(dst) || !allowed.contains(dst)) return;
        out.add(new Relation(src, dst, k));
    }

    private static String simple(Type t) {
        String s = t.asString();
        int lt = s.indexOf('<');
        if (lt >= 0) s = s.substring(0, lt);
        while(s.endsWith("[]")) s = s.substring(0, s.length() - 2);
        int dot = s.lastIndexOf('.');
        if(dot >= 0 && dot + 1 < s.length()) s = s.substring(dot + 1);
        return s;
    }
}
