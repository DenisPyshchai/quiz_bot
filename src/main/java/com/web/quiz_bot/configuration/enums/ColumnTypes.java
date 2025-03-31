package com.web.quiz_bot.configuration.enums;

public enum ColumnTypes {
    AUTO,
    TEXT,
    IMAGE;

    @Override
    public String toString() {
        if (this == AUTO) {
            return "Auto";
        } else if (this == TEXT) {
            return "Text";
        } else if (this == IMAGE) {
            return "Image";
        } else {
            return "";
        }
    }
}
