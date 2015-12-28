package uk.co.deftelf.cats;

import java.util.*;

/**
 * Created by carl on 25/12/15.
 */
public class Station {

    private final int id;
    private final String name;
    private final Set<Station> connectedTo;
    private boolean isOpen;

    public Station(int id, String name) {
        this.id = id;
        this.name = name;
        connectedTo = new HashSet<>();
        isOpen = true;
    }

    public String getName() {
        return name;
    }

    public Set<Station> getConnectedTo() {
        return connectedTo;
    }

    public void addConnectedTo(Station targetStation) {
        // Some stations are connected to the same station twice through different lines.
        // To simplify implementation we don't need to have two connections, so just use a set and discard duplicates
        connectedTo.add(targetStation);
    }

    public void close() {
        isOpen = false;
        for (Station connection : connectedTo) {
            connection.getConnectedTo().remove(this);
        }
        getConnectedTo().clear();
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj.getClass().equals(getClass())) {
                return (id == ((Station) obj).id);
            }
        }
        return false;
    }
}
