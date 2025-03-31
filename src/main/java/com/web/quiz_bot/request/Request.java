package com.web.quiz_bot.request;

import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import org.json.JSONObject;

import java.util.UUID;

public class Request {

    private JSONObject tableJson = null;
    private JSONObject quizJson = null;
    private JSONObject updateJson = null;
    private JSONObject fetchJson = null;
    private JSONObject deleteJson = null;
    private final UUID userIdentifier;
    private final RequestKeywords requestKeyword;
    public Request(UUID userIdentifier, RequestKeywords requestKeyword) {
        this.userIdentifier = userIdentifier;
        this.requestKeyword = requestKeyword;
    }

    public UUID getUserIdentifier() {
        return userIdentifier;
    }

    public RequestKeywords getRequestKeyword() {
        return requestKeyword;
    }

    public JSONObject getTableJson() {
        return tableJson;
    }

    public JSONObject getQuizJson() {
        return quizJson;
    }

    public JSONObject getUpdateJson() {
        return updateJson;
    }

    public JSONObject getFetchJson() {
        return fetchJson;
    }

    public JSONObject getDeleteJson() {
        return deleteJson;
    }

    public void setTableJson(JSONObject tableJson) {
        this.tableJson = tableJson;
    }

    public void setQuizJson(JSONObject quizJson) {
        this.quizJson = quizJson;
    }

    public void setUpdateJson(JSONObject updateJson) {
        this.updateJson = updateJson;
    }

    public void setFetchJson(JSONObject fetchJson) {
        this.fetchJson = fetchJson;
    }

    public void setDeleteJson(JSONObject deleteJson) {
        this.deleteJson = deleteJson;
    }

    public JSONObject getRequest() {
        JSONObject request = null;
        if (requestKeyword == RequestKeywords.CREATE) {
            request = new JSONObject();
            request.put(JSONKeys.USER_IDENTIFIER.toString(), userIdentifier.toString());
            request.put(JSONKeys.REQUEST_KEYWORD.toString(), requestKeyword.toString());
            request.put(JSONKeys.TABLE_JSON.toString(), tableJson);
            request.put(JSONKeys.QUIZ_JSON.toString(), quizJson);
        } else if (requestKeyword == RequestKeywords.UPDATE) {
            request = updateJson;
            request.put(JSONKeys.USER_IDENTIFIER.toString(), userIdentifier.toString());
            request.put(JSONKeys.REQUEST_KEYWORD.toString(), requestKeyword.toString());
        } else if (requestKeyword == RequestKeywords.FETCH) {
            request = fetchJson;
            request.put(JSONKeys.USER_IDENTIFIER.toString(), userIdentifier.toString());
            request.put(JSONKeys.REQUEST_KEYWORD.toString(), requestKeyword.toString());
        } else if (requestKeyword == RequestKeywords.DELETE) {
            request = deleteJson;
            request.put(JSONKeys.USER_IDENTIFIER.toString(), userIdentifier.toString());
            request.put(JSONKeys.REQUEST_KEYWORD.toString(), requestKeyword.toString());
        }
        return request;
    }

    @Override
    public String toString() {
        return getRequest().toString();
    }
}
