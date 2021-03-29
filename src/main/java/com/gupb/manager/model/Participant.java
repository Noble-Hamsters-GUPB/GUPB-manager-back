package com.gupb.manager.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Participant.TABLE_NAME)
public class Participant {

    public static final String TABLE_NAME = "participant";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TEAM_ID)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    public int getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String TEAM_ID = "team_id";

        public static final String TOURNAMENT_ID = "tournament_id";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return id == that.id &&
                Objects.equals(team, that.team) &&
                Objects.equals(tournament, that.tournament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team, tournament);
    }
}
