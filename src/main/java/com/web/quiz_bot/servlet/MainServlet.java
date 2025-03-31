package com.web.quiz_bot.servlet;

import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.VaadinServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = "/*", name = "slot", asyncSupported = true, initParams = {
        @WebInitParam(name = Constants.I18N_PROVIDER, value = "com.web.quiz_bot.localization.LocalizationProvider")})
public class MainServlet extends VaadinServlet {
}
