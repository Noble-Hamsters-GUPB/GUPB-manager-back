package com.gupb.manager.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Team.TABLE_NAME)
public class Team {

    public static final String TABLE_NAME = "team";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.NAME)
    private String name;

    @Column(name = Columns.GITHUB_LINK)
    private String githubLink;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String GITHUB_LINK = "github_link";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id &&
                Objects.equals(name, team.name) &&
                Objects.equals(githubLink, team.githubLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, githubLink);
    }
}
