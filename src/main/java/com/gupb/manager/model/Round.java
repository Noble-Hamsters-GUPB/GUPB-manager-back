package com.gupb.manager.model;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = Round.TABLE_NAME)
public class Round {

    public static final String TABLE_NAME = "round";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    @Column(name = Columns.NUMBER)
    private int number;

    @Column(name = Columns.DATE)
    private LocalDateTime date;

    public int getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int getNumber() {
        return number;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String TOURNAMENT_ID = "tournament_id";

        public static final String NUMBER = "number";

        public static final String DATE = "date";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Round round = (Round) o;
        return id == round.id &&
                number == round.number &&
                Objects.equals(tournament, round.tournament) &&
                Objects.equals(date, round.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, number, date);
    }
}