package com.web.quiz_bot.configuration.enums;

public enum RequestKeywords {
    CREATE,
    UPDATE,
    FETCH,
    STOP,
    DELETE;

    @Override
    public String toString() {
        if (this == CREATE) {
            return "create";
        } else if (this == UPDATE) {
            return "update";
        } else if (this == FETCH) {
            return "fetch";
        } else if (this == STOP) {
            return "stop";
        } else if (this == DELETE) {
            return "delete";
        } else {
            return "";
        }
    }
}
