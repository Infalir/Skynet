package Innowise.Threads;

import Innowise.Models.Factory;

import java.util.concurrent.CountDownLatch;

public class FactoryThread extends Thread{
    private final Factory factory;
    private final CountDownLatch latch;

    public FactoryThread(Factory factory, CountDownLatch latch) {
        this.factory = factory;
        this.latch = latch;
        setName("FactoryThread");
    }

    @Override
    public void run() {
        try {
            latch.await();

            for (int day = 1; day <= 100; day++) {
                factory.startDay();
                Thread.sleep(50);
                factory.endDay();

                if (day % 20 == 0 || day == 100) {
                    System.out.printf("--- Day %d completed. Total parts produced: %d, Remaining in factory: %d ---%n", day, factory.getTotalProduced(), factory.getRemainingParts());
                }

                if (day < 100) {
                    Thread.sleep(20);
                }
            }

            factory.completeSimulation();
            System.out.println("\nFactory: 100 days completed. Stopping production.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
