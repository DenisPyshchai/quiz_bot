package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import com.web.quiz_bot.vaadin.view.form.RedactUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;
import org.springframework.stereotype.Component;

@Route(value = "account-settings", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
@Component
public class AccountSettingsView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final RedactUserForm redactUserForm;

    @Autowired
    public AccountSettingsView(RedactUserForm redactUserForm) {
        setAlignItems(Alignment.CENTER);
        this.redactUserForm = redactUserForm;
        redactUserForm.setWidth("60%");
        add(redactUserForm);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        redactUserForm.localeChange(localeChangeEvent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        redactUserForm.beforeEnter(beforeEnterEvent);
    }
}
