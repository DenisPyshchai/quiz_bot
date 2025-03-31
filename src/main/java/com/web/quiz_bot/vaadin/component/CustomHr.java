package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Hr;

@CssImport(value = "./styles/sizing-margins.css")
public class CustomHr extends Hr {

    public CustomHr() {
        super();
        init();
    }

    private void init() {
        addClassName("hr");
    }
}
