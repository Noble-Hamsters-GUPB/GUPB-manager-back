package com.gupb.manager.model;

public enum BotStatus {

    IN_TESTING("In testing"),
    INCOMPLETE("Incomplete"),
    READY("Ready");

    private final String name;

    BotStatus(String name) {
        this.name = name;
    }
}
