package com.gupb.manager.model;

public enum RequirementStatus {

    VALID("Valid"),
    PENDING("Pending"),
    DECLINED("Declined");

    private final String name;

    RequirementStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
