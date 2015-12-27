package uk.co.deftelf.cats;

import java.util.List;

/**
 * Created by carl on 25/12/15.
 */
public class Cat extends SentientThing {

    private boolean isShaved;

    public Cat(int id, String name) {
        super(id, name);
    }

    @Override
    public void move() {
        moveToRandom(getStation().getConnectedTo());
    }

    public void shave() {
        isShaved = true;
    }

    public boolean isShaved() {
        return isShaved;
    }

}
