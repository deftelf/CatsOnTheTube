package uk.co.deftelf.cats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    static final int ITERATIONS = 100000;
    static final Random random = new Random();

    static HashMap<Integer, Station> stations;
    static int catsStarting; // owners number == cats number
    static List<Cat> cats;
    static List<Owner> owners;
    static List<Integer> turnsToFind;

    static boolean verbose;
    static boolean veryVerbose;
    static boolean shaveMode, mindTheGapMode;

    public static void main(String[] args) {

        if (!parseArgs(args)) {
            return;
        }

        turnsToFind = new ArrayList<>();
        try {
            loadStations();
            loadCats();
            loadOwners();
            assignStartingPositions();
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * make sure our random assignment hasn't put cats with their owners
     * @return If we had to do reassignment, therefore we need to run again until we didn't need to do anything.
     */
    private static boolean moveCatsAwayFromOwners(Station[] stationsArr) {
        for (int i=0; i < cats.size(); i++) {
            SentientThing cat = cats.get(i);
            SentientThing owner = owners.get(i);
            if (cat.getStation().equals(owner.getStation())) {
                cat.setStation(stationsArr[random.nextInt(stationsArr.length)]);
                return true;
            }
        }
        return false;
    }

    private static void assignStartingPositions() {
        Station[] stationsArr = stations.values().toArray(new Station[stations.size()]);
        for (SentientThing cat : cats) {
            int i = random.nextInt(stationsArr.length);
            cat.setStation(stationsArr[i]);
        }
        for (SentientThing owner : owners) {
            int i = random.nextInt(stationsArr.length);
            owner.setStation(stationsArr[i]);
        }
        while (moveCatsAwayFromOwners(stationsArr));
    }

    private static void run() {
        int movementCount = 0;
        while (true) {
            // check matches
            ArrayList<Integer> matches = new ArrayList<>();
            for (int i=0; i < cats.size(); i++) {
                SentientThing cat = cats.get(i);
                SentientThing owner = owners.get(i);
                if (cat.getStation().equals(owner.getStation())) {
                    matches.add(i);
                }
            }
            // remove matches
            for (int i=matches.size() - 1; i>=0; i--) {
                int matchedIndex = matches.get(i);
                Cat cat = cats.remove(matchedIndex);
                Owner owner = owners.remove(matchedIndex);
                if (verbose) {
                    System.out.print("Movement " + movementCount + " - ");
                }
                System.out.print("Owner " + owner.id + " " + owner.name + " found cat " + cat.id + " " + cat.name + " - " + owner.getStation().getName() + " is now closed.");
                if (cat.isShaved()) {
                    System.out.print(" Cat was shaved!");
                }
                System.out.println();
                turnsToFind.add(movementCount);
                owner.getStation().close();
            }

            if (shaveMode) {
                // We've removed matches, so any cats in a station with any owners are not their own
                // and are up for a de-fur
                HashSet<Station> ownerOccuped = new HashSet<>(owners.size());
                for (Owner owner : owners) {
                    ownerOccuped.add(owner.getStation());
                }
                for (Cat cat : cats) {
                    if (ownerOccuped.contains(cat.getStation())) { // OH DEAR
                        cat.shave(movementCount);
                    }
                }
            }

            if (movementCount < ITERATIONS && cats.size() > 0) {
                // move people and cats
                for (int i = 0; i < cats.size(); i++) {
                    cats.get(i).move();
                    owners.get(i).move();
                }
                movementCount++;
            } else {
                break;
            }
        }

        System.out.println("Total number of cats: " + catsStarting);
        int found = catsStarting - cats.size();
        System.out.println("Number of cats found: " + found);
        if (found > 0) { // If we didn't find any then we can't have an average
            // Assuming mean average
            // Omitting unfound cats from the average turns to find a cat, because there's no sensible way to include that number
            // but that makes this calculation kindof meaningless...
            // if we found 10 cats in turn 1 and no other cats for the next 99999 iterations, does average "turns to find" == 1 tell us anything?
            int totalTurns = 0;
            for (Integer turns : turnsToFind) {
                totalTurns += turns;
            }
            float meanTurnsToFind = ((float) totalTurns) / turnsToFind.size();
            meanTurnsToFind = Math.round(meanTurnsToFind);
            System.out.println("Average number of movements required to find a cat: " + ((int) meanTurnsToFind));
        }
        if (verbose) {
            System.out.println("Iterations run " + movementCount);
        }
    }

    static void loadCats() throws IOException {
        ArrayList<String> catNames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("cats.dat"))) {
            String line;
            while ((line = br.readLine()) != null) {
                catNames.add(line);
            }
        }

        cats = new ArrayList<>(catsStarting);
        for (int i=0; i < catsStarting; i++) {
            Cat c = new Cat(i, catNames.get(random.nextInt(catNames.size())));
            cats.add(c);
        }
    }

    static void loadOwners() throws IOException {
        ArrayList<String> humanNames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("humans.dat"))) {
            String line;
            while ((line = br.readLine()) != null) {
                humanNames.add(line);
            }
        }

        owners = new ArrayList<>(catsStarting);
        for (int i=0; i < catsStarting; i++) {
            Owner o = new Owner(i, humanNames.get(random.nextInt(humanNames.size())));
            owners.add(o);
        }
    }

    static void loadStations() throws IOException {
        stations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("tfl_stations.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                int id = Integer.parseInt(split[0]);
                stations.put(id, new Station(id, split[1]));
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("tfl_connections.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                int oneEnd = Integer.parseInt(split[0]);
                int otherEnd = Integer.parseInt(split[1]);
                // Links are bidirectional so need adding at both stations
                Station oneStation = stations.get(oneEnd);
                Station otherStation = stations.get(otherEnd);
                oneStation.addConnectedTo(otherStation);
                otherStation.addConnectedTo(oneStation);
            }
        }
    }

    private static boolean parseArgs(String[] args) {

        String lastArg = null;
        for (String arg : args) {
            if (arg.startsWith("--h")) {
                outputHelp();
                return false;
            } else if (arg.startsWith("--s")) {
                shaveMode = true;
            } else if (arg.startsWith("--m")) {
                mindTheGapMode = true;
            } else if (arg.equals("-v")) {
                verbose = true;
            } else if (arg.equals("-vv")) {
                veryVerbose = true;
                verbose = true;
            }
            lastArg = arg;
        }

        try {
            catsStarting = Integer.parseInt(lastArg);
            return true;
        } catch (Exception e) { // If we fail to parse just output the help text
        }

        outputHelp();
        return false;
    }

    private static void outputHelp() {
        System.out.println("Usage:");
        System.out.println(" [options] <number_of_cats>");
        System.out.println("");
        System.out.println("Options:");
        System.out.println(" --shave:\tShave mode. If someone finds someone else's cat, they shave it out of spite.");
        System.out.println(" --mind_the_gap:\tCats have a 1/50 chance of falling into the gap, and getting stuck where they are.");
        System.out.println(" -v:\tVerbose");
        System.out.println(" -vv:\t*VERBOSE*");
        System.out.println(" --help: this text");
    }
}
