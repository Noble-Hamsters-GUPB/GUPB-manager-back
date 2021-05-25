package com.gupb.manager.model;

import com.gupb.manager.security.SecurityConfig;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = Admin.TABLE_NAME)
public class Admin {

    public static final String TABLE_NAME = "admin";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    private int id;

    @Column(name = Columns.FIRST_NAME)
    private String firstName;

    @Column(name = Columns.LAST_NAME)
    private String lastName;

    @Column(name = Columns.EMAIL_ADDRESS)
    private String emailAddress;

    @Column(name = Columns.PASSWORD)
    private String password;

    public Admin() {}

    public Admin(String firstName, String lastName, String emailAddress, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        setPassword(password);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = SecurityConfig.PASSWORD_ENCODER.encode(password);
    }

    private static class Columns {

        public static final String ID = "id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String EMAIL_ADDRESS = "email_address";

        public static final String PASSWORD = "password";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id &&
                Objects.equals(firstName, admin.firstName) &&
                Objects.equals(lastName, admin.lastName) &&
                Objects.equals(emailAddress, admin.emailAddress) &&
                Objects.equals(password, admin.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, emailAddress, password);
    }
}
