package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.web.quiz_bot.configuration.enums.Formats;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.vaadin.component.CustomHr;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedactQuizForm extends VerticalLayout {

    private final List<VerticalLayout> blocks = new ArrayList<>();
    private final List<Select<String>> columnSelectors = new ArrayList<>();
    private final List<Text> filters = Arrays.asList(
            new Text(getTranslation("request_form.custom_answer_any")),
            new Text(getTranslation("request_form.custom_answer_only_letters")),
            new Text(getTranslation("request_form.custom_answer_integer")),
            new Text(getTranslation("request_form.custom_answer_date")),
            new Text(getTranslation("request_form.custom_answer_name")),
            new Text(getTranslation("request_form.custom_answer_phone_number")),
            new Text(getTranslation("request_form.custom_answer_email_address"))
    );
    private final List<Formats> order = Arrays.asList(
            Formats.ANY,
            Formats.ONLY_LETTERS,
            Formats.INTEGER,
            Formats.DATE,
            Formats.NAME,
            Formats.PHONE_NUMBER,
            Formats.EMAIL_ADDRESS
    );
    private final List<String> tableColumns = new ArrayList<>();
    private final NormalTextField greeting = new NormalTextField();
    private final Checkbox noGreeting = new Checkbox();
    private final NormalTextField farewell = new NormalTextField();
    private final Checkbox noFarewell = new Checkbox();
    private final NormalTextField doneMessage = new NormalTextField();
    private final Checkbox multipleRegistration = new Checkbox();
    private boolean multipleRegistrationValue;

    public RedactQuizForm(JSONObject quizJson) {
        List<QuestionRecord> quiz = new ArrayList<>();
        HorizontalLayout registeredLayout = new HorizontalLayout();
        doneMessage.setPlaceholder(getTranslation("settings_form.done_message_placeholder"));
        doneMessage.setErrorMessage(getTranslation("settings_form.reminder"));
        doneMessage.addValueChangeListener(value -> doneMessage.setInvalid(false));
        if (quizJson.has(JSONKeys.DONE_MESSAGE.toString())) {
            doneMessage.setValue(quizJson.getString(JSONKeys.DONE_MESSAGE.toString()));
            quizJson.remove(JSONKeys.DONE_MESSAGE.toString());
        }
        multipleRegistration.setLabel(getTranslation("settings_form.multiple_registration"));
        multipleRegistration.addValueChangeListener(c -> {
            multipleRegistrationValue = c.getValue();
            doneMessage.setInvalid(false);
            doneMessage.setReadOnly(c.getValue());
        });
        multipleRegistrationValue = quizJson.getBoolean(JSONKeys.MULTIPLE_REGISTRATION.toString());
        multipleRegistration.setValue(multipleRegistrationValue);
        quizJson.remove(JSONKeys.MULTIPLE_REGISTRATION.toString());
        registeredLayout.add(doneMessage, multipleRegistration);
        registeredLayout.setWidth("72vw");
        HorizontalLayout greetingLayout = new HorizontalLayout();
        greeting.setPlaceholder(getTranslation("settings_form.greeting_placeholder"));
        greeting.setErrorMessage(getTranslation("settings_form.reminder"));
        greeting.addValueChangeListener(value -> greeting.setInvalid(false));
        noGreeting.setLabel(getTranslation("settings_form.no_greeting"));
        noGreeting.addValueChangeListener(value -> {
            greeting.setInvalid(false);
            greeting.setReadOnly(noGreeting.getValue());
        });
        greeting.setValue(quizJson.optString(JSONKeys.GREETING.toString()));
        if (greeting.isEmpty()) {
            noGreeting.setValue(true);
        } else {
            quizJson.remove(JSONKeys.GREETING.toString());
        }
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
        farewell.setValue(quizJson.optString(JSONKeys.FAREWELL.toString()));
        if (farewell.isEmpty()) {
            noFarewell.setValue(true);
        } else {
            quizJson.remove(JSONKeys.FAREWELL.toString());
        }
        farewellLayout.add(farewell, noFarewell);
        farewellLayout.setWidth("72vw");
        for (int i = 1; i < quizJson.keySet().size() + 1; i++) {
            JSONObject questionJson = quizJson.getJSONObject(String.valueOf(i));
            String question = questionJson.keys().next();
            JSONObject questionInfo = questionJson.getJSONObject(question);
            List<String> answers = new ArrayList<>();
            if (questionInfo.has(JSONKeys.ANSWERS.toString())) {
                answers = questionInfo.getJSONArray(JSONKeys.ANSWERS.toString()).toList()
                        .stream().map(a -> (String) a).toList();
            }
            List<String> customAnswer = new ArrayList<>();
            if (questionInfo.has(JSONKeys.CUSTOM_ANSWER.toString())) {
                customAnswer = questionInfo.getJSONArray(JSONKeys.CUSTOM_ANSWER.toString()).toList()
                        .stream().map(a -> (String) a).toList();
            }
            String formatMessage = null;
            if (questionInfo.has(JSONKeys.FORMAT_MESSAGE.toString())) {
                formatMessage = questionInfo.getJSONArray(JSONKeys.FORMAT_MESSAGE.toString()).getString(0);
            }
            String tableColumn = "";
            if (questionInfo.has(JSONKeys.TABLE_COLUMN.toString())) {
                tableColumn = questionInfo.getString(JSONKeys.TABLE_COLUMN.toString());
            }
            tableColumns.add(tableColumn);
            quiz.add(new QuestionRecord(question, answers, customAnswer, formatMessage, tableColumn));
        }
        add(registeredLayout, greetingLayout, farewellLayout, new CustomHr());
        for (QuestionRecord record : quiz) {
            addBlock(record.question, record.answers, record.customAnswer, record.formatMessage, record.tableColumn);
        }
    }

    private void addBlock(String question, List<String> answers,
                          List<String> customAnswer, String formatMessage, String tableColumn) {
        VerticalLayout block = new VerticalLayout();
        HorizontalLayout upperBlock = new HorizontalLayout();
        VerticalLayout middleBlock = new VerticalLayout();
        HorizontalLayout lowerBlock = new HorizontalLayout();

        NormalTextField questionField = new NormalTextField();
        questionField.setPlaceholder(getTranslation("request_form.question_placeholder"));
        questionField.setValueChangeMode(ValueChangeMode.EAGER);
        questionField.setValue(question);

        Select<String> columnSelection = new Select<>();
        columnSelection.setItems(tableColumns);
        columnSelection.setPlaceholder(getTranslation("request_form.table_column_placeholder"));
        columnSelection.setErrorMessage(getTranslation("request_form.table_column_duplicate"));
        columnSelection.addValueChangeListener(value -> checkColumnSelectors());
        columnSelectors.add(columnSelection);
        columnSelection.setValue(tableColumn);

        NormalButton removeBlockButton = new NormalButton(new Icon(VaadinIcon.MINUS));
        removeBlockButton.setText(getTranslation("request_form.remove_question_button"));
        upperBlock.add(questionField, columnSelection, removeBlockButton);

        NormalButton addAnswer = new NormalButton(new Icon(VaadinIcon.PLUS), e -> {
            HorizontalLayout answerLayout = new HorizontalLayout();
            SmallTextField answerField = new SmallTextField();
            answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
            answerField.setValueChangeMode(ValueChangeMode.EAGER);
            NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answerLayout));
            removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
            answerLayout.add(answerField, removeAnswer);
            middleBlock.addComponentAtIndex(middleBlock.getComponentCount() - 1, answerLayout);
        });
        addAnswer.setText(getTranslation("request_form.new_answer_button"));
        if (!answers.isEmpty()) {
            for (String answer : answers) {
                HorizontalLayout answerLayout = new HorizontalLayout();
                SmallTextField answerField = new SmallTextField();
                answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
                answerField.setValueChangeMode(ValueChangeMode.EAGER);
                answerField.setValue(answer);
                NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answerLayout));
                removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
                answerLayout.add(answerField, removeAnswer);
                middleBlock.add(answerLayout);
            }
            middleBlock.add(addAnswer);
        } else {
            middleBlock.add(addAnswer);
            addAnswer.click();
        }

        ComboBox<Text> customAnswerFormat = new ComboBox<>();
        customAnswerFormat.setPlaceholder(getTranslation("request_form.custom_answer_format_placeholder"));
        customAnswerFormat.setItems(filters);
        customAnswerFormat.setItemLabelGenerator(Text::getText);
        if (!customAnswer.isEmpty()) {
            customAnswerFormat.setValue(filters.get(decodeFormat(customAnswer.get(0))));
        } else {
            customAnswerFormat.setValue(filters.get(0));
        }
        Checkbox customAnswerSelection = new Checkbox(getTranslation("request_form.custom_answer"));
        customAnswerSelection.setValue(!customAnswer.isEmpty());
        customAnswerFormat.setReadOnly(!customAnswerSelection.getValue());
        TextField formatMessageField = new NormalTextField();
        if (formatMessage != null) {
            formatMessageField.setReadOnly(false);
            formatMessageField.setValue(formatMessage);
        } else {
            formatMessageField.setReadOnly(true);
        }
        formatMessageField.setPlaceholder(getTranslation("request_form.format_message_placeholder"));
        formatMessageField.addValueChangeListener(value -> formatMessageField.setInvalid(false));
        customAnswerSelection.addValueChangeListener(value -> {
            customAnswerFormat.setReadOnly(!value.getValue());
            if (value.getValue()) {
                questionField.setInvalid(false);
                formatMessageField.setReadOnly(customAnswerFormat.getValue().equals(filters.get(0)));
            } else {
                formatMessageField.setReadOnly(true);
            }
        });
        customAnswerFormat.addValueChangeListener(value ->
                formatMessageField.setReadOnly(customAnswerFormat.isReadOnly() || value.getValue().equals(filters.get(0))));
        lowerBlock.add(customAnswerSelection, customAnswerFormat, formatMessageField);
        customAnswerSelection.setWidth("15vw");
        formatMessageField.setWidth("36.5vw");

        block.add(upperBlock, middleBlock, lowerBlock);
        block.setAlignItems(Alignment.START);
        block.setSizeFull();
        blocks.add(block);

        removeBlockButton.addClickListener(e -> {
            remove(block);
            blocks.remove(block);
            columnSelectors.remove(columnSelection);
        });

        add(block);
    }

    public JSONObject edit() {
        if (!checkColumnSelectors()) {
            return null;
        }
        JSONObject json = new JSONObject();
        int questionOrder = 1;
        for (VerticalLayout block : blocks) {
            JSONObject content = new JSONObject();

            HorizontalLayout upperBlock = (HorizontalLayout) block.getComponentAt(0);
            TextField question = (TextField) upperBlock.getComponentAt(0);
            if (question.getValue().isEmpty()) {
                continue;
            }
            VerticalLayout middleBlock = (VerticalLayout) block.getComponentAt(1);
            middleBlock.getChildren().limit(middleBlock.getComponentCount() - 1)
                    .map(e -> (HorizontalLayout) e).forEach(e -> {
                        TextField answer = (TextField) e.getComponentAt(0);
                        if (!answer.isEmpty()) {
                            content.append(JSONKeys.ANSWERS.toString(), answer.getValue());
                        }
                    });

            HorizontalLayout lowerBlock = (HorizontalLayout) block.getComponentAt(2);
            Checkbox customAnswer = (Checkbox) lowerBlock.getComponentAt(0);
            if (customAnswer.getValue()) {
                ComboBox<Text> customAnswerFormat = (ComboBox<Text>) lowerBlock.getComponentAt(1);
                int index = filters.indexOf(customAnswerFormat.getValue());
                content.append(JSONKeys.CUSTOM_ANSWER.toString(), encodeFormat(index));
                TextField formatMessage = (TextField) lowerBlock.getComponentAt(2);
                if (!formatMessage.isReadOnly()) {
                    if (formatMessage.isEmpty()) {
                        formatMessage.setErrorMessage(getTranslation("settings_form.reminder"));
                        formatMessage.setInvalid(true);
                        formatMessage.focus();
                        return null;
                    }
                    content.append(JSONKeys.FORMAT_MESSAGE.toString(), formatMessage.getValue());
                }
            }
            if (content.isEmpty()) {
                question.setErrorMessage(getTranslation("request_form.question_err_message"));
                question.setInvalid(true);
                question.focus();
                return null;
            }
            Select<String> tableColumn = (Select<String>) upperBlock.getComponentAt(1);
            String columnValue = tableColumn.getValue();
            if (columnValue == null || columnValue.isEmpty() || columnValue.isBlank()) {
                ;
            } else {
                content.put(JSONKeys.TABLE_COLUMN.toString(), columnValue);
            }
            JSONObject wrapper = new JSONObject();
            wrapper.put(question.getValue(), content);
            json.put(String.valueOf(questionOrder++), wrapper);
        }
        String doneMessage = readAuxField(this.doneMessage);
        String greeting = readAuxField(this.greeting);
        String farewell = readAuxField(this.farewell);
        if (json.isEmpty() || doneMessage == null || greeting == null || farewell == null) {
            return null;
        }
        if (!doneMessage.isEmpty()) {
            json.put(JSONKeys.DONE_MESSAGE.toString(), doneMessage);
        }
        if (!greeting.isEmpty()) {
            json.put(JSONKeys.GREETING.toString(), greeting);
        }
        if (!farewell.isEmpty()) {
            json.put(JSONKeys.FAREWELL.toString(), farewell);
        }
        json.put(JSONKeys.MULTIPLE_REGISTRATION.toString(), multipleRegistrationValue);
        return json;
    }

    private boolean checkColumnSelectors() {
        boolean allValid = true;
        for (Select<String> selector1 : columnSelectors) {
            selector1.setInvalid(false);
            for (Select<String> selector2 : columnSelectors) {
                if (selector1.getValue() != null && selector2.getValue() != null
                        && !selector1.equals(selector2)
                        && selector1.getValue().equals(selector2.getValue())
                        && !selector1.getValue().isEmpty()) {
                    selector1.setInvalid(true);
                    allValid = false;
                    break;
                }
            }
        }
        return allValid;
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

    private int decodeFormat(String format) {
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i).name().toLowerCase().equals(format)) {
                return i;
            }
        }
        return 0;
    }

    private String encodeFormat(int index) {
        return switch (order.get(index)) {
            case ANY -> Formats.ANY.name().toLowerCase();
            case ONLY_LETTERS -> Formats.ONLY_LETTERS.name().toLowerCase();
            case INTEGER -> Formats.INTEGER.name().toLowerCase();
            case DATE -> Formats.DATE.name().toLowerCase();
            case NAME -> Formats.NAME.name().toLowerCase();
            case PHONE_NUMBER -> Formats.PHONE_NUMBER.name().toLowerCase();
            case EMAIL_ADDRESS -> Formats.EMAIL_ADDRESS.name().toLowerCase();
        };
    }

    private record QuestionRecord(String question, List<String> answers,
                                  List<String> customAnswer, String formatMessage, String tableColumn) {}
}
