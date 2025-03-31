package com.web.quiz_bot.vaadin.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.CustomH1;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Route(value = "qr-collection", layout = InnerMenuLayout.class)
@UIScope
@PermitAll
public class QRCollectionView extends VerticalLayout implements BeforeEnterObserver {

    private final Notification noActiveQuizzes = new Notification("Test");
    private final Notification error = new Notification();
    private final UserService userService;
    private final QuizServerService quizServerService;

    @Autowired
    public QRCollectionView(UserService userService, QuizServerService quizServerService) {
        this.userService = userService;
        this.quizServerService = quizServerService;
        addClassName("qr-collection-view");
        noActiveQuizzes.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        noActiveQuizzes.setPosition(Position.MIDDLE);
        error.setPosition(Notification.Position.MIDDLE);
        error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        setAlignItems(Alignment.CENTER);
        add(error, noActiveQuizzes);
    }

    private boolean refresh() {
        try {
            this.removeAll();
            add(error, noActiveQuizzes);
        } catch (IllegalArgumentException ignore) {
            UI.getCurrent().getPage().reload();
        }
        JSONObject quizzes = VaadinViewUtil.fetchQuizzes(
                userService, quizServerService, error, UI.getCurrent().getLocale());
        if (quizzes == null || quizzes.isEmpty() ||quizzes.getJSONArray(JSONKeys.TABLES.toString()).isEmpty()) {
            return false;
        }
        HorizontalLayout layout = new HorizontalLayout();
        for (Object table : quizzes.getJSONArray(JSONKeys.TABLES.toString())) {
            JSONObject data = (JSONObject) table;
            for(Object botUsername : data.getJSONArray(JSONKeys.BOTS.toString())) {
                TextField quizName = new SmallTextField();
                quizName.setValue(data.getString(JSONKeys.TABLE_NAME.toString()));
                quizName.setReadOnly(true);
                String link = String.format("https://t.me/%s", botUsername);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    BitMatrix bitMatrix = qrCodeWriter
                            .encode(link, BarcodeFormat.QR_CODE, 300, 300);
                    MatrixToImageWriter.writeToStream(bitMatrix, "jpg", output);
                } catch (WriterException | IOException e) {
                    throw new RuntimeException(e);
                }
                final StreamResource resource = new StreamResource(
                        data.getString(JSONKeys.TABLE_NAME.toString()) + ".jpg",
                        () -> new ByteArrayInputStream(output.toByteArray()));
                Button getQrCode = new Button(new Icon(VaadinIcon.DOWNLOAD), e -> {
                    final StreamRegistration registration = VaadinSession.getCurrent()
                            .getResourceRegistry().registerResource(resource);
                    UI.getCurrent().getPage().open(registration.getResourceUri().toString());
                });
                getQrCode.getStyle().set("background", "hsla(214, 53%, 23%, 0.16)");
                quizName.setSuffixComponent(getQrCode);
                Image image = new Image(resource, link);
                image.getElement().getStyle().set("aspect-ratio", "1 / 1");
                image.setWidth("20vw");
                VerticalLayout imageBlock = new VerticalLayout(quizName, image);
                if (layout.getComponentCount() == 3) {
                    add(layout);
                    layout.setSizeFull();
                    layout.setAlignItems(Alignment.CENTER);
                    layout = new HorizontalLayout();
                }
                layout.add(imageBlock);
            }
        }
        add(layout);
        layout.setSizeFull();
        layout.setAlignItems(Alignment.START);
        return true;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.setEnabled(VaadinViewUtil.getCurrentUser(userService).isVerified());
        if (!refresh() && !error.isOpened()) {
            VaadinViewUtil.openNotification(noActiveQuizzes, getTranslation("qr_collection.no_active_quizzes"));
        }
    }
}
