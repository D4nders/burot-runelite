package com.burot;

public class SharedEventState {

    private int lastPetDropTick;

    public SharedEventState() {
        this.lastPetDropTick = -1;
    }

    public void registerPetDrop(int currentTick) {
        this.lastPetDropTick = currentTick;
    }

    public boolean isWithinPetDropWindow(int currentTick) {
        return lastPetDropTick != -1 && (currentTick - lastPetDropTick) <= 2;
    }
}