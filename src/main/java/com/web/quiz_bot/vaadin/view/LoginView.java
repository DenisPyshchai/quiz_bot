package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.util.SecurityUtil;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Route(value = "login", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
@Component
public class LoginView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        loginForm.setAction("login");
        loginForm.addLoginListener(this::onLoginEvent);
        add(loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication().isAuthenticated()) {
            UI.getCurrent().navigate(GuideView.class);
        } else {
            if (beforeEnterEvent.getLocation()
                    .getQueryParameters()
                    .getParameters()
                    .containsKey("error")) {
                loginForm.setError(true);
            }
        }
    }

    private void onLoginEvent(LoginForm.LoginEvent loginEvent) {
        boolean authenticated = SecurityUtil.authenticate(
                loginEvent.getUsername(), loginEvent.getPassword());
        if (authenticated) {
            UI.getCurrent().getPage().setLocation("/guide");
            UI.getCurrent().navigate(GuideView.class);
        } else {
            loginForm.setError(true);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        LoginI18n translation = new LoginI18n();
        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle(getTranslation("login.title"));
        form.setUsername(getTranslation("login.username"));
        form.setPassword(getTranslation("login.password"));
        form.setSubmit(getTranslation("login.submit"));
        form.setForgotPassword(getTranslation("login.forgot_password"));
        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(getTranslation("login.error_title"));
        errorMessage.setMessage(getTranslation("login.error_message"));
        translation.setForm(form);
        translation.setErrorMessage(errorMessage);
        loginForm.setI18n(translation);
    }
}
