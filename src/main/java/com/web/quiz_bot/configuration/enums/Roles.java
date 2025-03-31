package com.web.quiz_bot.configuration.enums;

public enum Roles {
    ADMIN,
    USER;

    @Override
    public String toString() {
        if (this == ADMIN) {
            return "admin";
        } else if (this == USER) {
            return "user";
        } else {
            return "";
        }
    }
}
