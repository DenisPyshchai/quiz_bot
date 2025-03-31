package com.web.quiz_bot.service;

import com.web.quiz_bot.dao.UserDao;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.domain.data.UserData;
import com.web.quiz_bot.exception.UserAlreadyExists;
import com.web.quiz_bot.service.listener.UserEmailConformationListener;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(UserData userData) throws UserAlreadyExists {
        if (checkIfExists(userData)) {
            throw new UserAlreadyExists("User with this username\\email already exists");
        }
        User user = new User();
        user.setName(userData.getName());
        user.setUsername(userData.getUsername());
        user.setEmail(userData.getEmail());
        user.setRole(userData.getRole());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(userData.getPassword()));
        userDao.save(user);
        return user;
    }

    public User register(UserData userData, boolean testUser) throws UserAlreadyExists {
        if (testUser) {
            Random random = new Random(System.currentTimeMillis());
            int appendix = random.nextInt(100000, 999999);
            String email = userData.getEmail() + String.format("?%s", appendix);
            while (checkIfExistsEmail(email)) {
                appendix = random.nextInt(100000, 999999);
                email = userData.getEmail() + String.format("?%s", appendix);
            }
            userData.setEmail(email);
        }
        if (checkIfExists(userData)) {
            throw new UserAlreadyExists("User with this username\\email already exists");
        }
        User user = new User();
        user.setName(userData.getName());
        user.setUsername(userData.getUsername());
        user.setEmail(userData.getEmail());
        user.setRole(userData.getRole());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(userData.getPassword()));
        if (testUser) {
            user.setVerified(true);
            user.setEmailConfirmed(true);
        }
        userDao.save(user);
        return user;
    }

    public boolean checkIfExists(UserData userData) {
        return userDao.checkIfExists(userData.getUsername(), userData.getEmail());
    }

    public boolean checkIfExists(String username) {
        return userDao.checkIfExists(username);
    }

    public boolean checkIfExistsEmail(String email) {
        return userDao.checkIfExistsEmail(email);
    }

    public boolean checkPassword(UUID userId, String password) {
        return new BCryptPasswordEncoder().matches(password, userDao.findByUserId(userId).getPasswordHash());
    }

    public User getFromUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User getFromEmail(String email) {
        return userDao.findByEmail(email);
    }

    public boolean isVerified(UUID userId) {
        return userDao.isVerified(userId);
    }

    public boolean isEmailConfirmed(UUID userId) {
        return userDao.isEmailConfirmed(userId);
    }

    public void changeName(UUID userId, String name) {
        userDao.changeName(userId, name);
    }

    public void changeUsername(UUID userId, String username) {
        userDao.changeUsername(userId, username);
    }

    public void changeEmail(UUID userId, String email) {
        userDao.changeEmail(userId, email);
        userDao.setConfirmEmail(userId, false);
    }

    public void changePassword(UUID userId, String newPassword) {
        userDao.changePasswordHash(userId, new BCryptPasswordEncoder().encode(newPassword));
    }

    public void verify(UUID userId) {
        userDao.verify(userId);
    }

    public void confirmEmail(UUID userId) {
        userDao.setConfirmEmail(userId, true);
    }

    public void deleteUser(UUID userId) {
        userDao.deleteUser(userId);
    }
}
