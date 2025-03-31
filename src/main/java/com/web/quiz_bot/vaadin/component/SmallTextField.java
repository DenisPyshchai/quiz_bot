package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.textfield.TextField;

@CssImport(value = "./styles/sizing-margins.css")
public class SmallTextField extends TextField {

    public SmallTextField() {
        super();
        init();
    }

    public SmallTextField(String label) {
        super(label);
        init();
    }

    public SmallTextField(String label, String placeholder) {
        super(label, placeholder);
        init();
    }

    public SmallTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
        init();
    }

    public SmallTextField(HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
        init();
    }

    public SmallTextField(String label,
                           HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
        init();
    }

    public SmallTextField(String label, String initialValue,
                           HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
        init();
    }

    private void init() {
        addClassName("small-text-field");
    }
}
