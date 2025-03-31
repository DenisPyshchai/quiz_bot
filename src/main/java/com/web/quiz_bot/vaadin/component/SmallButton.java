package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import java.util.Arrays;

@CssImport(value = "./styles/sizing-margins.css")
public class SmallButton extends Button {

    public SmallButton() {
        super();
        init();
    }

    public SmallButton(String text) {
        super(text);
        init();
    }

    public SmallButton(Component icon) {
        super(icon);
        init();
    }

    public SmallButton(String text, Component icon) {
        super(text, icon);
        init();
    }

    public SmallButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        init();
    }

    public SmallButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        init();
    }

    public SmallButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        init();
    }

    private void init() {
        addClassName("small-button");
    }

    @Override
    public void addThemeVariants(ButtonVariant... variants) {
        if (Arrays.asList(variants).contains(ButtonVariant.LUMO_PRIMARY)) {
            getStyle().set("background", "var(--lumo-primary-color)");
        }
        super.addThemeVariants(variants);
    }
}
