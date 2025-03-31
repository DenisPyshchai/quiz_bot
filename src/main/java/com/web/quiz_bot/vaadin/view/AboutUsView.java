package com.web.quiz_bot.vaadin.view;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;
import org.springframework.stereotype.Component;

@Route(value = "about-us", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
@Component
public class AboutUsView extends VerticalLayout implements LocaleChangeObserver {

    private final Tab englishCV = new Tab();
    private final Tab germanCV = new Tab();
    private final Tabs tabs = new Tabs();
    private final PdfViewer pdfViewer = new PdfViewer();

    public AboutUsView() {
        addClassName("about-us-view");
        setPdfViewer();
        setTabs();
        add(tabs, pdfViewer);
    }

    private void setTabs() {
        tabs.add(englishCV, germanCV);
        tabs.addSelectedChangeListener(change -> {
            if (change.getSelectedTab().equals(germanCV)) {
                pdfViewer.setSrc(getCV("de"));
            } else {
                pdfViewer.setSrc(getCV("en"));
            }
        });
        if (getLocale().getLanguage().equalsIgnoreCase("de")) {
            tabs.setSelectedTab(germanCV);
        } else {
            tabs.setSelectedTab(englishCV);
        }
    }

    private void setPdfViewer() {
        pdfViewer.setSrc(getCV("en"));
        pdfViewer.hideZoom(true);
        pdfViewer.setHeight("80vh");
    }

    private StreamResource getCV(String languageCode) {
        return new StreamResource("cv.pdf",
                () -> ClassLoader.getSystemResourceAsStream(String.format("META-INF/resources/pdf/cv_%s.pdf", languageCode)));
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        String languageCode = localeChangeEvent.getLocale().getLanguage().toLowerCase();
        englishCV.setLabel(getTranslation("about_us.cv_en"));
        germanCV.setLabel(getTranslation("about_us.cv_de"));
        if (languageCode.equals("en")) {
            tabs.setSelectedTab(englishCV);
        } else if (languageCode.equals("de")) {
            tabs.setSelectedTab(germanCV);
        }
    }
}
