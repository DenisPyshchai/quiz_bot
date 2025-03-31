package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport(value = "./styles/sizing-margins.css")
public class MenuHorizontal extends HorizontalLayout {

    public MenuHorizontal() {
        super();
        setSize();
    }

    public MenuHorizontal(Component... children) {
        super(children);
        setSize();
    }

    private void setSize() {
        addClassName("menu-horizontal");
        setPadding(false);
        setSpacing(false);
    }
}
