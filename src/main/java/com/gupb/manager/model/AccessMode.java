package com.gupb.manager.model;

public enum AccessMode {

    OPEN("Open"),
    INVITE_ONLY("Invite only");

    private final String name;

    AccessMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
