package com.gupb.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gupb.manager.serializers.AdminSerializer;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Tournament.TABLE_NAME)
public class Tournament {

    public static final String TABLE_NAME = "tournament";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.NAME)
    private String name;

    @Column(columnDefinition = "ENUM('OPEN', 'INVITE_ONLY')", name = Columns.ACCESS_MODE)
    @Enumerated(EnumType.STRING)
    private AccessMode accessMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.CREATOR_ID)
    private Admin creator;

    @Column(name = Columns.INVITATION_CODE)
    private String invitationCode;

    public Tournament() {}

    public Tournament(String name, AccessMode accessMode, Admin creator, String invitationCode) {
        this.name = name;
        this.accessMode = accessMode;
        this.creator = creator;
        this.invitationCode = invitationCode;
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

    @JsonSerialize(using = AdminSerializer.class)
    public Admin getCreator() {
        return creator;
    }

    public String getInvitationCode() {
        return invitationCode;
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

    public void setCreator(Admin creator) {
        this.creator = creator;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String ACCESS_MODE = "access_mode";

        public static final String CREATOR_ID = "creator_id";

        public static final String INVITATION_CODE = "invitation_code";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                accessMode == that.accessMode &&
                Objects.equals(creator, that.creator) &&
                Objects.equals(invitationCode, that.invitationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, accessMode, creator, invitationCode);
    }
}