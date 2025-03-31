package com.web.quiz_bot.util;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.web.quiz_bot.configuration.enums.FetchKeywords;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.localization.LocalizationProvider;
import com.web.quiz_bot.request.Request;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.vaadin.view.LoginView;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class VaadinViewUtil {

    public static final LocalizationProvider localization = new LocalizationProvider();

    private VaadinViewUtil() {}

    public static User getCurrentUser(UserService userService) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        if (username.contains("@")) {
            return userService.getFromEmail(username);
        } else {
            return userService.getFromUsername(username);
        }
    }

    public static JSONObject fetchQuizzes(UserService userService,
                                          QuizServerService quizServerService,
                                          Notification error, Locale locale) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            VaadinViewUtil.openNotification(error,
                    localization.getTranslation("util.not_authenticated", locale, (Object) null));
        } else {
            JSONObject fetchJson = new JSONObject();
            fetchJson.put(JSONKeys.FETCH_WHAT.toString(), FetchKeywords.INFO.toString());
            byte[] response = VaadinViewUtil.fetch(currentUser, fetchJson, quizServerService, error, locale);
            if (response != null) {
                return new JSONObject(new String(response, StandardCharsets.UTF_8));
            }
        }
        return null;
    }

    public static byte[] fetch(User currentUser, JSONObject fetchJson,
                               QuizServerService quizServerService,
                               Notification error, Locale locale) {
        try {
            Request request = new Request(currentUser.getId(), RequestKeywords.FETCH);
            request.setFetchJson(fetchJson);
            byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
            if (response == null) {
                openNotification(error,
                        localization.getTranslation("util.server_not_responding", locale, (Object) null));
            }
            return response;
        } catch (URISyntaxException e) {
            System.err.println("Cannot convert URL to URI");
            openNotification(error,
                    localization.getTranslation("util.something_went_wrong", locale, (Object) null));
            return null;
        } catch (NullPointerException er) {
            openNotification(error,
                    localization.getTranslation("util.no_servers_available", locale, (Object) null));
            return null;
        } catch (Exception ex) {
            System.err.println("Unexpected exception: " + ex.getMessage());
            openNotification(error,
                    localization.getTranslation("util.something_went_wrong", locale, (Object) null));
            return null;
        }
    }

    public static void openNotification(Notification notification, String text) {
        notification.removeAll();
        notification.setDuration(4000);
        Button closeNotification = new Button(new Icon(VaadinIcon.CLOSE));
        closeNotification.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeNotification.getElement().setAttribute("aria-label", "Close");
        closeNotification.addClickListener(event -> {
            if (notification.isOpened()) {
                notification.close();
            }
        });
        HorizontalLayout layout = new HorizontalLayout(new Div(new Text(text)), closeNotification);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);
        notification.open();
    }

    public static void setH1NamedLogo(H1 namedLogo, UserService userService, String messageToFormat) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }
        String name = currentUser.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            name = currentUser.getUsername();
        }
        namedLogo.setText(String.format(messageToFormat, name));
    }

    public static void addDialogCloseButton(Dialog dialog) {
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), click -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        closeButton.getElement().getStyle().set("margin-left", "auto");
        HorizontalLayout header = new HorizontalLayout(closeButton);
        header.setWidthFull();
        dialog.getHeader().add(header);
    }
}
