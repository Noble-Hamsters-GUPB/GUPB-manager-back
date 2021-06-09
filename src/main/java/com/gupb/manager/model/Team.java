package com.gupb.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gupb.manager.serializers.StudentsSerializer;
import com.gupb.manager.serializers.TeamsSerializer;
import com.gupb.manager.serializers.TournamentSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = Columns.TOURNAMENT_ID)
    private Tournament tournament;

    @ManyToMany(mappedBy = "teams", fetch = FetchType.EAGER)
    private Set<Student> students;

    @Column(name = Columns.NAME)
    private String name;

    @Column(name = Columns.GITHUB_LINK)
    private String githubLink;

    @Column(name = Columns.MAIN_CLASS_NAME)
    private String mainClassName;

    @Column(name = Columns.BRANCH_NAME)
    private String branchName;

    @Column(name = Columns.PLAYER_NAME, unique = true)
    private String playerName;

    @Column(columnDefinition = "ENUM('IN_TESTING', 'INCOMPLETE', 'READY')", name = Columns.PLAYER_STATUS)
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;

    @Column(name = Columns.LAST_UPDATED)
    private LocalDateTime lastUpdated;

    public static final int MESSAGE_MAX_LENGTH = 2048;

    @Column(name = Columns.MESSAGE, length = MESSAGE_MAX_LENGTH)
    private String message;

    @Column(name = Columns.TOTAL_POINTS)
    private int totalPoints;

    @Column(name = Columns.INVITATION_CODE)
    private String invitationCode;

    public Team() {}

    public Team(String name, String githubLink) {
        this.name = name;
        this.githubLink = githubLink;
    }

    public Team(Tournament tournament, String name, String branchName, String playerName, String githubLink, String mainClassName, String invitationCode) {
        this.tournament = tournament;
        this.name = name;
        this.branchName = branchName;
        this.playerName = playerName;
        this.githubLink = githubLink;
        this.mainClassName = mainClassName;
        this.invitationCode = invitationCode;
    }

    public String getSafeName() {
        return name.replaceAll("[^a-zA-Z0-9]", "");
    }

    public int getId() {
        return id;
    }

    @JsonSerialize(using= TournamentSerializer.class)
    public Tournament getTournament() {
        return tournament;
    }

    @JsonSerialize(using = StudentsSerializer.class)
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

    public String getBranchName() {
        return branchName;
    }

    public String getPlayerName() {
        return playerName;
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

    public String getInvitationCode() {
        return invitationCode;
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

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String TOURNAMENT_ID = "tournament_id";

        public static final String NAME = "name";

        public static final String GITHUB_LINK = "github_link";

        public static final String MAIN_CLASS_NAME = "controller_class_name";

        public static final String BRANCH_NAME = "branch_name";

        public static final String PLAYER_NAME = "player_name";

        public static final String PLAYER_STATUS = "bot_status";

        public static final String LAST_UPDATED = "last_updated";

        public static final String MESSAGE = "message";

        public static final String TOTAL_POINTS = "total_points";

        public static final String INVITATION_CODE = "invitation_code";
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
                Objects.equals(mainClassName, team.mainClassName) &&
                Objects.equals(branchName, team.branchName) &&
                Objects.equals(playerName, team.playerName) &&
                playerStatus == team.playerStatus &&
                Objects.equals(lastUpdated, team.lastUpdated) &&
                Objects.equals(message, team.message) &&
                Objects.equals(invitationCode, team.invitationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, name, githubLink, mainClassName, branchName, playerName, playerStatus, lastUpdated, message, totalPoints, invitationCode);
    }
}
