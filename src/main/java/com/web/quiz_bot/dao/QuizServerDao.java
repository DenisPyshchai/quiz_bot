package com.web.quiz_bot.dao;

import com.web.quiz_bot.domain.QuizServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface QuizServerDao extends JpaRepository<QuizServer, UUID> {

    @Query("SELECT s FROM QuizServer s " +
            "WHERE (LOWER(s.id) LIKE LOWER(:serverId))")
    Optional<QuizServer> findById(@Param("serverId") String serverId);
}
