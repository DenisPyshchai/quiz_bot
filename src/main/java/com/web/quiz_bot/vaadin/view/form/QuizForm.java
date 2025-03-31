package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.Formats;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class QuizForm extends VerticalLayout implements LocaleChangeObserver {

    private final NormalButton newQuestion = new NormalButton(new Icon(VaadinIcon.PLUS), e -> addBlock());
    private final List<VerticalLayout> blocks = new ArrayList<>();
    private final List<ComboBox<Text>> formatComboBox = new ArrayList<>();
    private final List<Select<TextField>> tableColumnSelectors = new ArrayList<>();
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
    public final QuizSettingsForm quizSettings;

    @Autowired
    public QuizForm(QuizSettingsForm quizSettings) {
        this.quizSettings = quizSettings;
        setAlignItems(Alignment.CENTER);
        addQuestionForm();
    }

    private void addQuestionForm() {
        newQuestion.setText(getTranslation("request_form.new_question_button"));
        addBlock();
        add(newQuestion);
    }

    private void addBlock() {
        VerticalLayout block = new VerticalLayout();
        HorizontalLayout upperBlock = new HorizontalLayout();
        VerticalLayout middleBlock = new VerticalLayout();
        HorizontalLayout lowerBlock = new HorizontalLayout();
        NormalTextField questionField = new NormalTextField();
        questionField.setPlaceholder(getTranslation("request_form.question_placeholder"));
        questionField.setValueChangeMode(ValueChangeMode.EAGER);
        Select<TextField> tableColumn = new Select<>();
        tableColumn.setItems(quizSettings.tableColumnDataProvider);
        tableColumn.setItemLabelGenerator(t -> {
            String value = t.getValue();
            if (value.isEmpty()) {
                if (t.getPlaceholder() != null) {
                    return t.getPlaceholder();
                } else {
                    return "";
                }
            } else {
                return value;
            }
        });
        tableColumn.setPlaceholder(getTranslation("request_form.table_column_placeholder"));
        tableColumn.setErrorMessage(getTranslation("request_form.table_column_duplicate"));
        tableColumn.addValueChangeListener(value -> checkTableColumnSelectors());
        tableColumnSelectors.add(tableColumn);
        NormalButton removeBlockButton = new NormalButton(new Icon(VaadinIcon.MINUS));
        removeBlockButton.setText(getTranslation("request_form.remove_question_button"));
        upperBlock.add(questionField, tableColumn, removeBlockButton);
        upperBlock.setWidth("75vw");

        NormalButton newAnswer = new NormalButton(new Icon(VaadinIcon.PLUS), e -> {
            HorizontalLayout answer = new HorizontalLayout();
            SmallTextField answerField = new SmallTextField();
            answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
            answerField.setValueChangeMode(ValueChangeMode.EAGER);
            answerField.addValueChangeListener(c -> questionField.setInvalid(false));
            NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answer));
            removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
            answer.add(answerField, removeAnswer);
            middleBlock.addComponentAtIndex(middleBlock.getComponentCount() - 1, answer);
        });
        newAnswer.setText(getTranslation("request_form.new_answer_button"));
        middleBlock.add(newAnswer);
        newAnswer.click();

        ComboBox<Text> customAnswerFormat = new ComboBox<>();
        customAnswerFormat.setPlaceholder(getTranslation("request_form.custom_answer_format_placeholder"));
        customAnswerFormat.setItems(filters);
        customAnswerFormat.setValue(filters.get(0));
        customAnswerFormat.setItemLabelGenerator(Text::getText);
        formatComboBox.add(customAnswerFormat);
        Checkbox customAnswer = new Checkbox(getTranslation("request_form.custom_answer"));
        customAnswer.setValue(false);
        customAnswerFormat.setReadOnly(true);
        TextField formatMessage = new NormalTextField();
        formatMessage.setReadOnly(true);
        formatMessage.setPlaceholder(getTranslation("request_form.format_message_placeholder"));
        formatMessage.addValueChangeListener(value -> formatMessage.setInvalid(false));
        customAnswer.addValueChangeListener(value -> {
            customAnswerFormat.setReadOnly(!value.getValue());
            if (value.getValue()) {
                questionField.setInvalid(false);
                formatMessage.setReadOnly(customAnswerFormat.getValue().equals(filters.get(0)));
            } else {
                formatMessage.setReadOnly(true);
            }
        });
        customAnswerFormat.addValueChangeListener(value ->
            formatMessage.setReadOnly(customAnswerFormat.isReadOnly() || value.getValue().equals(filters.get(0))));
        lowerBlock.add(customAnswer, customAnswerFormat, formatMessage);
        lowerBlock.setWidth("75vw");
        customAnswer.setWidth("15vw");
        formatMessage.setWidth("36.5vw");

        block.add(upperBlock, middleBlock, lowerBlock);
        block.setAlignItems(Alignment.START);
        block.setSizeFull();
        blocks.add(block);

        removeBlockButton.addClickListener(e -> {
            remove(block);
            blocks.remove(block);
            tableColumnSelectors.remove(tableColumn);
            formatComboBox.remove(customAnswerFormat);
        });

        if (getComponentCount() > 0) {
            addComponentAtIndex(getComponentCount() - 1, block);
        } else {
            add(block);
        }
    }

    public JSONObject save() {
        if (!checkTableColumnSelectors()) {
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
            Select<TextField> tableColumn = (Select<TextField>) upperBlock.getComponentAt(1);
            TextField selectedField = tableColumn.getValue();
            if (selectedField == null || selectedField.getPlaceholder() == null) {
                ;
            } else if (selectedField.isEmpty()) {
                content.put(JSONKeys.TABLE_COLUMN.toString(), selectedField.getPlaceholder());
            } else {
                content.put(JSONKeys.TABLE_COLUMN.toString(), selectedField.getValue());
            }
            JSONObject wrapper = new JSONObject();
            wrapper.put(question.getValue(), content);
            json.put(String.valueOf(questionOrder++), wrapper);
        }
        if (json.isEmpty()) {
            return json;
        }
        String doneMessage = quizSettings.readDoneMessage();
        String greeting = quizSettings.readGreeting();
        String farewell = quizSettings.readFarewell();
        if (doneMessage == null || greeting == null || farewell == null || quizSettings.checkQuizName()) {
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
        json.put(JSONKeys.MULTIPLE_REGISTRATION.toString(), quizSettings.multipleRegistrationValue);
        return json;
    }

    private boolean checkTableColumnSelectors() {
        boolean allValid = true;
        for (Select<TextField> selector1 : tableColumnSelectors) {
            selector1.setInvalid(false);
            for (Select<TextField> selector2 : tableColumnSelectors) {
                if (selector1.getValue() != null && selector2.getValue() != null
                        && !selector1.equals(selector2)
                        && selector1.getValue().equals(selector2.getValue())
                        && !(selector1.getValue().getValue().isEmpty()
                        && (selector1.getValue().getPlaceholder() == null
                        || selector1.getValue().getPlaceholder().isEmpty()))) {
                    selector1.setInvalid(true);
                    allValid = false;
                    break;
                }
            }
        }
        return allValid;
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

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        newQuestion.setText(getTranslation("request_form.new_question_button"));
        filters.get(0).setText(getTranslation("request_form.custom_answer_any"));
        filters.get(1).setText(getTranslation("request_form.custom_answer_only_letters"));
        filters.get(2).setText(getTranslation("request_form.custom_answer_integer"));
        filters.get(3).setText(getTranslation("request_form.custom_answer_date"));
        filters.get(4).setText(getTranslation("request_form.custom_answer_name"));
        filters.get(5).setText(getTranslation("request_form.custom_answer_phone_number"));
        filters.get(6).setText(getTranslation("request_form.custom_answer_email_address"));
        for (ComboBox<Text> comboBox : formatComboBox) {
            comboBox.setItemLabelGenerator(Text::getText);
        }
        for (VerticalLayout block : blocks) {
            HorizontalLayout upperBlock = (HorizontalLayout) block.getComponentAt(0);
            TextField question = (TextField) upperBlock.getComponentAt(0);
            question.setPlaceholder(getTranslation("request_form.question_placeholder"));
            question.setErrorMessage(getTranslation("request_form.question_err_message"));
            Select<TextField> tableColumn = (Select<TextField>) upperBlock.getComponentAt(1);
            tableColumn.setPlaceholder(getTranslation("request_form.table_column_placeholder"));
            tableColumn.setErrorMessage(getTranslation("request_form.table_column_duplicate"));
            ((Button) upperBlock.getComponentAt(2)).setText(
                    getTranslation("request_form.remove_question_button"));
            VerticalLayout middleBlock = (VerticalLayout) block.getComponentAt(1);
            ((Button) middleBlock.getComponentAt(middleBlock.getComponentCount() - 1)).setText(
                    getTranslation("request_form.new_answer_button"));
            for (int i = 0; i < middleBlock.getComponentCount() - 1; i++) {
                HorizontalLayout answer = (HorizontalLayout) middleBlock.getComponentAt(i);
                ((TextField) answer.getComponentAt(0)).setPlaceholder(
                        getTranslation("request_form.answer_placeholder"));
                ((Button) answer.getComponentAt(1)).setText(
                        getTranslation("request_form.remove_answer_button"));
            }
            HorizontalLayout lowerBlock = (HorizontalLayout) block.getComponentAt(2);
            ((Checkbox) lowerBlock.getComponentAt(0)).setLabel(
                    getTranslation("request_form.custom_answer"));
            ((ComboBox<String>) lowerBlock.getComponentAt(1)).setPlaceholder(
                    getTranslation("request_form.custom_answer_format_placeholder"));
            TextField formatMessage = (TextField) lowerBlock.getComponentAt(2);
            formatMessage.setPlaceholder(getTranslation("request_form.format_message_placeholder"));
            formatMessage.setErrorMessage(getTranslation("settings_form.reminder"));

        }
    }
}
