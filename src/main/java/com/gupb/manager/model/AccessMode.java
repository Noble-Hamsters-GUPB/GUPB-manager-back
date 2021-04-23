package com.gupb.manager.model;

public enum AccessMode {

    OPEN("Open"),
    RESTRICTED("Restricted"),
    INVITE_ONLY("Invite only");

    private final String name;

    AccessMode(String name) {
        this.name = name;
    }
}
