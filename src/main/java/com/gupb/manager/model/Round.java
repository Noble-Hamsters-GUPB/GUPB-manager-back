package com.gupb.manager.model;

import javax.persistence.*;
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

    @Column(name = Columns.COMPLETED_RUNS)
    private int completedRuns;

    @Column(name = Columns.NUMBER_OF_RUNS)
    private int numberOfRuns;

    @Column(name = Columns.DATE)
    private LocalDateTime date;

    @Column(name = Columns.LOGS_PATH)
    private String logsPath;

    public Round() {}

    public Round(Tournament tournament, int number, int numberOfRuns, LocalDateTime date) {
        this.tournament = tournament;
        this.number = number;
        this.numberOfRuns = numberOfRuns;
        this.date = date;
        this.completedRuns = 0;
    }

    public int getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int getNumber() {
        return number;
    }

    public int getCompletedRuns() {
        return completedRuns;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLogsPath() {
        return this.logsPath;
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

    public void setCompletedRuns(int completedRuns) {
        this.completedRuns = completedRuns;
    }

    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLogsPath(String logsPath) {
        this.logsPath = logsPath;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String TOURNAMENT_ID = "tournament_id";

        public static final String NUMBER = "number";

        public static final String COMPLETED_RUNS = "completed_runs";

        public static final String NUMBER_OF_RUNS = "number_of_runs";

        public static final String DATE = "date";

        public static final String LOGS_PATH = "logs_path";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Round round = (Round) o;
        return id == round.id &&
                number == round.number &&
                completedRuns == round.completedRuns &&
                numberOfRuns == round.numberOfRuns &&
                Objects.equals(tournament, round.tournament) &&
                Objects.equals(date, round.date) &&
                Objects.equals(logsPath, round.logsPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, number, completedRuns, numberOfRuns, date, logsPath);
    }
}