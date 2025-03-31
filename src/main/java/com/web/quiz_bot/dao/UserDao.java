package com.web.quiz_bot.dao;

import com.web.quiz_bot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
@Transactional
public interface UserDao extends JpaRepository<User, UUID> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE (LOWER(u.username) LIKE LOWER(:username)) " +
            "OR (LOWER(u.email) LIKE LOWER(:email))")
    boolean checkIfExists(@Param("username") String username, @Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE (LOWER(u.username) LIKE LOWER(:username))")
    boolean checkIfExists(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE (LOWER(u.email) LIKE LOWER(:email))")
    boolean checkIfExistsEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findByUserId(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.username) LIKE LOWER(:username)")
    User findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.email) LIKE LOWER(:email)")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u.verified FROM User u WHERE u.id = :userId")
    boolean isVerified(@Param("userId") UUID userId);

    @Query("SELECT u.emailConfirmed FROM User u WHERE u.id = :userId")
    boolean isEmailConfirmed(@Param("userId") UUID userid);

    @Modifying
    @Query("UPDATE User SET verified = true WHERE id = :userId")
    void verify(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User SET emailConfirmed = :toSet WHERE id = :userId")
    void setConfirmEmail(@Param("userId") UUID userId, @Param("toSet") boolean toSet);

    @Modifying
    @Query("UPDATE User SET name = :name WHERE id = :userId")
    void changeName(@Param("userId") UUID userId, @Param("name") String name);

    @Modifying
    @Query("UPDATE User SET username = :username WHERE id = :userId")
    void changeUsername(@Param("userId") UUID userId, @Param("username") String username);

    @Modifying
    @Query("UPDATE User SET email = :email WHERE id = :userId")
    void changeEmail(@Param("userId") UUID userId, @Param("email") String email);

    @Modifying
    @Query("UPDATE User SET passwordHash = :passwordHash WHERE id = :userId")
    void changePasswordHash(@Param("userId") UUID userId, @Param("passwordHash") String passwordHash);

    @Modifying
    @Query("DELETE User WHERE id = :userId")
    void deleteUser(@Param("userId") UUID userId);
}
