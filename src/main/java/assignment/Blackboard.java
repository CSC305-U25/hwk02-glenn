package assignment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Central data model for the application.
 * Stores file, class, and relation information, and notifies observers on
 * changes.
 * Used for communication between UI components and data processing logic.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Blackboard implements PropertyChangeListener{
    private final List<FileInfo> files = new ArrayList<>();
    private final List<ClassDesc> classes = new ArrayList<>();
    private final List<Relation> relations = new ArrayList<>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (l != null) pcs.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (l != null) pcs.removePropertyChangeListener(l);
    }
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (l != null) pcs.addPropertyChangeListener(prop, l);
    }
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (l != null) pcs.removePropertyChangeListener(prop, l);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    public void setFiles(Collection<FileInfo> list) {
        var old = List.copyOf(files);
        files.clear();
        if (list != null) files.addAll(list);
        pcs.firePropertyChange("files", old, getFiles());
        pcs.firePropertyChange("model", null, this);
    }

    public void setClassesAndRelations(Collection<ClassDesc> cs, Collection<Relation> rs) {
        var oldC = List.copyOf(classes);
        var oldR = List.copyOf(relations);
        classes.clear();
        relations.clear();
        if (cs != null) classes.addAll(cs);
        if (rs != null) relations.addAll(rs);
        pcs.firePropertyChange("classes", oldC, getClasses());
        pcs.firePropertyChange("relations", oldR, getRelations());
        pcs.firePropertyChange("model", null, this);
    }

    public List<FileInfo> getFiles() { return List.copyOf(files); }
    public List<ClassDesc> getClasses() { return List.copyOf(classes); }
    public List<Relation> getRelations() { return List.copyOf(relations); }

    public List<FileInfo> getJavaFiles() {
        List<FileInfo> onlyJava = new ArrayList<>();
        for (FileInfo f : files) {
            if (JavaFilter.INSTANCE.test(f)) {
                onlyJava.add(f);
            }
        }
        return List.copyOf(onlyJava);
    }

    public int getIncomingRelationCount(String fileName) {
        String base = Names.baseName(fileName);
        int count = 0;
        for (Relation r : relations) {
            if (base.equals(r.dst))
                count++;
        }
        return count;
    }

    public int getOutgoingRelationCount(String fileName) {
        String base = Names.baseName(fileName);
        int count = 0;
        for (Relation r : relations) {
            if (base.equals(r.src))
                count++;
        }
        return count;
    }
}
