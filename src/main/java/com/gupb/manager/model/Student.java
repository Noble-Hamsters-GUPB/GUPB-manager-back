package com.gupb.manager.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gupb.manager.security.SecurityConfig;
import com.gupb.manager.serializers.TeamsSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = Student.TABLE_NAME)
public class Student {

    public static final String TABLE_NAME = "student";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @JoinTable(name = Columns.STUDENT_TEAM,
            joinColumns = @JoinColumn(name = Columns.STUDENT_ID),
            inverseJoinColumns = @JoinColumn(name = Columns.TEAM_ID))
    @ManyToMany
    private Set<Team> teams;

    @Column(name = Columns.FIRST_NAME)
    private String firstName;

    @Column(name = Columns.LAST_NAME)
    private String lastName;

    @Column(name = Columns.INDEX_NUMBER)
    private String indexNumber;

    @Column(name = Columns.EMAIL_ADDRESS)
    private String emailAddress;

    @Column(name = Columns.PASSWORD)
    private String password;

    public Student() {}

    public Student(Set<Team> teams, String firstName, String lastName,
                   String indexNumber, String emailAddress, String password) {
        this.teams = teams;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.emailAddress = emailAddress;
        setPassword(password);
    }

    public int getId() {
        return id;
    }

    @JsonSerialize(using = TeamsSerializer.class)
    public Set<Team> getTeams() {
        return teams;
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

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
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

    public void setPassword(String password) {
        this.password = SecurityConfig.PASSWORD_ENCODER.encode(password);
    }

    private static class Columns {

        public static final String ID = "id";

        public static final String STUDENT_TEAM = "student_team";

        public static final String STUDENT_ID = "student_id";

        public static final String TEAM_ID = "team_id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String INDEX_NUMBER = "index_number";

        public static final String EMAIL_ADDRESS = "email_address";

        public static final String PASSWORD = "password";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName) &&
                Objects.equals(indexNumber, student.indexNumber) &&
                Objects.equals(emailAddress, student.emailAddress) &&
                Objects.equals(password, student.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, indexNumber, emailAddress, password);
    }
}
