package assignment;

import java.util.*;

/**
 * Utility class for building PlantUML class diagrams from file and relation
 * data.
 * Generates PlantUML source code for classes and their relationships for
 * visualization.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class UmlBuilder {
    private UmlBuilder() {
    }

    public static String createUml(Collection<Square> squares,
            Collection<Relation> relations) {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("!pragma layout smetana\n");

        Map<String, String> nameMap = new LinkedHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        if (squares != null) {
            for (Square s : squares) {
                String base = Names.baseName(s.getFileName());
                String key = Names.sanitize(base);
                String label = nameMap.get(key);
                if (label == null) {
                    label = Names.uniquify(names, key);
                    nameMap.put(key, label);
                    sb.append("class ").append(label).append('\n');
                }
            }
        }
        if (relations != null && !relations.isEmpty()) {
            for (Relation r : relations) {
                String src = Names.resolveLabel(nameMap, r.getSrc());
                String dst = Names.resolveLabel(nameMap, r.getDst());
                if (src == null || dst == null)
                    continue;
                String arrow = r.plantUmlArrow();
                sb.append(src).append(arrow).append(dst).append('\n');
            }
        }
        sb.append("@enduml\n");
        return sb.toString();
    }
}
