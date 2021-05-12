package com.gupb.manager.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = Team.TABLE_NAME)
public class Team {

    public static final String TABLE_NAME = "team";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    @Column(name = Columns.NAME)
    private String name;

    @Column(name = Columns.GITHUB_LINK)
    private String githubLink;

    @Column(name = Columns.PACKAGE_NAME)
    private String packageName;

    @Column(name = Columns.CONTROLLER_CLASS_NAME)
    private String controllerClassName;

    @Column(name = Columns.BOT_NAME)
    private String botName;

    @Column(columnDefinition = "ENUM('IN_TESTING', 'INCOMPLETE', 'READY')", name = Columns.BOT_STATUS)
    @Enumerated(EnumType.STRING)
    private BotStatus botStatus;

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

    public int getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public String getName() {
        return name;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getControllerClassName() {
        return controllerClassName;
    }

    public String getBotName() {
        return botName;
    }

    public BotStatus getBotStatus() {
        return botStatus;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setControllerClassName(String controllerClassName) {
        this.controllerClassName = controllerClassName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setBotStatus(BotStatus botStatus) {
        this.botStatus = botStatus;
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

        public static final String CONTROLLER_CLASS_NAME = "controller_class_name";

        public static final String BOT_NAME = "bot_name";

        public static final String BOT_STATUS = "bot_status";

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
                Objects.equals(name, team.name) &&
                Objects.equals(githubLink, team.githubLink) &&
                Objects.equals(packageName, team.packageName) &&
                Objects.equals(controllerClassName, team.controllerClassName) &&
                Objects.equals(botName, team.botName) &&
                botStatus == team.botStatus &&
                Objects.equals(lastUpdated, team.lastUpdated) &&
                Objects.equals(message, team.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, name, githubLink, packageName, controllerClassName, botName, botStatus, lastUpdated, message, totalPoints);
    }
}
