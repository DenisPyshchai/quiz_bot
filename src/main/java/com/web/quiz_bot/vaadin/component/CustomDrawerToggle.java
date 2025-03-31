package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport(value = "./styles/sizing-margins.css")
public class CustomDrawerToggle extends DrawerToggle {

    public CustomDrawerToggle() {
        super();
        init();
    }

    private void init() {
        addClassName("drawer-toggle");
    }
}
