package com.web.quiz_bot.service.listener;

import com.web.quiz_bot.configuration.UTF8Control;
import com.web.quiz_bot.domain.EmailVerificationToken;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.localization.LocalizationProvider;
import com.web.quiz_bot.service.EmailVerificationTokenService;
import com.web.quiz_bot.service.event.UserEmailConformationEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class UserEmailConformationListener implements ApplicationListener<UserEmailConformationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEmailConformationListener.class.getName());
    private static final String BUNDLE_PREFIX = "messages";
    private static final String CONFORMATION_FROM = "noreply@quiz-bot.net";
    private final EmailVerificationTokenService tokenService;
    private final JavaMailSenderImpl mailSender;

    @Autowired
    public UserEmailConformationListener(EmailVerificationTokenService tokenService,
                                         JavaMailSenderImpl mailSender) {
        this.tokenService = tokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(UserEmailConformationEvent event) {
        User user = event.getUser();
        EmailVerificationToken token = tokenService.createToken(user);
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            name = user.getUsername();
        }
        String templatePath = getMessage("mail.template", event.getLocale());
        String subject = getMessage("mail.subject", event.getLocale());
        String title = getMessage("mail.title", event.getLocale());
        String mailText = getMessage("mail.text", event.getLocale(), name);
        String button = getMessage("mail.button", event.getLocale());
        String link = getMessage("mail.link", event.getLocale()) + token.getId();
        String template = loadTemplate(templatePath);
        if (template.isEmpty()) {
            template = getMessage("mail.backup_text", event.getLocale(), name, link);
        } else {
            template = formatTemplate(template, title, mailText, button, link);
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(CONFORMATION_FROM);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(template, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (MailException ignore) {

        }
    }

    private String getMessage(String key, Locale locale, Object... params) {
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale, new UTF8Control());
        String value;
        try {
            value = bundle.getString(key);
        } catch (MissingResourceException e) {
            LoggerFactory.getLogger(LocalizationProvider.class.getName())
                    .warn("Missing resource", e);
            return  "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = formatMessage(value, locale, params);
        }
        return value;
    }

    private String formatMessage(String message, Locale locale, Object... params) {
        MessageFormat formatter = new MessageFormat(message, locale);
        return formatter.format(params);
    }

    private String formatTemplate(String template, String... params) {
        int i = 0;
        String formattedTemplate = template;
        for (String param: params) {
            formattedTemplate = formattedTemplate.replaceAll(String.format("\\{%d\\}", i++), param);
        }
        return formattedTemplate;
    }

    private String loadTemplate(String templatePath) {
        try {
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource(String.format("classpath:%s", templatePath));
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("HTML template for email conformation not found!");
        }
        return "";
    }
}
