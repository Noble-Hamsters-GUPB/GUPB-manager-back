package com.gupb.manager.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Requirement.TABLE_NAME)
public class Requirement {

    public static final String TABLE_NAME = "requirement";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.PACKAGE_INFO)
    private String packageInfo;

    @Column(columnDefinition = "ENUM('REQUESTED', 'APPROVED', 'REJECTED')", name = Columns.STATUS)
    @Enumerated(EnumType.STRING)
    private RequirementStatus status;

    public int getId() {
        return id;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public RequirementStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public void setStatus(RequirementStatus status) {
        this.status = status;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String PACKAGE_INFO = "package_info";

        public static final String STATUS = "status";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement that = (Requirement) o;
        return id == that.id &&
                Objects.equals(packageInfo, that.packageInfo) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, packageInfo, status);
    }
}
