package com.gupb.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gupb.manager.serializers.TeamSerializer;
import com.gupb.manager.serializers.TournamentSerializer;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Requirement.TABLE_NAME)
public class Requirement {

    public static final String TABLE_NAME = "requirement";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.PACKAGE_INFO)
    private String packageInfo;

    @Column(columnDefinition = "ENUM('VALID', 'PENDING', 'DECLINED')", name = Columns.STATUS)
    @Enumerated(EnumType.STRING)
    private RequirementStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TEAM_ID)
    private Team requestedBy;

    public Requirement() {}

    public Requirement(String packageInfo, RequirementStatus status, Tournament tournament, Team requestedBy) {
        this.packageInfo = packageInfo;
        this.status = status;
        this.tournament = tournament;
        this.requestedBy = requestedBy;
    }

    public int getId() {
        return id;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public RequirementStatus getStatus() {
        return status;
    }

    @JsonSerialize(using = TournamentSerializer.class)
    public Tournament getTournament() {
        return tournament;
    }

    @JsonSerialize(using = TeamSerializer.class)
    public Team getRequestedBy() {
        return requestedBy;
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

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void setRequestedBy(Team requestedBy) {
        this.requestedBy = requestedBy;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String PACKAGE_INFO = "package_info";

        public static final String STATUS = "status";

        public static final String TOURNAMENT_ID = "tournament_id";

        public static final String TEAM_ID = "team_id";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement that = (Requirement) o;
        return id == that.id &&
                Objects.equals(packageInfo, that.packageInfo) &&
                status == that.status &&
                Objects.equals(tournament, that.tournament) &&
                Objects.equals(requestedBy, that.requestedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, packageInfo, status, tournament, requestedBy);
    }
}
