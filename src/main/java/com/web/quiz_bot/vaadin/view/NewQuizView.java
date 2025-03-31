package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.request.Request;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.RequestUtil;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.CustomHr;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import com.web.quiz_bot.vaadin.view.form.QuizSettingsForm;
import com.web.quiz_bot.vaadin.view.form.QuizForm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Route(value = "new-quiz", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
@Component
public class NewQuizView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private Notification error;
    private Notification success;
    private QuizSettingsForm quizSettingsForm;
    private QuizForm quizForm;
    private VerticalLayout quizSettingsPage;
    private VerticalLayout quizPage;
    private NormalButton next;
    private NormalButton cancel;
    private NormalButton save;
    private NormalButton back;
    private final UserService userService;
    private final QuizServerService quizServerService;

    @Autowired
    public NewQuizView(UserService userService, QuizServerService quizServerService) {
        this.userService = userService;
        this.quizServerService = quizServerService;
        setNotifications();
        resetPages(false, null);
        add(quizSettingsPage, error, success);
    }

    private void setNotifications() {
        error = new Notification();
        success = new Notification();
        error.setPosition(Notification.Position.MIDDLE);
        error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        success.setPosition(Notification.Position.MIDDLE);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void setQuizSettingsPage() {
        quizSettingsPage = new VerticalLayout();
        quizSettingsPage.setSizeFull();
        quizSettingsPage.setAlignItems(Alignment.CENTER);
        quizSettingsPage.setHorizontalComponentAlignment(Alignment.CENTER);
        next = new NormalButton(getTranslation("new_quiz.next"), new Icon(VaadinIcon.ARROW_RIGHT));
        next.addClickListener(e -> {
            String doneMessage = quizSettingsForm.readDoneMessage();
            String greeting = quizSettingsForm.readGreeting();
            String farewell = quizSettingsForm.readFarewell();
            if (doneMessage == null || greeting == null || farewell == null || quizSettingsForm.checkQuizName()) {
                return;
            }
            remove(quizSettingsPage);
            add(quizPage);
        });
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel = new NormalButton(getTranslation("register.cancel"), new Icon(VaadinIcon.CLOSE));
        cancel.addClickListener(e -> resetPages(false, null));
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttons = new HorizontalLayout(cancel, next);
        quizSettingsPage.add(quizSettingsForm, new CustomHr(), buttons);
    }

    private void setQuizPage() {
        quizPage = new VerticalLayout();
        quizPage.setSizeFull();
        quizPage.setAlignItems(Alignment.CENTER);
        quizPage.setHorizontalComponentAlignment(Alignment.CENTER);
        save = new NormalButton(getTranslation("register.save"), new Icon(VaadinIcon.CHECK));
        save.addClickListener(e -> {
            User currentUser = VaadinViewUtil.getCurrentUser(userService);
            if (currentUser == null) {
                VaadinViewUtil.openNotification(error, getTranslation("util.not_authenticated"));
            } else {
                Request request = new Request(currentUser.getId(), RequestKeywords.CREATE);
                JSONObject quizJson = quizForm.save();
                if (quizJson == null) {
                    return;
                } else if (quizJson.isEmpty()) {
                    VaadinViewUtil.openNotification(error, getTranslation("new_quiz.empty_quiz"));
                    return;
                }
                request.setQuizJson(quizJson);
                JSONObject tableJson = quizSettingsForm.save();
                if (tableJson == null || tableJson.isEmpty()) {
                    return;
                }
                request.setTableJson(tableJson);
                try {
                    byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
                    if (response != null) {
                        resetPages(true, new String(response, StandardCharsets.UTF_8));
                    }
                } catch (URISyntaxException ex) {
                    System.err.println("Cannot convert URL to URI");
                    VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
                } catch (NullPointerException er) {
                    System.err.println("No servers available");
                    VaadinViewUtil.openNotification(error, getTranslation("util.no_servers_available"));
                } catch (Exception ec) {
                    System.err.println("Unexpected exception: " + ec.getMessage());
                    VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
                }
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        back = new NormalButton(getTranslation("new_quiz.back"), new Icon(VaadinIcon.ARROW_LEFT));
        back.addClickListener(e -> {
            remove(quizPage);
            add(quizSettingsPage);
        });
        back.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttons = new HorizontalLayout(back, save);
        quizPage.add(quizForm, new CustomHr(), buttons);
    }

    private void resetPages(boolean save, String response) {
        this.removeAll();
        quizSettingsForm = new QuizSettingsForm(userService, quizServerService);
        quizForm = new QuizForm(quizSettingsForm);
        setQuizSettingsPage();
        setQuizPage();
        add(quizSettingsPage, error, success);
        if (save) {
            VaadinViewUtil.openNotification(success, response);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.setEnabled(VaadinViewUtil.getCurrentUser(userService).isVerified());
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        save.setText(getTranslation("register.save"));
        cancel.setText(getTranslation("register.cancel"));
        next.setText(getTranslation("new_quiz.next"));
        back.setText(getTranslation("new_quiz.back"));
        quizSettingsForm.localeChange(localeChangeEvent);
        quizForm.localeChange(localeChangeEvent);
    }
}
