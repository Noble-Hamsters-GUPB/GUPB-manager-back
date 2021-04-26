package com.gupb.manager.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = Tournament.TABLE_NAME)
public class Tournament {

    public static final String TABLE_NAME = "tournament";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.NAME)
    private String name;

    @Column(columnDefinition = "ENUM('OPEN', 'RESTRICTED', 'INVITE_ONLY')", name = Columns.ACCESS_MODE)
    @Enumerated(EnumType.STRING)
    private AccessMode accessMode;

    public Tournament() {}

    public Tournament(String name, AccessMode accessMode) {
        this.name = name;
        this.accessMode = accessMode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String ACCESS_MODE = "access_mode";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                accessMode == that.accessMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, accessMode);
    }
}