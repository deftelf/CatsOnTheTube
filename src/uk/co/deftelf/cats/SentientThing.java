package uk.co.deftelf.cats;

import java.util.Set;

/**
 * Created by carl on 25/12/15.
 */
public abstract class SentientThing { // I had to look up whether cats were sentient when I named this...

    public final int id;
    public final String name;
    private Station station;

    protected SentientThing(int id, String name) {
        this.id = id;
        this.name = name;
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

    protected void moveToRandom(Set<Station> connectedTo) {
        if (connectedTo.size() > 0) {
            // Slightly awkward pick random item iterator. Sets have no int index, so you have to step through them
            int index = Main.random.nextInt(connectedTo.size());
            int i = 0;
            for (Station station : connectedTo) {
                if (i == index) {
                    setStation(station);
                    break;
                }
                i++;
            }
        }
    }
}
