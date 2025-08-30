package assignment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central data model for the application.
 * Stores file, class, and relation information, and notifies observers on
 * changes.
 * Used for communication between UI components and data processing logic.
 * Provides methods for updating and retrieving files, classes, and relations.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Blackboard implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(Blackboard.class);
    private final List<FileInfo> files = new ArrayList<>();
    private final List<ClassDesc> classes = new ArrayList<>();
    private final List<Relation> relations = new ArrayList<>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (l == null)
            return;
        pcs.addPropertyChangeListener(l);
        logger.trace("Listener added: {}", l.getClass().getSimpleName());
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (l == null)
            return;
        pcs.removePropertyChangeListener(l);
        logger.trace("Listener removed (all props): {}", l.getClass().getSimpleName());
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (l == null)
            return;
        pcs.addPropertyChangeListener(prop, l);
        logger.trace("Listener added for '{}': {}", prop, l.getClass().getSimpleName());
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (l == null)
            return;
        pcs.removePropertyChangeListener(prop, l);
        logger.trace("Listener removed for '{}': {}", prop, l.getClass().getSimpleName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.trace("Forwarding property change: {}", evt.getPropertyName());
        pcs.firePropertyChange(evt);
    }

    public void setFiles(Collection<FileInfo> list) {
        var old = List.copyOf(files);
        files.clear();
        if (list != null)
            files.addAll(list);
        pcs.firePropertyChange("files", old, getFiles());
        pcs.firePropertyChange("model", null, this);
    }

    public void setClassesAndRelations(Collection<ClassDesc> cs, Collection<Relation> rs) {
        var oldC = List.copyOf(classes);
        var oldR = List.copyOf(relations);
        classes.clear();
        relations.clear();
        if (cs != null)
            classes.addAll(cs);
        if (rs != null)
            relations.addAll(rs);
        pcs.firePropertyChange("classes", oldC, getClasses());
        pcs.firePropertyChange("relations", oldR, getRelations());
        pcs.firePropertyChange("model", null, this);
    }

    public List<FileInfo> getFiles() {
        return List.copyOf(files);
    }

    public List<ClassDesc> getClasses() {
        return List.copyOf(classes);
    }

    public List<Relation> getRelations() {
        return List.copyOf(relations);
    }

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
