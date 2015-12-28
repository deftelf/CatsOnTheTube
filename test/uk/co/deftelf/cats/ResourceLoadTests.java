package uk.co.deftelf.cats;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by carl on 28/12/15.
 */
public class ResourceLoadTests {
    @Before
    public void setUp() throws Exception {
        Main.loadStations();
        Main.loadCats();
        Main.loadOwners();
    }


    @Test
    public void testStationCount() throws Exception {
        Assert.assertEquals(Main.stations.size(), 302);
    }

    @Test
    public void testStations() throws Exception {
        Station bow = Main.stations.get(33);
        Assert.assertEquals(bow.getName(), "Bow Road");
        Assert.assertEquals(bow.getConnectedTo().size(), 2); // Connected through two different lines
        Station mileEnd = Main.stations.get(164);
        Station bromley = Main.stations.get(36);
        Assert.assertTrue(bow.getConnectedTo().contains(mileEnd));
        Assert.assertTrue(bow.getConnectedTo().contains(bromley));
    }

    @Test
    public void testMovement() throws Exception {
        for (int i=0; i < 20; i++) { // Random elements so we should run a few times to improve chance of hitting all states
            Owner bob = new Owner(9999, "Bob");
            Station bow = Main.stations.get(33);
            Station mileEnd = Main.stations.get(164);
            Station bromley = Main.stations.get(36);
            bob.setStation(bow);
            Assert.assertEquals(bob.beenTo.size(), 1);
            Assert.assertTrue(bob.beenTo.contains(bow));
            bob.move();
            Assert.assertEquals(bob.beenTo.size(), 2);
            if (bob.getStation().equals(mileEnd)) {
                Assert.assertTrue(bob.beenTo.contains(mileEnd));
                bob.move();
                Station bg = Main.stations.get(24);
                Station stepney = Main.stations.get(244);
                Station stratford = Main.stations.get(247);
                Assert.assertTrue(bob.getStation().equals(stepney) || bob.getStation().equals(bg) || bob.getStation().equals(stratford));
            } else if (bob.getStation().equals(bromley)) {
                Assert.assertTrue(bob.beenTo.contains(bromley));
                bob.move();
                Station westham = Main.stations.get(289);
                Assert.assertEquals(bob.getStation(), westham);
            } else {
                throw new Exception("Wrong station: " + bob.getStation().getName());
            }
        }
    }
}
