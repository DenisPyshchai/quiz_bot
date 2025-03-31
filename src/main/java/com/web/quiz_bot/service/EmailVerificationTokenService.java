package com.web.quiz_bot.service;

import com.web.quiz_bot.dao.EmailVerificationTokenDao;
import com.web.quiz_bot.domain.EmailVerificationToken;
import com.web.quiz_bot.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {

    private final EmailVerificationTokenDao tokenDao;

    @Autowired
    public EmailVerificationTokenService(EmailVerificationTokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public EmailVerificationToken createToken(User user) {
        EmailVerificationToken token = tokenDao.getTokenByUser(user);
        if (token != null) {
            tokenDao.delete(token);
        }
        token = new EmailVerificationToken();
        token.setUser(user);
        tokenDao.save(token);
        return token;
    }

    public EmailVerificationToken getTokenById(UUID tokenId) {
        return tokenDao.findById(tokenId).orElse(null);
    }

    public void deleteTokenById(UUID tokenId) {
        tokenDao.deleteById(tokenId);
    }
}
