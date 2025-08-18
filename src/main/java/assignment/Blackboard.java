package assignment;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Blackboard {

    public static final class FileInfo {
        public final String name;
        public final String path;
        public final int lines;
        public FileInfo(String name, String path, int lines) {
            this.name = name; this.path = path; this.lines = lines;
        }
        @Override
        public String toString() {
            return name + " (" + lines + " lines)";
        }
    }

    public static final class ClassDesc {
        public final String name;
        public ClassDesc(String name) {
            this.name = name;
        }
    }

    public static final class Relation {
        public enum Kind { AGGREGATION, DEPENDENCY}
        public final String src, dst;
        public final Kind kind;
        public Relation(String src, String dst, Kind kind) {
            this.src = src; this.dst = dst; this.kind = kind;
        }
    }

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
    private void notifyObservers(){
        for (var o : observers) {
            o.accept(this);
        }
    }

    public void setFiles(Collection<FileInfo> list) {
        files.clear();
        if(list != null) files.addAll(list);
        notifyObservers();
    }

    public void setClassesAndRelations(Collection<ClassDesc> cs, Collection<Relation> rs) {
        classes.clear();
        relations.clear();
        if(cs != null) classes.addAll(cs);
        if(rs != null) relations.addAll(rs);
        notifyObservers();
    }

    public void setSelectedFile(String simpleName) {
        selectedFileSimpleName = simpleName;
        notifyObservers();
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
    public Optional<String> getSelectedFiles() {
        return Optional.ofNullable(selectedFileSimpleName);
    };
}
