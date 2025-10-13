package Innowise.Models;

import Innowise.Enum.PartType;

import java.util.concurrent.atomic.AtomicInteger;

public class Faction {
    private final String name;
    private final AtomicInteger heads = new AtomicInteger(0);
    private final AtomicInteger torsos = new AtomicInteger(0);
    private final AtomicInteger hands = new AtomicInteger(0);
    private final AtomicInteger feet = new AtomicInteger(0);
    private final AtomicInteger totalCollected = new AtomicInteger(0);

    public Faction(String name) {
        this.name = name;
    }

    public void addPart(PartType part) {
        switch (part) {
            case HEAD:
                heads.incrementAndGet();
                break;
            case TORSO:
                torsos.incrementAndGet();
                break;
            case HAND:
                hands.incrementAndGet();
                break;
            case FOOT:
                feet.incrementAndGet();
                break;
        }
        totalCollected.incrementAndGet();
    }

    public int calculateCompleteRobots() {
        int completeRobots = Math.min(heads.get(), Math.min(torsos.get(), Math.min(hands.get() / 2, feet.get() / 2)));
        return completeRobots;
    }

    public void printStatus() {
        int robots = calculateCompleteRobots();
        System.out.printf("%s: %d robots (Heads: %d, Torsos: %d, Hands: %d, Feet: %d, Total Collected: %d)%n", name, robots, heads.get(), torsos.get(), hands.get(), feet.get(), totalCollected.get());
    }

    public String getName() {
        return name;
    }
    public int getTotalCollected() {
        return totalCollected.get();
    }
}
