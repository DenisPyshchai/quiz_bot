package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import java.util.Arrays;

@CssImport(value = "./styles/sizing-margins.css")
public class NormalButton extends Button {

    public NormalButton() {
        super();
        init();
    }

    public NormalButton(String text) {
        super(text);
        init();
    }

    public NormalButton(Component icon) {
        super(icon);
        init();
    }

    public NormalButton(String text, Component icon) {
        super(text, icon);
        init();
    }

    public NormalButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        init();
    }

    public NormalButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        init();
    }

    public NormalButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        init();
    }

    private void init() {
        addClassName("normal-button");
    }

    @Override
    public void addThemeVariants(ButtonVariant... variants) {
        if (Arrays.asList(variants).contains(ButtonVariant.LUMO_PRIMARY)) {
            getStyle().set("background", "var(--lumo-primary-color)");
        }
        super.addThemeVariants(variants);
    }
}
