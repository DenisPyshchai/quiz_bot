package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;

@CssImport(value = "./styles/sizing-margins.css")
public class CustomH1 extends H1 {

    public CustomH1() {
        super();
        init();
    }

    public CustomH1(Component... components) {
        super(components);
        init();
    }

    public CustomH1(String text) {
        super(text);
        init();
    }

    private void init(){
        addClassName("h1");
    }
}
