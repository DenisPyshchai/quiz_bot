package com.web.quiz_bot.vaadin.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.FetchKeywords;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.request.Request;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.RequestUtil;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.SmallButton;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import com.web.quiz_bot.vaadin.view.form.RedactQuizForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Route(value = "quiz-manager", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
public class QuizManagerView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final Notification error = new Notification();
    private final Notification success = new Notification();
    private final Notification tableEmpty = new Notification();
    private final UserService userService;
    private final QuizServerService quizServerService;

    @Autowired
    public QuizManagerView(UserService userService, QuizServerService quizServerService) {
        this.userService = userService;
        this.quizServerService = quizServerService;
        addClassName("quiz-manager-view");
        error.setPosition(Notification.Position.MIDDLE);
        error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        success.setPosition(Notification.Position.MIDDLE);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        tableEmpty.setPosition(Notification.Position.MIDDLE);
        tableEmpty.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        refresh();
    }

    private void refresh() {
        try {
            this.removeAll();
            add(error, success, tableEmpty);
        } catch (IllegalArgumentException ignore) {
            UI.getCurrent().getPage().reload();
        }
        JSONObject quizzes = VaadinViewUtil.fetchQuizzes(userService, quizServerService, error, getLocale());
        NormalButton newQuiz = new NormalButton(new Icon(VaadinIcon.CLIPBOARD_TEXT),
                e -> UI.getCurrent().navigate(NewQuizView.class));
        NormalButton copyQuiz = new NormalButton(new Icon(VaadinIcon.COPY));
        Button refreshQuizzes = new IconButton(new Icon(VaadinIcon.REFRESH), c -> refresh());
        newQuiz.setText(getTranslation("quiz_manager.new_quiz"));
        copyQuiz.setText(getTranslation("quiz_manager.copy_quiz"));
        copyQuiz.setEnabled(false);
        HorizontalLayout buttons = new HorizontalLayout(newQuiz, copyQuiz, refreshQuizzes);
        if (quizzes == null) {
            add(buttons);
            return;
        }
        for (Object table : quizzes.getJSONArray(JSONKeys.TABLES.toString())) {
            JSONObject data = (JSONObject) table;
            String tableName = data.getString(JSONKeys.TABLE_NAME.toString());
            H4 tableNameH4 = new H4(tableName);
            Details tableInfo = new Details(getTranslation("quiz_manager.table_info"));
            tableInfo.addContent(new HorizontalLayout(new Text(String.format(getTranslation("quiz_manager.all_entries"),
                    data.getInt(JSONKeys.ALL_ENTRIES.toString())))));
            tableInfo.addContent(new HorizontalLayout(new Text(String.format(getTranslation("quiz_manager.new_entries"),
                    data.getInt(JSONKeys.NEW_ENTRIES.toString())))));
            Details telegramBots = new Details(getTranslation("quiz_manager.telegram_bots"));
            for(Object botUsername : data.getJSONArray(JSONKeys.BOTS.toString())) {
                Button resume = new IconButton(new Icon(VaadinIcon.PLAY));
                resume.setEnabled(false);
                Button pause = new IconButton(new Icon(VaadinIcon.PAUSE));
                pause.setEnabled(false);
                telegramBots.addContent(new HorizontalLayout(
                        new Text(botUsername.toString()),
                        resume, pause
                ));
            }
            Button view = new IconButton(new Icon(VaadinIcon.EYE), e -> {
                Locale userLocale = UI.getCurrent().getLocale();
                Map<String, Object> resultsJSON = fetchResults(tableName, userLocale);
                assert resultsJSON != null;
                if (resultsJSON.isEmpty()) {
                    VaadinViewUtil.openNotification(tableEmpty, getTranslation("quiz_manager.table_empty"));
                    return;
                }
                List<Map<String, Object>> results = new ArrayList<>();
                Iterator<String> columns = resultsJSON.keySet().iterator();
                String currentColumn = columns.next();
                Map<String, Object> rows = (Map<String, Object>) resultsJSON.get(currentColumn);
                Grid<Map<String, Object>> grid = new Grid<>();
                for (int i = 0; i < rows.keySet().size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(currentColumn, rows.get(String.format("%s", i)));
                    results.add(map);
                }
                String finalCurrentColumn1 = currentColumn;
                grid.addColumn(item -> item.getOrDefault(finalCurrentColumn1, ""))
                        .setHeader(finalCurrentColumn1);
                while (columns.hasNext()) {
                    currentColumn = columns.next();
                    rows = (Map<String, Object>) resultsJSON.get(currentColumn);
                    for (int i = 0; i < rows.keySet().size(); i++) {
                        Map<String, Object> map = results.get(i);
                        map.put(currentColumn, rows.get(String.format("%s", i)));
                    }
                    String finalCurrentColumn2 = currentColumn;
                    grid.addColumn(item -> item.getOrDefault(finalCurrentColumn2, ""))
                            .setHeader(finalCurrentColumn2);
                }
                grid.setItems(results);
                grid.setWidthFull();
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle(
                        String.format(getTranslation("quiz_manager.table_view"), tableName));
                VaadinViewUtil.addDialogCloseButton(dialog);
                dialog.add(grid);
                dialog.setWidthFull();
                dialog.open();
            });
            Button download = new IconButton(new Icon(VaadinIcon.DOWNLOAD), e -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle(getTranslation("quiz_manager.chose_format"));
                VaadinViewUtil.addDialogCloseButton(dialog);
                Select<String> fileFormat = new Select<>();
                fileFormat.setItems(
                        capitalize(FetchKeywords.EXCEL.toString()),
                        capitalize(FetchKeywords.CSV.toString())
                );
                fileFormat.setValue(capitalize(FetchKeywords.EXCEL.toString()));
                Button confirm = new SmallButton(getTranslation("quiz_manager.confirm"), d -> {
                    Locale userLocale = UI.getCurrent().getLocale();
                    if (fileFormat.getValue().toLowerCase()
                            .equals(FetchKeywords.EXCEL.toString())) {
                        final StreamResource resource = new StreamResource(
                                tableName + ".xlsx", () -> fetchExcel(tableName, userLocale));
                        final StreamRegistration registration = VaadinSession.getCurrent()
                                .getResourceRegistry().registerResource(resource);
                        UI.getCurrent().getPage().open(registration.getResourceUri().toString());
                    } else if (fileFormat.getValue().toLowerCase()
                            .equals(FetchKeywords.CSV.toString())) {
                        final StreamResource resource = new StreamResource(
                                tableName + ".csv", () -> fetchCsv(tableName, userLocale));
                        final StreamRegistration registration = VaadinSession.getCurrent()
                                .getResourceRegistry().registerResource(resource);
                        UI.getCurrent().getPage().open(registration.getResourceUri().toString());
                    }
                    dialog.close();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new SmallButton(getTranslation("quiz_manager.cancel"),
                        d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                VerticalLayout layout = new VerticalLayout(fileFormat,
                        new HorizontalLayout(cancel, confirm));
                layout.setAlignItems(Alignment.CENTER);
                layout.setAlignSelf(Alignment.CENTER);
                dialog.add(layout);
                dialog.open();
            });
            Button edit = new IconButton(new Icon(VaadinIcon.EDIT), e -> {
                Dialog dialog = new Dialog();
                VaadinViewUtil.addDialogCloseButton(dialog);
                JSONObject quiz = fetchQuiz(tableName, UI.getCurrent().getLocale());
                if (quiz == null) {
                    return;
                }
                RedactQuizForm redactQuizForm = new RedactQuizForm(quiz);
                Button confirm = new SmallButton(getTranslation("quiz_manager.confirm"), d -> {
                    JSONObject edited = redactQuizForm.edit();
                    if (edited == null) {
                        return;
                    }
                    dialog.close();
                    User currentUser = VaadinViewUtil.getCurrentUser(userService);
                    Request request = new Request(currentUser.getId(), RequestKeywords.UPDATE);
                    JSONObject updateJson = new JSONObject();
                    updateJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
                    updateJson.put(JSONKeys.QUIZ_JSON.toString(), edited);
                    request.setUpdateJson(updateJson);
                    try {
                        byte[] response = RequestUtil.sendRequest(request,
                                quizServerService.getRandomServerURL().toURI());
                        if (response != null) {
                            VaadinViewUtil.openNotification(success, "Success");
                        }
                    } catch (URISyntaxException ex) {
                        System.err.println("Cannot convert URL to URI");
                        VaadinViewUtil.openNotification(error,
                                getTranslation("util.something_went_wrong"));
                    } catch (NullPointerException er) {
                        System.err.println("No servers available");
                        VaadinViewUtil.openNotification(error,
                                getTranslation("util.no_servers_available"));
                    } catch (Exception ec) {
                        System.err.println("Unexpected exception: " + ec.getMessage());
                        VaadinViewUtil.openNotification(error,
                                getTranslation("util.something_went_wrong"));
                    }
                    refresh();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new SmallButton(getTranslation("quiz_manager.cancel"),
                        d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                HorizontalLayout dialogButtons = new HorizontalLayout(cancel, confirm);
                dialogButtons.setAlignItems(Alignment.CENTER);
                dialogButtons.setAlignSelf(Alignment.CENTER);
                dialog.add(new VerticalLayout(redactQuizForm, dialogButtons));
                dialog.open();

            });
            Button delete = new IconButton(new Icon(VaadinIcon.CLOSE), e -> {
                Dialog dialog = new Dialog();
                Button confirm = new SmallButton(getTranslation("quiz_manager.confirm"), d -> {
                    deleteQuiz(data.getString(JSONKeys.TABLE_NAME.toString()));
                    dialog.close();
                    refresh();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new SmallButton(getTranslation("quiz_manager.cancel"),
                        d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                HorizontalLayout dialogButtons = new HorizontalLayout(cancel, confirm);
                VerticalLayout layout = new VerticalLayout(new Text(
                        getTranslation("quiz_manager.delete_conformation")), dialogButtons);
                layout.setAlignItems(Alignment.CENTER);
                layout.setAlignSelf(Alignment.CENTER);
                dialog.add(layout);
                dialog.open();
            });
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            HorizontalLayout quizButtons = new HorizontalLayout(view, download, edit, delete);
            quizButtons.getStyle().set("padding-top", "1.5vh");
            HorizontalLayout layout = new HorizontalLayout(tableNameH4, tableInfo,
                    telegramBots, quizButtons);
            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.setWidth("60%");
            add(layout, new Hr());
        }
        add(buttons);
    }

    private InputStream fetchExcel(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.EXCEL);
        if (response != null) {
            return new ByteArrayInputStream(response);
        }
        return null;
    }

    private InputStream fetchCsv(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.CSV);
        if (response != null) {
            return new ByteArrayInputStream(response);
        }
        return null;
    }

    private JSONObject fetchQuiz(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.QUIZ);
        if (response != null) {
            return new JSONObject(new String(response, StandardCharsets.UTF_8));
        }
        return null;
    }

    private Map<String, Object> fetchResults(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.RESULTS);
        if (response != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new String(response, StandardCharsets.UTF_8), LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                return new JSONObject(new String(response, StandardCharsets.UTF_8)).toMap();
            }
        }
        return null;
    }

    private byte[] fetch(String tableName, Locale locale, FetchKeywords fetchWhat) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            VaadinViewUtil.openNotification(error, getTranslation("util.not_authenticated"));
        } else {
            JSONObject fetchJson = new JSONObject();
            fetchJson.put(JSONKeys.FETCH_WHAT.toString(), fetchWhat.toString());
            fetchJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
            return VaadinViewUtil.fetch(currentUser, fetchJson, quizServerService, error, locale);
        }
        return null;
    }

    private void deleteQuiz(String tableName) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            VaadinViewUtil.openNotification(error, getTranslation("util.not_authenticated"));
        } else {
            try {
                JSONObject deleteJson = new JSONObject();
                deleteJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
                Request request = new Request(currentUser.getId(), RequestKeywords.DELETE);
                request.setDeleteJson(deleteJson);
                byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
                if (response == null) {
                    VaadinViewUtil.openNotification(error, getTranslation("util.server_not_responding"));
                } else {
                    VaadinViewUtil.openNotification(success, getTranslation("quiz_manager.quiz_deleted"));
                }
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
                VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
            } catch (NullPointerException er) {
                VaadinViewUtil.openNotification(error, getTranslation("util.no_servers_available"));
            } catch (Exception ex) {
                System.err.println("Unexpected exception: " + ex.getMessage());
                VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
            }
        }
    }

    private static String capitalize(String str)
    {
        if(str == null || str.length()<=1) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.setEnabled(VaadinViewUtil.getCurrentUser(userService).isVerified());
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        refresh();
    }

    private static class IconButton extends Button {

        public IconButton(Component icon) {
            super(icon);
            init();
        }

        public IconButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
            super(icon, clickListener);
            init();
        }

        private void init() {
            getStyle().set("background", "hsla(214, 53%, 23%, 0.16)");
            getStyle().set("padding", "0.5vw");
        }
    }
}
