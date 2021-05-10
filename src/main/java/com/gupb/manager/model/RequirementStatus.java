package com.gupb.manager.model;

public enum RequirementStatus {

    VALID("Valid"),
    PENDING("Pending"),
    INVALID("Invalid"),
    DECLINED("Declined");

    private final String name;

    RequirementStatus(String name) {
        this.name = name;
    }
}
