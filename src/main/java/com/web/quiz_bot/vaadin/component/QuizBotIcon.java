package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;

@CssImport(value = "./styles/sizing-margins.css")
public class QuizBotIcon extends Image {

    public QuizBotIcon() {
        super();
        init();
    }

    private void init() {
        addClassName("quiz-bot-icon");
        setSrc("img/quiz-bot-icon.png");
    }
}
