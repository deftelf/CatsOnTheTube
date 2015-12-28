package uk.co.deftelf.cats;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by carl on 25/12/15.
 */
public class Owner extends SentientThing {

    HashSet<Station> beenTo = new HashSet<>();

    protected Owner(int id, String name) {
        super(id, name);
    }

    @Override
    public void move() {
        // Owners try not to go to any station they've been to
        // There's probably an interesting statistical discussion to be had over whether they should do this since the cat is also moving randomly...
        Set<Station> reducedConnectTo = new HashSet<>(getStation().getConnectedTo());
        reducedConnectTo.removeAll(beenTo);
        if (reducedConnectTo.size() == 0) { // if there are none stations we haven't been to, then go anywhere
            reducedConnectTo = getStation().getConnectedTo();
        }
        moveToRandom(reducedConnectTo);
    }

    @Override
    public void setStation(Station station) {
        super.setStation(station);
        beenTo.add(station);
    }
}
