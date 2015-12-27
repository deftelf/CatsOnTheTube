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

    public void shave(int movementCount) {
        if (!isShaved) {
            isShaved = true;
            if (Main.verbose) {
                System.out.print("Movement " + movementCount + " - ");
            }
            System.out.println("Cat " + id + " " + name + " was shaved.");
        }
    }

    public boolean isShaved() {
        return isShaved;
    }

}
