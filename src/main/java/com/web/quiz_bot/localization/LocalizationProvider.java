package com.web.quiz_bot.localization;

import com.vaadin.flow.i18n.I18NProvider;
import com.web.quiz_bot.configuration.UTF8Control;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.text.MessageFormat;
import java.util.*;

@Component
public class LocalizationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "i18n";
    public static final Locale LOCALE_EN = new Locale("en", "GB");
    public static final Locale LOCALE_DE = new Locale("de", "DE");
    public static final Locale LOCALE_RU = new Locale("ru", "RU");
    public static final Locale LOCALE_UK = new Locale("ua", "UA");
    public static final List<Locale> locales = List.of(LOCALE_EN, LOCALE_DE, LOCALE_RU, LOCALE_UK);

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String s, Locale locale, Object... objects) {
        if (s == null) {
            return "";
        }
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale, new UTF8Control());

        String value;
        try {
            value = bundle.getString(s);
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(LocalizationProvider.class.getName())
                    .warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + s;
        }
        if (objects.length > 0) {
            value = MessageFormat.format(value, objects, locale);
        }
        return value;
    }
}
