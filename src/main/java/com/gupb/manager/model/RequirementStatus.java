package com.gupb.manager.model;

public enum RequirementStatus {

    REQUESTED("Requested"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String name;

    RequirementStatus(String name) {
        this.name = name;
    }
}
