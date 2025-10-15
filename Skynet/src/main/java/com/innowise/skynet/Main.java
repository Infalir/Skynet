package com.innowise.skynet;

import com.innowise.skynet.models.Faction;
import com.innowise.skynet.models.Factory;
import com.innowise.skynet.threads.FactionThread;
import com.innowise.skynet.threads.FactoryThread;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Setting up Skynet simulation for 100 days...\n");

        Faction worldFaction = new Faction("World");
        Faction wednesdayFaction = new Faction("Wednesday");
        Factory factory = new Factory();
        CountDownLatch startLatch = new CountDownLatch(1);
        FactoryThread factoryThread = new FactoryThread(factory, startLatch);
        FactionThread worldThread = new FactionThread(worldFaction, factory, startLatch);
        FactionThread wednesdayThread = new FactionThread(wednesdayFaction, factory, startLatch);

        factoryThread.start();
        worldThread.start();
        wednesdayThread.start();
        System.out.println("Starting simulation...\n");
        startLatch.countDown();

        factoryThread.join();
        System.out.println("Factory thread finished. Waiting for factions to collect remaining parts...");

        Thread.sleep(1000);

        worldThread.interrupt();
        wednesdayThread.interrupt();
        worldThread.join(1000);
        wednesdayThread.join(1000);

        System.out.println("\n=== FINAL RESULTS AFTER 100 DAYS ===");
        System.out.printf("Total parts produced by factory: %d%n", factory.getTotalProduced());
        System.out.printf("Parts remaining in factory: %d%n", factory.getRemainingParts());
        System.out.println();
        worldFaction.printStatus();
        wednesdayFaction.printStatus();

        System.out.println();
        if (worldFaction.calculateCompleteRobots() > wednesdayFaction.calculateCompleteRobots()) {
            System.out.println("World faction has the strongest army.");
        } else if (wednesdayFaction.calculateCompleteRobots() > worldFaction.calculateCompleteRobots()) {
            System.out.println("Wednesday faction has the strongest army.");
        } else {
            System.out.println("It's a tie. Both factions have equally strong armies.");
        }
    }
}
