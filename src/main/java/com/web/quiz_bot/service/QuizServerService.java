package com.web.quiz_bot.service;

import com.web.quiz_bot.dao.QuizServerDao;
import com.web.quiz_bot.domain.QuizServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Service
public class QuizServerService {

    private final QuizServerDao quizServerDao;
    private static final HashMap<UUID, URL> activeQuizServers = new HashMap<>();

    @Autowired
    public QuizServerService(QuizServerDao quizServerDao) {
        this.quizServerDao = quizServerDao;
    }

    public boolean authenticate(UUID serverId, String authKey, URL serverUrl) {
        QuizServer server = quizServerDao.findById(serverId.toString()).orElse(null);
        if (server == null) {
            System.out.println("No such server was registered");
            return false;
        } else if (new BCryptPasswordEncoder().matches(authKey, server.getAuthKeyHash())) {
            activeQuizServers.put(serverId, serverUrl);
            return true;
        }
        System.out.println("Auth key does not match");
        return false;
    }

    public boolean authenticate(UUID serverId, String authKey) {
        QuizServer server = quizServerDao.findById(serverId.toString()).orElse(null);
        if (server == null) {
            System.out.println("No such server was registered");
            return false;
        } else if (new BCryptPasswordEncoder().matches(authKey, server.getAuthKeyHash())) {
            return true;
        }
        System.out.println("Auth key does not match");
        return false;
    }

    public void remove(UUID serverId) {
        activeQuizServers.remove(serverId);
    }

    public URL getRandomServerURL() {
        if (activeQuizServers.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return activeQuizServers.values().stream()
                .skip(random.nextInt(activeQuizServers.size()))
                .findFirst().orElse(null);
    }
}
