package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.component.TelegramIcon;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;
import org.springframework.stereotype.Component;

@Route(value = "", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
@CssImport(value = "./styles/sizing-margins.css")
@Component
public class MainView extends VerticalLayout implements LocaleChangeObserver {
    private final VerticalLayout manual = new VerticalLayout();
    private final VerticalLayout botScreen = new VerticalLayout();
    private final Image image = new Image();
    private final Text manual_1 = new Text("");
    private final Text manual_2 = new Text("");
    private final Text manual_3 = new Text("");
    private final Text manual_4 = new Text("");
    private final Text manual_5 = new Text("");
    private final Text manual_6 = new Text("");
    private final Text manual_7 = new Text("");

    public MainView() {
        setManual();
        setBotScreen();
        add(new HorizontalLayout(manual, botScreen));
    }

    private void setManual() {
        Span part_1 = new Span();
        Span part_2 = new Span();
        Span part_3 = new Span();
        Span part_4 = new Span();
        Span part_5 = new Span();
        part_1.add(manual_1, new TelegramIcon(), manual_2);
        part_1.setWidth("40vw");
        part_2.add(manual_3);
        part_2.setWidth("40vw");
        part_3.add(manual_4, new TelegramIcon(), manual_5);
        part_3.setWidth("40vw");
        part_4.add(manual_6);
        part_4.setWidth("40vw");
        part_5.add(manual_7);
        part_5.setWidth("40vw");
        manual.add(part_1, part_2, part_3, part_4, part_5);
    }

    private void setBotScreen() {
        image.getStyle().set("aspect-ratio", "1080 / 2400");
        image.getStyle().set("padding-left", "12.5vw");
        image.setWidth("16vw");
        botScreen.add(image);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        String languageCode = localeChangeEvent.getLocale().getLanguage().toLowerCase();
        manual_1.setText(getTranslation("main.manual.1"));
        manual_2.setText(getTranslation("main.manual.2"));
        manual_3.setText(getTranslation("main.manual.3"));
        manual_4.setText(getTranslation("main.manual.4"));
        manual_5.setText(getTranslation("main.manual.5"));
        manual_6.setText(getTranslation("main.manual.6"));
        manual_7.setText(getTranslation("main.manual.7"));
        image.setSrc(String.format("img/bot/bot-view-%s.jpg", languageCode));
    }
}
