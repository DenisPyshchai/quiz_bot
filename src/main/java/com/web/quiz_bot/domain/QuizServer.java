package com.web.quiz_bot.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "servers", schema = "public")
public class QuizServer {

    @Id
    protected String id;
    @NotEmpty(message = "Auth key hash cannot be empty")
    protected String authKeyHash;

    public String getId() {
        return id;
    }

    public void setId(@NotEmpty String id) {
        this.id = id;
    }

    public String getAuthKeyHash() {
        return authKeyHash;
    }

    public void setAuthKeyHash(@NotEmpty String authKeyHash) {
        this.authKeyHash = authKeyHash;
    }
}
