package com.web.quiz_bot.service.event;

import com.web.quiz_bot.domain.User;
import org.springframework.context.ApplicationEvent;
import java.util.Locale;

public class UserEmailConformationEvent extends ApplicationEvent {

    private final User user;
    private final Locale locale;

    public UserEmailConformationEvent(User user, Locale locale) {
        super(user);
        this.user = user;
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }

    public Locale getLocale() {
        return locale;
    }

}
