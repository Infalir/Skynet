package com.innowise.skynet.models;

import com.innowise.skynet.enums.PartType;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Factory {
    private final BlockingQueue<PartType> producedParts = new LinkedBlockingQueue<>();
    private final Random random = new Random();
    private final AtomicInteger daysCompleted = new AtomicInteger(0);
    private final AtomicInteger totalProduced = new AtomicInteger(0);
    private final AtomicBoolean simulationComplete = new AtomicBoolean(false);

    private final Object dayNightLock = new Object();
    private boolean isDayTime = true;

    public void startDay() throws InterruptedException {
        synchronized (dayNightLock) {
            isDayTime = true;
            dayNightLock.notifyAll();
        }

        int partsToProduce = random.nextInt(11);
        List<PartType> todayProduction = new ArrayList<>();

        for (int i = 0; i < partsToProduce; i++) {
            PartType part = PartType.values()[random.nextInt(PartType.values().length)];
            producedParts.offer(part);
            todayProduction.add(part);
        }

        totalProduced.addAndGet(partsToProduce);

        System.out.printf("Day %d: Factory produced %d parts: %s%n", daysCompleted.get() + 1, partsToProduce, getProductionSummary(todayProduction));
    }

    public void endDay() throws InterruptedException {
        synchronized (dayNightLock) {
            isDayTime = false;
            daysCompleted.incrementAndGet();
            dayNightLock.notifyAll();
        }
    }

    public List<PartType> takeParts() throws InterruptedException {
        synchronized (dayNightLock) {
            while ((isDayTime || producedParts.isEmpty()) && !simulationComplete.get()) {
                dayNightLock.wait(100);
            }

            if (simulationComplete.get() && producedParts.isEmpty()) {
                return new ArrayList<>();
            }
        }

        List<PartType> takenParts = new ArrayList<>();
        int partsToTake = Math.min(5, producedParts.size());

        for (int i = 0; i < partsToTake; i++) {
            PartType part = producedParts.poll();
            if (part != null) {
                takenParts.add(part);
            }
        }

        return takenParts;
    }

    public void waitForDay() throws InterruptedException {
        synchronized (dayNightLock) {
            while (!isDayTime && !simulationComplete.get()) {
                dayNightLock.wait();
            }
        }
    }

    public void waitForNight() throws InterruptedException {
        synchronized (dayNightLock) {
            while (isDayTime && !simulationComplete.get()) {
                dayNightLock.wait();
            }
        }
    }

    private String getProductionSummary(List<PartType> production) {
        Map<PartType, Integer> count = new HashMap<>();
        for (PartType type : PartType.values()) {
            count.put(type, 0);
        }
        for (PartType part : production) {
            count.put(part, count.get(part) + 1);
        }
        return String.format("Heads: %d, Torsos: %d, Hands: %d, Feet: %d", count.get(PartType.HEAD), count.get(PartType.TORSO), count.get(PartType.HAND), count.get(PartType.FOOT));
    }

    public void completeSimulation() {
        simulationComplete.set(true);
        synchronized (dayNightLock) {
            dayNightLock.notifyAll();
        }
    }

    public boolean isSimulationComplete() {
        return simulationComplete.get();
    }

    public int getDaysCompleted() {
        return daysCompleted.get();
    }
    public int getTotalProduced() {
        return totalProduced.get();
    }
    public int getRemainingParts() {
        return producedParts.size();
    }
}
