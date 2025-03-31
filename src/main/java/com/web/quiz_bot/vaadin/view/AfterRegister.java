package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.CustomH1;
import com.web.quiz_bot.vaadin.component.NormalButton;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;

@Route(value = "successfully-registered")
@PermitAll
public class AfterRegister extends VerticalLayout implements LocaleChangeObserver {

    private final H1 greeting = new H1();
    private final H1 verifyEmail = new H1();
    private final NormalButton mainRoute = new NormalButton(getTranslation("outer_menu.main_tab"),
            c -> UI.getCurrent().navigate(MainView.class));
    private final UserService userService;

    @Autowired
    public AfterRegister(UserService userService) {
        this.userService = userService;
        VaadinViewUtil.setH1NamedLogo(greeting, userService, getTranslation("after_register.greeting"));
        mainRoute.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        mainRoute.setIcon(new Icon(VaadinIcon.BROWSER));
        add(greeting, verifyEmail, new Hr(), mainRoute);
        this.setAlignItems(Alignment.CENTER);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        VaadinViewUtil.setH1NamedLogo(greeting, userService, getTranslation("after_register.greeting"));
        verifyEmail.setText(getTranslation("after_register.verify_email"));
        mainRoute.setText(getTranslation("outer_menu.main_tab"));
    }
}
