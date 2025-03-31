package com.web.quiz_bot.servlet;

import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.service.QuizServerService;
import org.json.JSONObject;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@WebServlet(urlPatterns = "/*", name = "communicationServlet", asyncSupported = true)
public class CommunicationServlet extends HttpServlet {

    private final QuizServerService quizServerService;

    public CommunicationServlet(QuizServerService quizServerService) {
        this.quizServerService = quizServerService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject data = new JSONObject(request.getReader().lines().reduce("", String::concat));
        boolean authenticated;
        if (data.has(JSONKeys.SERVER_URL.toString()) && !data.isNull(JSONKeys.SERVER_URL.toString())) {
            authenticated = authenticate(
                    UUID.fromString(data.getString(JSONKeys.SERVER_ID.toString())),
                    data.getString(JSONKeys.AUTH_KEY.toString()),
                    new URL(data.getString(JSONKeys.SERVER_URL.toString())));
        } else {
            authenticated = authenticate(
                    UUID.fromString(data.getString(JSONKeys.SERVER_ID.toString())),
                    data.getString(JSONKeys.AUTH_KEY.toString()), null);
        }
        if (!authenticated) {
            response.sendError(401, "Authentication failed, wrong credentials");
        }
        if (request.getHeader(JSONKeys.CLIENT_DATA.toString()).equalsIgnoreCase("true")) {
            response.setStatus(200);
        }
        if (request.getHeader(JSONKeys.SHUTDOWN.toString()).equalsIgnoreCase("true")) {
            quizServerService.remove(UUID.fromString(data.getString(JSONKeys.SERVER_ID.toString())));
            response.setStatus(200);
        }
        response.getWriter().println("POST");
    }

    private boolean authenticate(UUID serverId, String authKey, URL serverUrl) {
        if (serverUrl == null) {
            return quizServerService.authenticate(serverId, authKey);
        } else {
            return quizServerService.authenticate(serverId, authKey, serverUrl);
        }
    }
}
