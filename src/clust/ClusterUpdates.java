package clust;

import javafx.util.Pair;

import java.util.ArrayList;

public class ClusterUpdates {
    public ArrayList<Integer> addedTo = new ArrayList<>();
    public ArrayList<Integer> created = new ArrayList<>();
    public ArrayList<Integer> removed = new ArrayList<>();
    public ArrayList<Pair<Integer, Integer>> merged = new ArrayList<>();

    public ClusterUpdates() {}

    public ClusterUpdates(ArrayList<Integer> addedTo, ArrayList<Integer> created, ArrayList<Integer> removed,
                          ArrayList<Pair<Integer, Integer>> merged) {
        this.created = created;
        this.addedTo = addedTo;
        this.removed = removed;
        this.merged = merged;
    }
}
