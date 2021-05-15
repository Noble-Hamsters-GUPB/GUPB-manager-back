package com.gupb.manager.model;

public enum PlayerStatus {

    IN_TESTING("In testing"),
    INCOMPLETE("Incomplete"),
    READY("Ready");

    private final String name;

    PlayerStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
