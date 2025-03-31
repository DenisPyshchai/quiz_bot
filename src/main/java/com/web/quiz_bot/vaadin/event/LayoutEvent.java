package com.web.quiz_bot.vaadin.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public abstract class LayoutEvent extends ComponentEvent<Component> {

    protected LayoutEvent(Component source) {
        super(source, false);
    }
}
