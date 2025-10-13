package Innowise.Threads;

import Innowise.Enum.PartType;
import Innowise.Models.Faction;
import Innowise.Models.Factory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FactionThread extends Thread{
    private final Faction faction;
    private final Factory factory;
    private final CountDownLatch latch;

    public FactionThread(Faction faction, Factory factory, CountDownLatch latch) {
        this.faction = faction;
        this.factory = factory;
        this.latch = latch;
        setName(faction.getName() + "Thread");
    }

    @Override
    public void run() {
        try {
            latch.await();
            while (!factory.isSimulationComplete() || factory.getRemainingParts() > 0) {
                factory.waitForNight();

                if (factory.isSimulationComplete() && factory.getRemainingParts() == 0) {
                    break;
                }

                List<PartType> collectedParts = factory.takeParts(faction);
                for (PartType part : collectedParts) {
                    faction.addPart(part);
                }

                if (!collectedParts.isEmpty() && factory.getDaysCompleted() % 20 == 0) {
                    System.out.printf("  %s collected %d parts (total: %d)%n", faction.getName(), collectedParts.size(), faction.getTotalCollected());
                }

                if (!factory.isSimulationComplete()) {
                    factory.waitForDay();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
