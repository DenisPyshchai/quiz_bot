package com.web.quiz_bot.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.ServletException;

public final class SecurityUtil {

    private static final String LOGOUT_SUCCESS_URL = "/";

    private SecurityUtil() {}

    public static boolean isAuthenticated() {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        return request != null && request.getUserPrincipal() != null;
    }

    public static boolean authenticate(String username, String password) {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request == null) {
            return false;
        }
        try {
            request.login(username, password);
            return true;
        } catch (ServletException e) {
            return false;
        }
    }

    public static void logout() {
        SecurityContextHolder.clearContext();
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
    }
}