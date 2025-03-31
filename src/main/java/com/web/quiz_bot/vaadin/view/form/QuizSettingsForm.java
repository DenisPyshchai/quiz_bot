package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.CustomHr;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class QuizSettingsForm extends VerticalLayout implements LocaleChangeObserver {

    private final Label quizNameLabel = new Label(getTranslation("settings_form.quiz_name"));
    private final TextField quizName = new NormalTextField();
    private final Set<String> existingQuizNames = new HashSet<String>();
    private final Checkbox multipleRegistration = new Checkbox();
    private final TextField doneMessage = new NormalTextField();
    private final Label tableColumnsLabel = new Label(getTranslation("settings_form.label"));
    private final NormalButton newColumn = new NormalButton(new Icon(VaadinIcon.PLUS));
    private final List<TextField> columnNames = new ArrayList<>();
    private final List<Button> removeColumnButtons = new ArrayList<>();
    private final VerticalLayout quizSettings = new VerticalLayout();
    private final VerticalLayout tableSettings = new VerticalLayout();
    private final NormalTextField greeting = new NormalTextField();
    private final Checkbox noGreeting = new Checkbox();
    private final NormalTextField farewell = new NormalTextField();
    private final Checkbox noFarewell = new Checkbox();
    public final DataProvider<TextField, Void> tableColumnDataProvider;
    public boolean multipleRegistrationValue = false;

    public QuizSettingsForm(UserService userService, QuizServerService quizServerService) {
        tableColumnDataProvider = DataProvider.fromCallbacks(
                query -> columnNames.stream(),
                query -> columnNames.size()
        );
        columnNames.add(new TextField());
        configureQuizSettings();
        configureTableSettings();
        HorizontalLayout greetingLayout = new HorizontalLayout();
        greeting.setPlaceholder(getTranslation("settings_form.greeting_placeholder"));
        greeting.setErrorMessage(getTranslation("settings_form.reminder"));
        greeting.addValueChangeListener(value -> greeting.setInvalid(false));
        noGreeting.setLabel(getTranslation("settings_form.no_greeting"));
        noGreeting.addValueChangeListener(value -> {
            greeting.setInvalid(false);
            greeting.setReadOnly(noGreeting.getValue());
        });
        greetingLayout.add(greeting, noGreeting);
        greetingLayout.setWidth("72vw");
        HorizontalLayout farewellLayout = new HorizontalLayout();
        farewell.setPlaceholder(getTranslation("settings_form.farewell_placeholder"));
        farewell.setErrorMessage(getTranslation("settings_form.reminder"));
        farewell.addValueChangeListener(value -> farewell.setInvalid(false));
        noFarewell.setLabel(getTranslation("settings_form.no_farewell"));
        noFarewell.addValueChangeListener(value -> {
            farewell.setInvalid(false);
            farewell.setReadOnly(noFarewell.getValue());
        });
        farewellLayout.add(farewell, noFarewell);
        farewellLayout.setWidth("72vw");
        HorizontalLayout registeredLayout = new HorizontalLayout();
        registeredLayout.setWidth("72vw");
        doneMessage.setPlaceholder(getTranslation("settings_form.done_message_placeholder"));
        doneMessage.setErrorMessage(getTranslation("settings_form.reminder"));
        doneMessage.addValueChangeListener(value -> doneMessage.setInvalid(false));
        multipleRegistration.setLabel(getTranslation("settings_form.multiple_registration"));
        multipleRegistration.addValueChangeListener(c -> {
            multipleRegistrationValue = c.getValue();
            doneMessage.setInvalid(false);
            doneMessage.setReadOnly(c.getValue());
        });
        registeredLayout.add(doneMessage, multipleRegistration);
        HorizontalLayout layout = new HorizontalLayout(quizSettings, tableSettings);
        layout.setWidth("74vw");
        layout.setVerticalComponentAlignment(Alignment.START);
        Notification error = new Notification();
        error.setPosition(Notification.Position.MIDDLE);
        error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        add(layout, new CustomHr(), registeredLayout, greetingLayout, farewellLayout, error);
        setAlignItems(Alignment.CENTER);
        JSONObject quizzes = VaadinViewUtil.fetchQuizzes(userService, quizServerService, error, getLocale());
        if (quizzes != null) {
            for (Object table : quizzes.getJSONArray(JSONKeys.TABLES.toString())) {
                JSONObject data = (JSONObject) table;
                String tableName = data.getString(JSONKeys.TABLE_NAME.toString());
                existingQuizNames.add(tableName);
            }
        }
    }

    private void configureQuizSettings() {
        quizName.setPlaceholder(getTranslation("settings_form.quiz_name_placeholder"));
        quizName.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        quizName.setWidth("30vw");
        quizName.addValueChangeListener(change -> quizName.setInvalid(existingQuizNames.contains(change.getValue())));
        quizName.setErrorMessage(getTranslation("settings_form.quiz_name_error"));
        quizSettings.add(quizNameLabel, quizName, new Div(new Text("")));
    }

    private void configureTableSettings() {
        newColumn.addClickListener(e -> {
            HorizontalLayout column = new HorizontalLayout();
            SmallTextField columnName = new SmallTextField();
            columnName.setValueChangeMode(ValueChangeMode.EAGER);
            columnName.addValueChangeListener(c -> {
                columnName.setInvalid(false);
                for (TextField name : columnNames) {
                    if ((!c.getSource().isEmpty() && !name.equals(c.getSource()))
                            && ((name.isEmpty() && c.getValue().equals(name.getPlaceholder()))
                            || c.getValue().equals(name.getValue()))) {
                        columnName.setErrorMessage(getTranslation("settings_form.column_name_already_exists"));
                        columnName.setInvalid(true);
                        break;
                    }
                }
                tableColumnDataProvider.refreshItem(columnName);
            });
            columnName.setPlaceholder(String.format("%s %s",
                    getTranslation("settings_form.column_placeholder"),
                    tableSettings.getComponentCount() - 1));
            columnNames.add(columnName);
            tableColumnDataProvider.refreshAll();
            NormalButton removeColumn = new NormalButton(new Icon(VaadinIcon.MINUS),
                    c -> {
                        tableSettings.remove(column);
                        columnNames.remove(columnName);
                        for (int i = 1; i < columnNames.size(); i++) {
                            columnNames.get(i).setPlaceholder(String.format("%s %s",
                                    getTranslation("settings_form.column_placeholder"),
                                    i));
                        }
                        tableColumnDataProvider.refreshAll();
                    });
            removeColumn.setText(getTranslation("settings_form.remove_column_button"));
            removeColumnButtons.add(removeColumn);
            column.add(columnName, removeColumn);
            tableSettings.addComponentAtIndex(tableSettings.getComponentCount() - 1, column);
        });
        newColumn.setText(getTranslation("settings_form.new_column_button"));
        tableSettings.add(tableColumnsLabel, newColumn);
        newColumn.click();
    }

    public JSONObject save() {
        JSONObject json = new JSONObject();
        TextField quizName = (TextField) quizSettings.getComponentAt(1);
        if (quizName.isEmpty()) {
            json.put(JSONKeys.TABLE_NAME.toString(), quizName.getPlaceholder());
        } else {
            json.put(JSONKeys.TABLE_NAME.toString(), quizName.getValue());
        }
        List<String> columnOrder = new ArrayList<>();
        tableSettings.getChildren().limit(tableSettings.getComponentCount() - 1)
                .skip(1).map(t -> (HorizontalLayout) t).forEachOrdered(t -> {
                    TextField columnName = (TextField) t.getComponentAt(0);
                    if (columnName.isEmpty()) {
                        json.put(columnName.getPlaceholder(), "Auto");
                        columnOrder.add(columnName.getPlaceholder());
                    } else {
                        json.put(columnName.getValue(), "Auto");
                        columnOrder.add(columnName.getValue());
                    }
                });
        json.put(JSONKeys.COLUMN_ORDER.toString(), new JSONArray(columnOrder));
        return json;
    }

    public boolean checkQuizName() {
        return quizName.isInvalid();
    }

    public String readDoneMessage() {
        return readAuxField(doneMessage);
    }

    public String readGreeting() {
        return readAuxField(greeting);
    }

    public String readFarewell() {
        return readAuxField(farewell);
    }

    private String readAuxField(TextField field) {
        if (!field.isReadOnly()) {
            if (field.isEmpty()) {
                field.setInvalid(true);
                field.focus();
                return null;
            }
            return field.getValue();
        }
        return "";
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        quizNameLabel.setText(getTranslation("settings_form.quiz_name"));
        quizName.setPlaceholder(getTranslation("settings_form.quiz_name_placeholder"));
        quizName.setErrorMessage(getTranslation("settings_form.quiz_name_error"));
        tableColumnsLabel.setText(getTranslation("settings_form.label"));
        doneMessage.setPlaceholder(getTranslation("settings_form.done_message_placeholder"));
        doneMessage.setErrorMessage(getTranslation("settings_form.reminder"));
        multipleRegistration.setLabel(getTranslation("settings_form.multiple_registration"));
        greeting.setPlaceholder(getTranslation("settings_form.greeting_placeholder"));
        greeting.setErrorMessage(getTranslation("settings_form.reminder"));
        noGreeting.setLabel(getTranslation("settings_form.no_greeting"));
        farewell.setPlaceholder(getTranslation("settings_form.farewell_placeholder"));
        farewell.setErrorMessage(getTranslation("settings_form.reminder"));
        noFarewell.setLabel(getTranslation("settings_form.no_farewell"));
        for (int i = 1; i < columnNames.size(); i++) {
            columnNames.get(i).setPlaceholder(String.format("%s %s",
                    getTranslation("settings_form.column_placeholder"),
                    i));
            tableColumnDataProvider.refreshItem(columnNames.get(i));
        }
        for (Button removeColumnButton : removeColumnButtons) {
            removeColumnButton.setText(getTranslation("settings_form.remove_column_button"));
        }
        newColumn.setText(getTranslation("settings_form.new_column_button"));
    }
}
