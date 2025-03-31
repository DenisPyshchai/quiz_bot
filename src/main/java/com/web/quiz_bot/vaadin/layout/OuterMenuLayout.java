package com.web.quiz_bot.vaadin.layout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.util.VaadinLayoutUtil;
import com.web.quiz_bot.vaadin.component.*;
import com.web.quiz_bot.vaadin.view.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@UIScope
@PreserveOnRefresh
@Component
public class OuterMenuLayout extends CustomAppLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final MenuHorizontal header = new MenuHorizontal();
    private final Image icon = new QuizBotIcon();
    private final CustomH1 logo = new CustomH1(getTranslation("outer_menu.logo"));
    private final LanguageSelect languageSelect = new LanguageSelect();
    private final NormalButton signUp = new NormalButton(getTranslation("outer_menu.sign_up"),
            e -> UI.getCurrent().navigate(RegisterView.class));
    private final NormalButton login = new NormalButton(getTranslation("outer_menu.log_in"),
            e -> UI.getCurrent().navigate(LoginView.class));
    private final NormalButton cabinet = new NormalButton(getTranslation("outer_menu.cabinet"),
            e -> UI.getCurrent().navigate(GuideView.class));
    private final NormalButton logout = new NormalButton(getTranslation("outer_menu.log_out"), e -> {
        SecurityContextHolder.clearContext();
        UI.getCurrent().close();
        UI.getCurrent().navigate(LoginView.class);
    });

    private CustomTabs tabs = getTabs();

    public OuterMenuLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        icon.setVisible(true);
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.addValueChangeListener(
                c -> UI.getCurrent().getSession().setLocale(c.getValue()));
        signUp.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        signUp.setIcon(new Icon(VaadinIcon.USER_CHECK));
        login.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        login.setIcon(new Icon(VaadinIcon.SIGN_IN));
        cabinet.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cabinet.setIcon(new Icon(VaadinIcon.HOME));
        cabinet.setVisible(false);
        logout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        logout.setIcon(new Icon(VaadinIcon.SIGN_OUT));
        logout.setVisible(false);
        header.add(new CustomDrawerToggle(), icon, logo, languageSelect,
                signUp, login, cabinet, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        addToNavbar(true, header);
    }

    private void createDrawer() {
        addToDrawer(tabs);
    }

    private CustomTabs getTabs() {
        CustomTabs tabs = new CustomTabs();
        tabs.add(
                VaadinLayoutUtil.createTab(VaadinIcon.BROWSER,
                        getTranslation("outer_menu.main_tab"), MainView.class, tabs),
                VaadinLayoutUtil.createTab(VaadinIcon.INFO_CIRCLE_O,
                        getTranslation("outer_menu.about_us_tab"), AboutUsView.class, tabs)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.getStyle().set("margin-top", "3vh");
        return tabs;
    }

    private void changeToStandardButtons() {
        signUp.setVisible(true);
        login.setVisible(true);
        cabinet.setVisible(false);
        logout.setVisible(false);
    }
    private void changeToAuthenticatedButtons() {
        signUp.setVisible(false);
        login.setVisible(false);
        cabinet.setVisible(true);
        logout.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        SecurityContext context = (SecurityContext)
                VaadinSession.getCurrent().getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if (context != null && context.getAuthentication().isAuthenticated()) {
            changeToAuthenticatedButtons();
        } else {
            changeToStandardButtons();
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        logo.setText(getTranslation("outer_menu.logo"));
        signUp.setText(getTranslation("outer_menu.sign_up"));
        login.setText(getTranslation("outer_menu.log_in"));
        cabinet.setText(getTranslation("outer_menu.cabinet"));
        logout.setText(getTranslation("outer_menu.log_out"));
        remove(tabs);
        int currentlySelected = tabs.getSelectedIndex();
        tabs = getTabs();
        addToDrawer(tabs);
        tabs.setSelectedIndex(currentlySelected);
    }
}
