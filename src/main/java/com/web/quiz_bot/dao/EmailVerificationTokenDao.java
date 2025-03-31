package com.web.quiz_bot.dao;

import com.web.quiz_bot.domain.EmailVerificationToken;
import com.web.quiz_bot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
@Transactional
public interface EmailVerificationTokenDao extends JpaRepository<EmailVerificationToken, UUID> {

    @Query("SELECT t FROM EmailVerificationToken t WHERE t.user = :user")
    EmailVerificationToken getTokenByUser(@Param("user") User user);
}
