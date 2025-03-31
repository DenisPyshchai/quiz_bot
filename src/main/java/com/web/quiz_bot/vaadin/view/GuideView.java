package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import javax.annotation.security.PermitAll;
import org.springframework.stereotype.Component;

@Route(value = "guide", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
@Component
public class GuideView extends VerticalLayout implements LocaleChangeObserver {

    private final Details createNewQuiz = new Details();
    private final Text createNewQuizSummary = new Text("");
    private final Text createNewQuizGuide1 = new Text("");
    private final Text createNewQuizGuide2 = new Text("");
    private final Text createNewQuizGuide3 = new Text("");
    private final Text createNewQuizGuide4 = new Text("");
    private final Image quizSettings = new Image();
    private final Image quizNext = new Image();
    private final Image quizQuestions = new Image();
    private final Image quizComplete = new Image();
    private final Details findingQuiz = new Details();
    private final Text findingQuizSummary = new Text("");
    private final Text findingQuizGuide1 = new Text("");
    private final Text findingQuizGuide2 = new Text("");
    private final Image findQR = new Image();
    private final Details getQuizResults = new Details();
    private final Text getQuizResultsSummary = new Text("");
    private final Text getQuizResultsGuide1 = new Text("");
    private final Text getQuizResultsGuide2 = new Text("");
    private final Text getQuizResultsGuide3 = new Text("");
    private final Image resultsView = new Image();
    private final Image resultsDownload = new Image();
    private final Image resultsFormat = new Image();
    private final Details managingAccount = new Details();
    private final Text managingAccountSummary = new Text("");
    private final Text managingAccountGuide1 = new Text("");
    private final Text managingAccountGuide2 = new Text("");
    private final Image accountEdit = new Image();
    private final Image accountComplete = new Image();

    public GuideView() {
        setCreateNewQuiz();
        setFindingQuiz();
        setGetQuizResults();
        setManagingAccount();
        add(createNewQuiz, findingQuiz, getQuizResults, managingAccount);
    }

    private void setCreateNewQuiz() {
        VerticalLayout layout = new VerticalLayout();
        Icon icon = getIcon(VaadinIcon.CLIPBOARD_TEXT);
        Span span = new Span(
                createNewQuizGuide1,
                icon,
                createNewQuizGuide2
        );
        HorizontalLayout quizSettingsLayout = new HorizontalLayout();
        quizSettings.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        quizSettings.setWidth("77vw");
        quizSettingsLayout.setSizeFull();
        quizSettingsLayout.setAlignItems(Alignment.CENTER);
        quizSettingsLayout.add(quizSettings);
        HorizontalLayout quizNextLayout = new HorizontalLayout();
        quizNext.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        quizNext.setWidth("77vw");
        quizNextLayout.setSizeFull();
        quizNextLayout.setAlignItems(Alignment.CENTER);
        quizNextLayout.add(quizNext);
        HorizontalLayout quizQuestionsLayout = new HorizontalLayout();
        quizQuestions.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        quizQuestions.setWidth("77vw");
        quizQuestionsLayout.setSizeFull();
        quizQuestionsLayout.setAlignItems(Alignment.CENTER);
        quizQuestionsLayout.add(quizQuestions);
        HorizontalLayout quizCompleteLayout = new HorizontalLayout();
        quizComplete.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        quizComplete.setWidth("77vw");
        quizCompleteLayout.setSizeFull();
        quizCompleteLayout.setAlignItems(Alignment.CENTER);
        quizCompleteLayout.add(quizComplete);
        layout.add(
                span,
                quizSettingsLayout,
                quizNextLayout,
                createNewQuizGuide3,
                quizQuestionsLayout,
                quizCompleteLayout,
                new Span(createNewQuizGuide4));
        icon = VaadinIcon.LIST_OL.create();
        setIcon(icon);
        createNewQuiz.setSummary(new Span(icon, createNewQuizSummary));
        createNewQuiz.addContent(layout);
    }

    private void setFindingQuiz() {
        VerticalLayout layout = new VerticalLayout();
        Icon icon = getIcon(VaadinIcon.DOWNLOAD);
        HorizontalLayout findQRLayout = new HorizontalLayout();
        findQR.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        findQR.setWidth("77vw");
        findQRLayout.setSizeFull();
        findQRLayout.setAlignItems(Alignment.CENTER);
        findQRLayout.add(findQR);
        Span span = new Span(
                findingQuizGuide1,
                icon,
                findingQuizGuide2
        );
        layout.add(span, findQRLayout);
        icon = VaadinIcon.SEARCH.create();
        setIcon(icon);
        findingQuiz.setSummary(new Span(icon, findingQuizSummary));
        findingQuiz.addContent(layout);
    }

    private void setGetQuizResults() {
        VerticalLayout layout = new VerticalLayout();
        Icon icon1 = getIcon(VaadinIcon.EYE);
        Icon icon2 = getIcon(VaadinIcon.DOWNLOAD);
        HorizontalLayout resultsViewLayout = new HorizontalLayout();
        resultsView.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        resultsView.setWidth("77vw");
        resultsViewLayout.setSizeFull();
        resultsViewLayout.setAlignItems(Alignment.CENTER);
        resultsViewLayout.add(resultsView);
        HorizontalLayout resultsDownloadLayout = new HorizontalLayout();
        resultsDownload.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        resultsDownload.setWidth("77vw");
        resultsDownloadLayout.setSizeFull();
        resultsDownloadLayout.setAlignItems(Alignment.CENTER);
        resultsDownloadLayout.add(resultsDownload);
        HorizontalLayout resultsFormatLayout = new HorizontalLayout();
        resultsFormat.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        resultsFormat.setWidth("77vw");
        resultsFormatLayout.setSizeFull();
        resultsFormatLayout.setAlignItems(Alignment.CENTER);
        resultsFormatLayout.add(resultsFormat);
        Span span = new Span(
                getQuizResultsGuide1,
                icon1,
                getQuizResultsGuide2,
                icon2,
                getQuizResultsGuide3
        );
        layout.add(span, resultsViewLayout, resultsDownloadLayout, resultsFormatLayout);
        Icon icon = VaadinIcon.DOWNLOAD.create();
        setIcon(icon);
        getQuizResults.setSummary(new Span(icon, getQuizResultsSummary));
        getQuizResults.addContent(layout);
    }

    private void setManagingAccount() {
        VerticalLayout layout = new VerticalLayout();
        Icon icon = getIcon(VaadinIcon.EDIT);
        Span span = new Span(
                managingAccountGuide1,
                icon,
                managingAccountGuide2
        );
        HorizontalLayout accountEditLayout = new HorizontalLayout();
        accountEdit.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        accountEdit.setWidth("77vw");
        accountEditLayout.setSizeFull();
        accountEditLayout.setAlignItems(Alignment.CENTER);
        accountEditLayout.add(accountEdit);
        HorizontalLayout accountCompleteLayout = new HorizontalLayout();
        accountComplete.getElement().getStyle().set("aspect-ratio", "1182.72 / 535.15");
        accountComplete.setWidth("77vw");
        accountCompleteLayout.setSizeFull();
        accountCompleteLayout.setAlignItems(Alignment.CENTER);
        accountCompleteLayout.add(accountComplete);
        layout.add(span, accountEditLayout, accountCompleteLayout);
        icon = VaadinIcon.USER.create();
        setIcon(icon);
        managingAccount.setSummary(new Span(icon, managingAccountSummary));
        managingAccount.addContent(layout);
    }

    private Icon getIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.setSize("var(--lumo-size-s)");
        icon.getStyle().set("padding", "var(--lumo-space-s)");
        return icon;
    }

    private void setIcon(Icon icon) {
        icon.getStyle()
                .set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        String languageCode = localeChangeEvent.getLocale().getLanguage().toLowerCase();
        createNewQuizSummary.setText(getTranslation("guide.create_new_quiz.1"));
        createNewQuizGuide1.setText(getTranslation("guide.create_new_quiz.2"));
        createNewQuizGuide2.setText(getTranslation("guide.create_new_quiz.3"));
        createNewQuizGuide3.setText(getTranslation("guide.create_new_quiz.4"));
        createNewQuizGuide4.setText(getTranslation("guide.create_new_quiz.5"));
        quizSettings.setSrc(String.format("img/make_quiz/quiz-settings-%s.png", languageCode));
        quizNext.setSrc(String.format("img/make_quiz/quiz-next-%s.png", languageCode));
        quizQuestions.setSrc(String.format("img/make_quiz/quiz-questions-%s.png", languageCode));
        quizComplete.setSrc(String.format("img/make_quiz/quiz-complete-%s.png", languageCode));
        findingQuizSummary.setText(getTranslation("guide.finding_quiz.1"));
        findingQuizGuide1.setText(getTranslation("guide.finding_quiz.2"));
        findingQuizGuide2.setText(getTranslation("guide.finding_quiz.3"));
        findQR.setSrc(String.format("img/find_quiz/find-qr-%s.png", languageCode));
        getQuizResultsSummary.setText(getTranslation("guide.get_quiz_results.1"));
        getQuizResultsGuide1.setText(getTranslation("guide.get_quiz_results.2"));
        getQuizResultsGuide2.setText(getTranslation("guide.get_quiz_results.3"));
        getQuizResultsGuide3.setText(getTranslation("guide.get_quiz_results.4"));
        resultsView.setSrc(String.format("img/results_quiz/results-view-%s.png", languageCode));
        resultsDownload.setSrc(String.format("img/results_quiz/results-download-%s.png", languageCode));
        resultsFormat.setSrc(String.format("img/results_quiz/results-format-%s.png", languageCode));
        managingAccountSummary.setText(getTranslation("guide.managing_account.1"));
        managingAccountGuide1.setText(getTranslation("guide.managing_account.2"));
        managingAccountGuide2.setText(getTranslation("guide.managing_account.3"));
        accountEdit.setSrc(String.format("img/account/account-edit-%s.png", languageCode));
        accountComplete.setSrc(String.format("img/account/account-complete-%s.png", languageCode));
    }
}
