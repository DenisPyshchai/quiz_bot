package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;
import com.web.quiz_bot.vaadin.view.form.RegisterUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Route(value = "register", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
@Component
public class RegisterView extends VerticalLayout implements LocaleChangeObserver {

    private final RegisterUserForm registerUserForm;

    @Autowired
    public RegisterView(RegisterUserForm registerUserForm) {
        setAlignItems(Alignment.CENTER);
        this.registerUserForm = registerUserForm;
        registerUserForm.setWidth("60%");
        registerUserForm.setSpacing(false);
        add(registerUserForm);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        registerUserForm.localeChange(localeChangeEvent);
    }
}
