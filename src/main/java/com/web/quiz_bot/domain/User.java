package com.web.quiz_bot.domain;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Transactional
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;
    protected String name;
    @NotEmpty(message = "Username may not be empty")
    @Column(unique = true)
    protected String username;
    @NotEmpty(message = "Email may not be empty")
    @Column(unique = true)
    protected String email;
    @NotNull(message = "Email is either confirmed or not")
    protected boolean emailConfirmed = false;
    @NotEmpty(message = "Password may not be empty")
    protected String passwordHash;
    @NotNull(message = "User is either verified or not")
    protected boolean verified = false;
    @NotEmpty(message = "User must have a role")
    protected String role;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty String email) {
        this.email = email;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(@NotEmpty String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
