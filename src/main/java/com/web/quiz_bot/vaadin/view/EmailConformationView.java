package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.domain.EmailVerificationToken;
import com.web.quiz_bot.service.EmailVerificationTokenService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.NormalButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = "confirm")
@AnonymousAllowed
public class EmailConformationView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final UserService userService;
    private final EmailVerificationTokenService tokenService;
    private final H1 message = new H1();
    private MessageState messageState = null;
    private final NormalButton cabinet = new NormalButton(getTranslation("outer_menu.cabinet"),
            c -> UI.getCurrent().navigate(GuideView.class));
    private final NormalButton mainRoute = new NormalButton(getTranslation("outer_menu.main_tab"),
            c -> UI.getCurrent().navigate(MainView.class));
    private final NormalButton logIn = new NormalButton(getTranslation("outer_menu.log_in"),
            c -> UI.getCurrent().navigate(LoginView.class));

    @Autowired
    public EmailConformationView(UserService userService, EmailVerificationTokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        cabinet.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cabinet.setIcon(new Icon(VaadinIcon.HOME));
        mainRoute.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        mainRoute.setIcon(new Icon(VaadinIcon.BROWSER));
        logIn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        logIn.setIcon(new Icon(VaadinIcon.SIGN_IN));
        this.setAlignItems(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.removeAll();
        Map<String, List<String>> parameters = beforeEnterEvent.getLocation().getQueryParameters().getParameters();
        UUID tokenId = UUID.fromString(parameters.get("token").get(0));
        EmailVerificationToken token = tokenService.getTokenById(tokenId);
        if (token == null) {
            message.setText(getTranslation("email_conformation.no_such_token"));
            messageState = MessageState.NO_SUCH_TOKEN;
            add(message, new Hr(), mainRoute);
        } else if (token.getExpirationDate().before(Date.from(Instant.now()))) {
            tokenService.deleteTokenById(tokenId);
            message.setText(getTranslation("email_conformation.token_expired"));
            messageState = MessageState.TOKEN_EXPIRED;
            add(message, new Hr(), mainRoute);
        } else if (userService.isEmailConfirmed(token.getUser().getId())) {
            tokenService.deleteTokenById(tokenId);
            message.setText(getTranslation("email_conformation.email_already_confirmed"));
            messageState = MessageState.EMAIL_ALREADY_CONFIRMED;
            add(message, new Hr(), logIn);
        } else {
            if (!userService.isVerified(token.getUser().getId())) {
                userService.verify(token.getUser().getId());
            }
            userService.confirmEmail(token.getUser().getId());
            tokenService.deleteTokenById(tokenId);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            token.getUser().getUsername(), null,
                            AuthorityUtils.createAuthorityList(token.getUser().getRole())
                    )
            );
            VaadinViewUtil.setH1NamedLogo(message, userService,
                    getTranslation("email_conformation.conformation_message"));
            messageState = MessageState.CONFORMATION_MESSAGE;
            add(message, new Hr(), cabinet);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        if (messageState != null) {
            switch (messageState) {
                case NO_SUCH_TOKEN ->
                        message.setText(getTranslation("email_conformation.no_such_token"));
                case TOKEN_EXPIRED ->
                        message.setText(getTranslation("email_conformation.token_expired"));
                case EMAIL_ALREADY_CONFIRMED ->
                        message.setText(getTranslation("email_conformation.email_already_confirmed"));
                case CONFORMATION_MESSAGE ->
                        VaadinViewUtil.setH1NamedLogo(message, userService,
                                getTranslation("email_conformation.conformation_message"));
            }
        }
        cabinet.setText(getTranslation("outer_menu.cabinet"));
        mainRoute.setText(getTranslation("outer_menu.main_tab"));
        logIn.setText(getTranslation("outer_menu.log_in"));
    }

    private enum MessageState {
        NO_SUCH_TOKEN,
        TOKEN_EXPIRED,
        EMAIL_ALREADY_CONFIRMED,
        CONFORMATION_MESSAGE;
    }
}
