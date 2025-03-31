package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.domain.data.UserData;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.service.event.UserDeleteEvent;
import com.web.quiz_bot.service.event.UserEmailConformationEvent;
import com.web.quiz_bot.util.SecurityUtil;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.CustomHr;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalPasswordField;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.event.UserInfoRefreshEvent;
import com.web.quiz_bot.vaadin.view.LoginView;
import me.gosimple.nbvcxz.Nbvcxz;
import org.apache.commons.validator.EmailValidator;
import org.springframework.context.ApplicationEventPublisher;
import java.util.concurrent.Callable;

@SpringComponent
@UIScope
public class RedactUserForm extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final NormalTextField name = new NormalTextField(getTranslation("account_settings.name"));
    private final NormalTextField username = new NormalTextField(getTranslation("account_settings.username"));
    private final NormalTextField email = new NormalTextField(getTranslation("account_settings.email"));
    private final NormalPasswordField password = new NormalPasswordField(getTranslation("account_settings.password"));
    private final NormalButton deleteAccount = new NormalButton(getTranslation("account_settings.delete_account"));
    private final Dialog deleteAccountDialog = new Dialog();
    private final Text deleteAccountText = new Text(getTranslation("account_settings.delete_account_dialog.2"));
    private final NormalButton deleteAccountYes = new NormalButton(getTranslation("account_settings.delete_account_yes"));
    private final NormalButton deleteAccountNo = new NormalButton(getTranslation("account_settings.delete_account_no"));
    private final NormalPasswordField newPassword = new NormalPasswordField(getTranslation("account_settings.new_password"));
    private final HorizontalLayout newPasswordButtons = new HorizontalLayout();
    private final NormalButton saveNewPassword = new NormalButton(getTranslation("register.save"));
    private final NormalButton cancelNewPassword = new NormalButton(getTranslation("register.cancel"));
    private final NormalButton sendNewLink = new NormalButton(getTranslation("inner_menu.new_link_button"));
    private final NormalButton changeEmail = new NormalButton(getTranslation("inner_menu.change_email_button"));
    private final NormalTextField newEmail = new NormalTextField();

    private static final String NAME_VALIDATION_PATTERN = "^([a-zA-Z]+\\s?)+$";
    private String nameValidationErrMessage = getTranslation("register.name_err_message");
    private static final String USERNAME_VALIDATION_PATTERN = "^[a-zA-Z\\d_.-]*$";
    private String usernameValidationErrMessage = getTranslation("register.username_err_message");
    private String usernameAlreadyUsed = getTranslation("register.username_already_used");
    private String emailValidationErrMessage = getTranslation("register.email_err_message");
    private String emailAlreadyUsed = getTranslation("register.email_already_used");
    private String passwordValidationErrMessage = getTranslation("account_settings.password_err_message");
    private String newPasswordValidationErrMessage = getTranslation("register.password_err_message");
    private String samePasswordValidationErrMessage = getTranslation("account_settings.new_password_same");

    private final Binder<UserData> nameBinder = new Binder<>(UserData.class);
    private final Binder<UserData> usernameBinder = new Binder<>(UserData.class);
    private final Binder<UserData> emailBinder = new Binder<>(UserData.class);
    private final Binder<UserData> passwordBinder = new Binder<>(UserData.class);
    private final Binder<UserData> newEmailBinder = new Binder<>(UserData.class);

    private final Nbvcxz nbvcxz = new Nbvcxz();
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private User user;

    public RedactUserForm(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }
        this.user = currentUser;
        setAlignItems(Alignment.CENTER);
        addUserFields();
        configureBinders();
    }

    private void addUserFields() {
        name.setValue(user.getName());
        name.setReadOnly(true);
        addEditButton(name, nameBinder, () -> {
            userService.changeName(user.getId(), name.getValue());
            ComponentUtil.fireEvent(UI.getCurrent(), new UserInfoRefreshEvent(this));
            return null;
        });
        username.setValue(user.getUsername());
        username.setReadOnly(true);
        addEditButton(username, usernameBinder, () -> {
            userService.changeUsername(user.getId(), username.getValue());
            ComponentUtil.fireEvent(UI.getCurrent(), new UserInfoRefreshEvent(this));
            return null;
        });
        if (user.getEmail().contains(RegisterUserForm.DEMO_USER_EMAIL)) {
            email.setValue(RegisterUserForm.DEMO_USER_EMAIL);
        } else {
            email.setValue(user.getEmail());
            addEditButton(email, emailBinder, () -> {
                userService.changeEmail(user.getId(), email.getValue());
                eventPublisher.publishEvent(new UserEmailConformationEvent(
                        userService.getFromUsername(user.getUsername()), UI.getCurrent().getLocale()));
                ComponentUtil.fireEvent(UI.getCurrent(), new UserInfoRefreshEvent(this));
                return null;
            });
        }
        email.setReadOnly(true);
        password.setValue(getTranslation("account_settings.password"));
        password.setReadOnly(true);
        password.setRevealButtonVisible(false);
        Button changePassword = new Button(new Icon(VaadinIcon.EDIT), e -> {
            password.clear();
            password.setReadOnly(false);
            password.setRevealButtonVisible(true);
            password.setInvalid(false);
            add(newPassword, newPasswordButtons);
        });
        changePassword.setDisableOnClick(true);
        password.setSuffixComponent(changePassword);
        deleteAccount.addClickListener(click -> {
            if (!deleteAccountDialog.isOpened()) {
                deleteAccountDialog.open();
            }
        });
        deleteAccount.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountDialog.setHeaderTitle(getTranslation("account_settings.delete_account_dialog.1"));
        VaadinViewUtil.addDialogCloseButton(deleteAccountDialog);
        VerticalLayout layout = new VerticalLayout(deleteAccountText, new CustomHr(),
                new HorizontalLayout(deleteAccountNo, deleteAccountYes));
        layout.setAlignItems(Alignment.CENTER);
        layout.setAlignSelf(Alignment.CENTER);
        deleteAccountDialog.add(layout);
        deleteAccountNo.addClickListener(click -> deleteAccountDialog.close());
        deleteAccountYes.addClickListener(click -> {
            deleteAccountDialog.close();
            SecurityUtil.logout();
            eventPublisher.publishEvent(new UserDeleteEvent(user));
        });
        newPasswordButtons.add(saveNewPassword, cancelNewPassword);
        cancelNewPassword.addClickListener(c -> {
            password.setValue(getTranslation("account_settings.password"));
            password.setReadOnly(true);
            password.setRevealButtonVisible(false);
            password.setInvalid(false);
            newPassword.clear();
            changePassword.setEnabled(true);
            remove(newPassword, newPasswordButtons);
        });
        saveNewPassword.addClickListener(c -> {
            if (passwordBinder.validate().isOk()) {
                userService.changePassword(user.getId(), newPassword.getValue());
                ComponentUtil.fireEvent(UI.getCurrent(), new UserInfoRefreshEvent(this));
                cancelNewPassword.click();
            }
        });
        sendNewLink.addClickListener(c -> eventPublisher.publishEvent(
                new UserEmailConformationEvent(user, UI.getCurrent().getLocale())));
        changeEmail.addClickListener(c -> newEmail.setVisible(true));
        newEmail.setPlaceholder(getTranslation("inner_menu.new_email"));
        newEmail.setSuffixComponent(new HorizontalLayout(
                new Button(new Icon(VaadinIcon.CHECK), c -> {
                    if (newEmailBinder.validate().isOk()) {
                        newEmail.setVisible(false);
                        userService.changeEmail(user.getId(), newEmail.getValue());
                        user.setEmail(newEmail.getValue());
                        email.setValue(newEmail.getValue());
                        eventPublisher.publishEvent(new UserEmailConformationEvent(user,
                                UI.getCurrent().getLocale()));
                    }
                }),
                new Button(new Icon(VaadinIcon.CLOSE), c -> newEmail.setVisible(false))
        ));
        add(name, username, email, new HorizontalLayout(sendNewLink, changeEmail, newEmail),
                new CustomHr(), password, deleteAccount);
    }

    private void configureBinders() {
        nameBinder.forField(name).withValidator(name -> name == null || name.equals("") || name.matches(NAME_VALIDATION_PATTERN),
                nameValidationErrMessage).bind(UserData::getName, UserData::setName);

        usernameBinder.forField(username).withValidator(username -> username.matches(USERNAME_VALIDATION_PATTERN),
                usernameValidationErrMessage).withValidator(username ->
                        (!userService.checkIfExists(username) || username.equals(user.getUsername())),
                usernameAlreadyUsed).bind(UserData::getUsername, UserData::setUsername);

        emailBinder.forField(email).withValidator(email -> EmailValidator.getInstance().isValid(email),
                emailValidationErrMessage).withValidator(email ->
                        (!userService.checkIfExistsEmail(email) || email.equals(user.getEmail())),
                emailAlreadyUsed).bind(UserData::getEmail, UserData::setEmail);

        newEmailBinder.forField(newEmail).withValidator(email -> EmailValidator.getInstance().isValid(email),
                emailValidationErrMessage).withValidator(email ->
                        (!userService.checkIfExistsEmail(email) || email.equals(user.getEmail())),
                emailAlreadyUsed).bind(UserData::getEmail, UserData::setEmail);

        passwordBinder.forField(password).withValidator(password ->
                        userService.checkPassword(user.getId(), password), passwordValidationErrMessage)
                .bind(UserData::getPassword, UserData::setPassword);

        passwordBinder.forField(newPassword).withValidator(newPassword ->
                                nbvcxz.estimate(newPassword).getEntropy() >= RegisterUserForm.MINIMAL_PASSWORD_STRENGTH,
                        newPasswordValidationErrMessage).withValidator(newPassword ->
                        !newPassword.equals(this.password.getValue()), samePasswordValidationErrMessage)
                .bind(UserData::getPassword, UserData::setPassword);
    }

    private void addEditButton(TextField field, Binder<UserData> binder, Callable<Void> saveOperation) {
        Button change = new Button(new Icon(VaadinIcon.EDIT), e -> {
            field.setReadOnly(false);
            String current = field.getValue();
            Button save = new Button(new Icon(VaadinIcon.CHECK), s -> {
                if (binder.validate().isOk()) {
                    field.setReadOnly(true);
                    if (!current.equals(field.getValue())) {
                        try {
                            saveOperation.call();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    field.setSuffixComponent(e.getSource());
                }
            });
            field.setSuffixComponent(save);
        });
        field.setSuffixComponent(change);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (!currentUser.isVerified()) {
            sendNewLink.setVisible(true);
            changeEmail.setVisible(true);
            newEmail.setVisible(false);
            sendNewLink.setEnabled(true);
            changeEmail.setEnabled(true);
            newEmail.setEnabled(true);
            email.setEnabled(false);
        } else {
            sendNewLink.setVisible(false);
            changeEmail.setVisible(false);
            newEmail.setVisible(false);
            sendNewLink.setEnabled(false);
            changeEmail.setEnabled(false);
            newEmail.setEnabled(false);
            email.setEnabled(true);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        name.setLabel(getTranslation("account_settings.name"));
        username.setLabel(getTranslation("account_settings.username"));
        email.setLabel(getTranslation("account_settings.email"));
        password.setLabel(getTranslation("account_settings.password"));
        deleteAccount.setText(getTranslation("account_settings.delete_account"));
        deleteAccountDialog.setHeaderTitle(getTranslation("account_settings.delete_account_dialog.1"));
        deleteAccountText.setText(getTranslation("account_settings.delete_account_dialog.2"));
        deleteAccountYes.setText(getTranslation("account_settings.delete_account_yes"));
        deleteAccountNo.setText(getTranslation("account_settings.delete_account_no"));
        newPassword.setLabel(getTranslation("account_settings.new_password"));
        saveNewPassword.setText(getTranslation("register.save"));
        cancelNewPassword.setText(getTranslation("register.cancel"));
        nameValidationErrMessage = getTranslation("register.name_err_message");
        usernameValidationErrMessage = getTranslation("register.username_err_message");
        usernameAlreadyUsed = getTranslation("register.username_already_used");
        emailValidationErrMessage = getTranslation("register.email_err_message");
        emailAlreadyUsed = getTranslation("register.email_already_used");
        passwordValidationErrMessage = getTranslation("account_settings.password_err_message");
        newPasswordValidationErrMessage = getTranslation("register.password_err_message");
        samePasswordValidationErrMessage = getTranslation("account_settings.new_password_same");
        sendNewLink.setText(getTranslation("inner_menu.new_link_button"));
        changeEmail.setText(getTranslation("inner_menu.change_email_button"));
        newEmail.setPlaceholder(getTranslation("inner_menu.new_email"));
        configureBinders();
    }
}
