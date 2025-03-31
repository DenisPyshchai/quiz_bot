package com.web.quiz_bot.service.listener;

import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.request.Request;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.service.event.UserDeleteEvent;
import com.web.quiz_bot.util.RequestUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeleteListener implements ApplicationListener<UserDeleteEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDeleteListener.class.getName());
    private final UserService userService;
    private final QuizServerService quizServerService;

    @Autowired
    public UserDeleteListener(UserService userService, QuizServerService quizServerService) {
        this.userService = userService;
        this.quizServerService = quizServerService;
    }

    @Override
    public void onApplicationEvent(UserDeleteEvent event) {
        User user = event.getUser();
        try {
            JSONObject deleteJSON = new JSONObject();
            deleteJSON.put(JSONKeys.CLIENT_DATA.toString(), "True");
            Request request = new Request(user.getId(), RequestKeywords.DELETE);
            request.setDeleteJson(deleteJSON);
            byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
            if (response == null) {
                throw new Exception("Server is unreachable!");
            }
        } catch (Exception ex) {
            LOGGER.error("Unexpected exception: " + ex.getMessage());
        }
        userService.deleteUser(user.getId());
    }
}
