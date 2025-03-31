package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.web.quiz_bot.localization.LocalizationProvider;
import java.util.Locale;

@CssImport(value = "./styles/sizing-margins.css")
public class LanguageSelect extends Select<Locale> {

    public LanguageSelect() {
        super();
        init();
        setRenderer(createRenderer());
        setItemLabelGenerator(locale -> locale.getLanguage().toUpperCase());
        setItems(LocalizationProvider.locales);
    }

    private ComponentRenderer<FlexLayout, Locale> createRenderer() {
        return new ComponentRenderer<>(locale -> {
            FlexLayout wrapper = new FlexLayout();
            wrapper.setAlignItems(FlexComponent.Alignment.CENTER);

            StreamResource resource = new StreamResource(String.format("%s.png", locale.getLanguage()),
                    () -> ClassLoader.getSystemResourceAsStream(
                            String.format("META-INF/resources/img/flags/%s.png",
                                    locale.getLanguage())
                    )
            );
            Image image = new Image(resource, "");
            image.setWidth("var(--lumo-size-m)");
            image.getStyle().set("margin-right", "var(--lumo-space-s)");

            Div info = new Div();
            info.setText(locale.getLanguage().toUpperCase());
            wrapper.add(image, info);
            return wrapper;
        });
    }

    private void init() {
        addClassName("language-select");
    }
}
