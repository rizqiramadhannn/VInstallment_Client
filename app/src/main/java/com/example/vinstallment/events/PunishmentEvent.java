package com.example.vinstallment.events;

public class PunishmentEvent {
    private final boolean isActive;
    private final int id;

    public PunishmentEvent(boolean isActive, int id) {
        this.isActive = isActive;
        this.id = id;
    }

    public boolean getStatus() { return isActive; }

    public int getId() {
        return id;
    }
}
