package assignment;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * Extracts UML-Style realtions between Classes/Interfaceas from a parsed Java AST.
 * Given a CompilationUnit and a set of allowed simple class names, the class
 * connects classes/interface declaration to infer relationships.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public final class JavaRelationExtractor{
    private static final Logger logger = LoggerFactory.getLogger(JavaRelationExtractor.class);
    private JavaRelationExtractor() {}

    public static Set<String> getClassNames(CompilationUnit cu) {
        Set<String> classNames  = new LinkedHashSet<>();
        cu.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(c -> classNames .add(c.getNameAsString()));
        return classNames;
    }

    public static List<Relation> giveRelations(CompilationUnit cu, Set<String> allowed) {
        List<Relation> relations = new ArrayList<>();
        List<ClassOrInterfaceDeclaration> classDecls = cu.findAll(ClassOrInterfaceDeclaration.class);
        logger.debug("Analyzing {} class/interface declarations with {} allowed names",
                        classDecls.size(), allowed.size());
        for(ClassOrInterfaceDeclaration classDecl : classDecls){
            String src = classDecl.getNameAsString();
            classDecl.getExtendedTypes().forEach(t ->
                addRelations(relations, allowed, src, simple(t), Relation.Kind.INHERITANCE));

            classDecl.getImplementedTypes().forEach(t ->
                addRelations(relations, allowed, src, simple(t), Relation.Kind.IMPLEMENTATION));

            for(FieldDeclaration fieldDecl : classDecl.getFields()) {
                for(String typeName : referencedTypeNames(fieldDecl.getElementType())) {
                    addRelations(relations, allowed, src, typeName,
                            fieldDecl.isFinal()
                                     ? Relation.Kind.COMPOSITION
                                     : Relation.Kind.AGGREGATION);
                }
            }

            for (MethodDeclaration methodDecl : classDecl.getMethods()) {
                methodDecl.getParameters().forEach(p ->
                    referencedTypeNames(p.getType()).forEach(name ->
                    addRelations(relations, allowed, src, name, Relation.Kind.DEPENDENCY)));
                methodDecl.getBody().ifPresent(body -> {
                    body.findAll(VariableDeclarationExpr.class).forEach(localVar ->
                    referencedTypeNames(localVar.getElementType()).forEach(name ->
                    addRelations(relations, allowed, src, name, Relation.Kind.ASSOCIATION)));
                });
            }

            for(ConstructorDeclaration constructorDecl : classDecl.getConstructors()) {
                constructorDecl.getParameters().forEach(p ->
                    referencedTypeNames(p.getType()).forEach(dst ->
                    addRelations(relations, allowed, src, dst, Relation.Kind.DEPENDENCY)));
                constructorDecl.getBody().findAll(VariableDeclarationExpr.class).forEach(localVar ->
                    referencedTypeNames(localVar.getElementType()).forEach(dst ->
                    addRelations(relations, allowed, src, dst, Relation.Kind.ASSOCIATION)));
                constructorDecl.getBody().findAll(ObjectCreationExpr.class).forEach(newExpr ->
                    referencedTypeNames(newExpr.getType()).forEach(dst ->
                    addRelations(relations, allowed, src, dst, Relation.Kind.DEPENDENCY)));
            }
        }
        return relations;
    }

    private static void addRelations(List<Relation> out, Set<String> allowed, String src,
                            String dst, Relation.Kind k) {
        if (dst == null || src.equals(dst) || !allowed.contains(dst)) return;
        out.add(new Relation(src, dst, k));
    }

    private static Set<String> referencedTypeNames(Type type) {
        Set<String> keywords = Set.of("extends", "super");
        Set<String> primitives = Set.of("void", "boolean", "byte", "short",
                "int", "long", "char", "float", "double");

        String text = type.asString();
        text = text.replace('@', ' ');
        logger.trace("Scanning type text: {}", text);
        StringTokenizer tokens = new StringTokenizer(text, "<>,[]&()? .:$\t\r\n", false);
        Set<String> names = new LinkedHashSet<>();
        while(tokens.hasMoreTokens()){
            String token = tokens.nextToken();
            if(primitives.contains(token) || keywords.contains(token)) continue;
            String simple = Names.simpleType(token);
            if(!simple.isEmpty()) names.add(simple);
        }
        logger.debug("Referenced types parsed: {}", names);
        return names;
    }

    private static String simple(Type t) {
        String s = Names.simpleType(t.asString());
        logger.trace("Simple type of '{}' : {}", t, s);
        return s;
    }
}
