package com.gupb.manager.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = Team.TABLE_NAME)
public class Team {

    public static final String TABLE_NAME = "team";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    @ManyToMany(mappedBy = "teams")
    private Set<Student> students;

    @Column(name = Columns.NAME)
    private String name;

    @Column(name = Columns.GITHUB_LINK)
    private String githubLink;

    @Column(name = Columns.MAIN_CLASS_NAME)
    private String mainClassName;

    @Column(columnDefinition = "ENUM('IN_TESTING', 'INCOMPLETE', 'READY')", name = Columns.PLAYER_STATUS)
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;

    @Column(name = Columns.LAST_UPDATED)
    private LocalDateTime lastUpdated;

    @Column(name = Columns.MESSAGE)
    private String message;

    @Column(name = Columns.TOTAL_POINTS)
    private int totalPoints;

    public Team() {}

    public Team(String name, String githubLink) {
        this.name = name;
        this.githubLink = githubLink;
    }

    public Team(Tournament tournament, String name, String githubLink, String mainClassName) {
        this.tournament = tournament;
        this.name = name;
        this.githubLink = githubLink;
        this.mainClassName = mainClassName;
    }

    public String getSafeName() {
        return name.replaceAll("[^a-zA-Z0-9]", "");
    }

    public int getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public String getName() {
        return name;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public void setMainClassName(String controllerClassName) {
        this.mainClassName = controllerClassName;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String TOURNAMENT_ID = "tournament_id";

        public static final String NAME = "name";

        public static final String GITHUB_LINK = "github_link";

        public static final String PACKAGE_NAME = "package_name";

        public static final String MAIN_CLASS_NAME = "controller_class_name";

        public static final String PLAYER_NAME = "bot_name";

        public static final String PLAYER_STATUS = "bot_status";

        public static final String LAST_UPDATED = "last_updated";

        public static final String MESSAGE = "message";

        public static final String TOTAL_POINTS = "total_points";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id &&
                totalPoints == team.totalPoints &&
                Objects.equals(tournament, team.tournament) &&
                Objects.equals(students, team.students) &&
                Objects.equals(name, team.name) &&
                Objects.equals(githubLink, team.githubLink) &&
                Objects.equals(mainClassName, team.mainClassName) &&
                playerStatus == team.playerStatus &&
                Objects.equals(lastUpdated, team.lastUpdated) &&
                Objects.equals(message, team.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, students, name, githubLink, mainClassName, playerStatus, lastUpdated, message, totalPoints);
    }
}
