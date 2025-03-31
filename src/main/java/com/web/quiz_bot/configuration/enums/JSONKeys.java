package com.web.quiz_bot.configuration.enums;

public enum JSONKeys {
    CLIENT_DATA,
    SHUTDOWN,
    SERVER_ID,
    AUTH_KEY,
    SERVER_URL,
    USER_IDENTIFIER,
    REQUEST_KEYWORD,
    TABLE_JSON,
    QUIZ_JSON,
    MULTIPLE_REGISTRATION,
    ANSWERS,
    CUSTOM_ANSWER,
    FORMAT_MESSAGE,
    TABLE_COLUMN,
    TABLE_NAME,
    COLUMN_ORDER,
    DONE_MESSAGE,
    GREETING,
    FAREWELL,
    FETCH_WHAT,
    TABLES,
    BOTS,
    ALL_ENTRIES,
    NEW_ENTRIES;

    @Override
    public String toString() {
        if (this == CLIENT_DATA) {
            return "client_data";
        } else if (this == SHUTDOWN) {
            return "shutdown";
        } else if (this == SERVER_ID) {
            return "server_id";
        } else if (this == AUTH_KEY) {
            return "auth_key";
        } else if (this == SERVER_URL) {
            return "server_url";
        } else if (this == USER_IDENTIFIER) {
            return "user_identifier";
        } else if (this == REQUEST_KEYWORD) {
            return "request_keyword";
        } else if (this == TABLE_JSON) {
            return "table_json";
        } else if (this == QUIZ_JSON) {
            return "quiz_json";
        } else if (this == MULTIPLE_REGISTRATION) {
            return "multiple_registration";
        } else if (this == ANSWERS) {
            return "answers";
        } else if (this == CUSTOM_ANSWER) {
            return "custom_answer";
        } else if (this == FORMAT_MESSAGE) {
            return "format_message";
        } else if (this == TABLE_COLUMN) {
            return "table_column";
        } else if (this == TABLE_NAME) {
            return "table_name";
        } else if (this == COLUMN_ORDER) {
            return "column_order";
        } else if (this == DONE_MESSAGE) {
            return "done_message";
        } else if (this == GREETING) {
            return "greeting";
        } else if (this == FAREWELL) {
            return "farewell";
        } else if (this == FETCH_WHAT) {
            return "fetch_what";
        } else if (this == TABLES) {
            return "tables";
        } else if (this == BOTS) {
            return "bots";
        } else if (this == ALL_ENTRIES) {
            return "all_entries";
        } else if (this == NEW_ENTRIES) {
            return "new_entries";
        } else {
            return "";
        }
    }
}
