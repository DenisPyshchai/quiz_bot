package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.textfield.PasswordField;

@CssImport(value = "./styles/sizing-margins.css")
public class NormalPasswordField extends PasswordField {

    public NormalPasswordField() {
        super();
        init();
    }

    public NormalPasswordField(String label) {
        super(label);
        init();
    }

    public NormalPasswordField(String label, String placeholder) {
        super(label, placeholder);
        init();
    }

    public NormalPasswordField(HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(listener);
        init();
    }

    public NormalPasswordField(String label,
                               HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, listener);
        init();
    }

    public NormalPasswordField(String label, String initialValue,
                               HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, initialValue, listener);
        init();
    }

    private void init() {
        addClassName("normal-password-field");
    }
}
