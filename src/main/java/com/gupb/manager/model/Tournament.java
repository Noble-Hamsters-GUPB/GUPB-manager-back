package com.gupb.manager.model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = Tournament.TABLE_NAME)
public class Tournament {

    public static final String TABLE_NAME = "tournament";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.START_TIME)
    private Date startTime;

    @Column(name = Columns.GITHUB_LINK)
    private String githubLink;

    public int getId() {
        return id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String START_TIME = "start_time";

        public static final String GITHUB_LINK = "github_link";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return id == that.id &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(githubLink, that.githubLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, githubLink);
    }
}