package com.web.quiz_bot.configuration.enums;

public enum Formats {

    ANY,
    ONLY_LETTERS,
    INTEGER,
    DATE,
    NAME,
    PHONE_NUMBER,
    EMAIL_ADDRESS;

    @Override
    public String toString() {
        if (this == ANY) {
            return "Any";
        } else if (this == ONLY_LETTERS) {
            return "Only letters";
        } else if (this == INTEGER) {
            return "Integer";
        } else if (this == DATE) {
            return "Date";
        } else if (this == NAME) {
            return "Name";
        } else if (this == PHONE_NUMBER) {
            return "Phone number";
        } else if (this == EMAIL_ADDRESS) {
            return "Email address";
        } else {
            return "unknown_format";
        }
    }
}
