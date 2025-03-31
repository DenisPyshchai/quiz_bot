package com.web.quiz_bot.vaadin.layout;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.domain.data.UserData;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.service.event.UserEmailConformationEvent;
import com.web.quiz_bot.util.SecurityUtil;
import com.web.quiz_bot.util.VaadinLayoutUtil;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.*;
import com.web.quiz_bot.vaadin.event.UserInfoRefreshEvent;
import com.web.quiz_bot.vaadin.view.*;
import org.apache.commons.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@UIScope
@SpringComponent
@PreserveOnRefresh
public class InnerMenuLayout extends CustomAppLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final MenuHorizontal header = new MenuHorizontal();
    private final Image icon = new QuizBotIcon();
    private final CustomH1 logo = new CustomH1();
    private final NormalButton logout = new NormalButton(
            getTranslation("outer_menu.log_out"), e -> SecurityUtil.logout());
    private final NormalButton mainRouteButton = new NormalButton(getTranslation("outer_menu.main_tab"),
            c -> UI.getCurrent().navigate(MainView.class));
    private CustomTabs tabs = getTabs();
    private final LanguageSelect languageSelect = new LanguageSelect();
    private final Text notVerifiedText = new Text(getTranslation("inner_menu.not_verified_text"));
    private String emailValidationErrMessage = getTranslation("register.email_err_message");
    private String emailAlreadyUsed = getTranslation("register.email_already_used");
    private final Dialog notVerifiedDialog = new Dialog();
    private final NormalButton sendNewLink;
    private final NormalButton changeEmail;
    private final NormalTextField newEmail = new NormalTextField();
    private  final Binder<UserData> newEmailBinder = new Binder<>(UserData.class);
    private boolean entered = false;
    private final UserService userService;

    @Autowired
    public InnerMenuLayout(UserService userService, ApplicationEventPublisher eventPublisher) {
        icon.setVisible(true);
        this.userService = userService;
        ComponentUtil.addListener(UI.getCurrent(), UserInfoRefreshEvent.class,
                e -> VaadinViewUtil.setH1NamedLogo(logo, userService, getTranslation("inner_menu.greeting")));
        notVerifiedDialog.setHeaderTitle(getTranslation("inner_menu.not_verified_header"));
        VaadinViewUtil.addDialogCloseButton(notVerifiedDialog);
        sendNewLink = new NormalButton(getTranslation("inner_menu.new_link_button"), c -> {
            User currentUser = VaadinViewUtil.getCurrentUser(userService);
            eventPublisher.publishEvent(new UserEmailConformationEvent(currentUser, UI.getCurrent().getLocale()));
            notVerifiedDialog.close();
        });
        changeEmail = new NormalButton(getTranslation("inner_menu.change_email_button"),
                c -> newEmail.setVisible(true));
        newEmail.setPlaceholder(getTranslation("inner_menu.new_email"));
        newEmail.setVisible(false);
        newEmail.setSizeFull();
        newEmail.setSuffixComponent(new HorizontalLayout(
                new Button(new Icon(VaadinIcon.CHECK), c -> {
                    if (newEmailBinder.validate().isOk()) {
                        newEmail.setVisible(false);
                        User currentUser = VaadinViewUtil.getCurrentUser(userService);
                        currentUser.setEmail(newEmail.getValue());
                        userService.changeEmail(currentUser.getId(), newEmail.getValue());
                        eventPublisher.publishEvent(new UserEmailConformationEvent(
                                currentUser, UI.getCurrent().getLocale()));
                        notVerifiedDialog.close();
                    }
                }),
                new Button(new Icon(VaadinIcon.CLOSE), c -> newEmail.setVisible(false))
        ));
        VerticalLayout layout = new VerticalLayout(notVerifiedText, new CustomHr(),
                new HorizontalLayout(sendNewLink, changeEmail, newEmail));
        layout.setAlignItems(Alignment.CENTER);
        layout.setAlignSelf(Alignment.CENTER);
        notVerifiedDialog.add(layout);
        configureBinder();
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        VaadinViewUtil.setH1NamedLogo(logo, userService, getTranslation("inner_menu.greeting"));
        logout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        logout.setIcon(new Icon(VaadinIcon.SIGN_OUT));
        mainRouteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        mainRouteButton.setIcon(new Icon(VaadinIcon.BROWSER));
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.addValueChangeListener(
                c -> UI.getCurrent().getSession().setLocale(c.getValue()));
        header.add(new CustomDrawerToggle(), icon, logo, languageSelect, mainRouteButton, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(tabs);
        getStyle().set("--app-layout-drawer-offset-size", "15vw");
    }

    private CustomTabs getTabs() {
        CustomTabs tabs = new CustomTabs();
        tabs.add(
                VaadinLayoutUtil.createTab(
                        VaadinIcon.QUESTION,
                        getTranslation("inner_menu.guide_tab"), GuideView.class, tabs),
                VaadinLayoutUtil.createTab(
                        VaadinIcon.ABACUS,
                        getTranslation("inner_menu.manage_quizzes_tab"), QuizManagerView.class, tabs),
                VaadinLayoutUtil.createTab(
                        VaadinIcon.QRCODE,
                        getTranslation("inner_menu.qr_collection_tab"), QRCollectionView.class, tabs),
                VaadinLayoutUtil.createTab(
                        VaadinIcon.LIST_OL,
                        getTranslation("inner_menu.new_quiz_tab"), NewQuizView.class, tabs),
                VaadinLayoutUtil.createTab(
                        VaadinIcon.USER,
                        getTranslation("inner_menu.account_settings_tab"), AccountSettingsView.class, tabs)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.getStyle().set("margin-top", "3vh");
        return tabs;
    }

    private void configureBinder() {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        newEmailBinder.forField(newEmail).withValidator(email -> EmailValidator.getInstance().isValid(email),
                emailValidationErrMessage).withValidator(email ->
                        (!userService.checkIfExistsEmail(email) || email.equals(currentUser.getEmail())),
                emailAlreadyUsed).bind(UserData::getEmail, UserData::setEmail);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!entered) {
            if (!VaadinViewUtil.getCurrentUser(userService).isVerified()) {
                notVerifiedDialog.open();
            }
            entered = true;
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        VaadinViewUtil.setH1NamedLogo(logo, userService, getTranslation("inner_menu.greeting"));
        logout.setText(getTranslation("outer_menu.log_out"));
        mainRouteButton.setText(getTranslation("outer_menu.main_tab"));
        notVerifiedDialog.setHeaderTitle(getTranslation("inner_menu.not_verified_header"));
        notVerifiedText.setText(getTranslation("inner_menu.not_verified_text"));
        sendNewLink.setText(getTranslation("inner_menu.new_link_button"));
        changeEmail.setText(getTranslation("inner_menu.change_email_button"));
        newEmail.setPlaceholder(getTranslation("inner_menu.new_email"));
        remove(tabs);
        int currentlySelected = tabs.getSelectedIndex();
        tabs = getTabs();
        addToDrawer(tabs);
        tabs.setSelectedIndex(currentlySelected);
        emailValidationErrMessage = getTranslation("register.email_err_message");
        emailAlreadyUsed = getTranslation("register.email_already_used");
        configureBinder();
    }
}
