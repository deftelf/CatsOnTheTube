package uk.co.deftelf.cats;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by carl on 25/12/15.
 */
public abstract class SentientThing { // I had to look up whether cats were sentient when I named this...

    public final int id;
    public final String name;
    private final Collection<Station> stationsVisited;
    protected boolean isStuck;
    private Station station;

    protected SentientThing(int id, String name) {
        this.id = id;
        this.name = name;
        stationsVisited = new HashSet<>();
    }

    public abstract void move();

    public void setStation(Station station) {
        if (Main.veryVerbose) {
            System.out.println("Moved " + getClass().getSimpleName() + " " + id + " to " + station.getName());
        }
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    protected void moveToRandom(List<Station> connectedTo) {
        if (connectedTo.size() > 0) {
            int index = Main.random.nextInt(connectedTo.size());
            Station station = connectedTo.get(index);
            setStation(station);
        }
    }
}
