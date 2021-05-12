package com.gupb.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gupb.manager.serializers.TeamSerializer;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Student.TABLE_NAME)
public class Student {

    public static final String TABLE_NAME = "student";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @JoinColumn(name = Columns.TEAM_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Column(name = Columns.FIRST_NAME)
    private String firstName;

    @Column(name = Columns.LAST_NAME)
    private String lastName;

    @Column(name = Columns.INDEX_NUMBER)
    private String indexNumber;

    @Column(name = Columns.EMAIL_ADDRESS)
    private String emailAddress;

    public Student() {}

    public Student(Team team, String firstName, String lastName, String indexNumber, String emailAddress) {
        this.team = team;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.emailAddress = emailAddress;
    }

    public int getId() {
        return id;
    }

    @JsonSerialize(using = TeamSerializer.class)
    public Team getTeam() {
        return team;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIndexNumber() { return indexNumber; }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    private static class Columns {

        public static final String ID = "id";

        public static final String TEAM_ID = "team_id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String INDEX_NUMBER = "index_number";

        public static final String EMAIL_ADDRESS = "email_address";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                Objects.equals(team, student.team) &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName) &&
                Objects.equals(indexNumber, student.indexNumber) &&
                Objects.equals(emailAddress, student.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team, firstName, lastName, indexNumber, emailAddress);
    }
}
