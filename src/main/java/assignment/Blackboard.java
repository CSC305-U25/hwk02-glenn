package assignment;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

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

public class Blackboard {
    private final List<FileInfo> files = new ArrayList<>();
    private final List<ClassDesc> classes = new ArrayList<>();
    private final List<Relation> relations = new ArrayList<>();
    private String selectedFileSimpleName = null;

    private final List<Consumer<Blackboard>> observers = new CopyOnWriteArrayList<>();

    public void addObserver(Consumer<Blackboard> obs) {
        if (obs != null) observers.add(obs);
    }

    public void removeObserver(Consumer<Blackboard> obs) {
        observers.remove(obs);
    }

    public void notifyObservers() { for (var o : observers) o.accept(this); }

    public void setFiles(Collection<FileInfo> list) {
        files.clear();
        if (list != null) files.addAll(list);
        notifyObservers();
    }

    public void setClassesAndRelations(Collection<ClassDesc> cs, Collection<Relation> rs) {
        classes.clear();
        relations.clear();
        if (cs != null) classes.addAll(cs);
        if (rs != null) relations.addAll(rs);
        notifyObservers();
    }

    public void setSelectedFile(String simpleName) {
        selectedFileSimpleName = simpleName;
        notifyObservers();
    }

    public List<FileInfo> getFiles() { return List.copyOf(files); }

    public List<ClassDesc> getClasses() { return List.copyOf(classes); }

    public List<Relation> getRelations() { return List.copyOf(relations); }

    public Optional<String> getSelectedFiles() {
        return Optional.ofNullable(selectedFileSimpleName);
    };
}
