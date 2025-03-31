package com.web.quiz_bot.service.event;

import com.web.quiz_bot.domain.User;
import org.springframework.context.ApplicationEvent;

public class UserDeleteEvent extends ApplicationEvent {

    private final User user;

    public UserDeleteEvent(User user) {
        super(user);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
