package com.web.quiz_bot.domain;

import org.springframework.transaction.annotation.Transactional;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Transactional
@Table(name = "tokens", schema = "public")
public class EmailVerificationToken {

    private static final int EXPIRATION = 60 * 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;

    @NotNull
    private Date expirationDate = Date.from(Instant.now().plusSeconds(EXPIRATION));

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
