package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;

@CssImport(value = "./styles/sizing-margins.css")
public class TelegramIcon extends Image {

    public TelegramIcon() {
        super();
        init();
    }

    private void init() {
        addClassName("telegram-icon");
        setSrc("img/telegram-icon.png");
        addClickListener(click -> UI.getCurrent().getPage().open("https://telegram.org/"));
    }
}
