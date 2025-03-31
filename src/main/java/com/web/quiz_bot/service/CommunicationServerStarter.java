package com.web.quiz_bot.service;

import com.web.quiz_bot.servlet.CommunicationServlet;
import org.apache.catalina.*;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
public class CommunicationServerStarter {

    private final Log log = LogFactory.getLog(CommunicationServerStarter.class);
    @Autowired
    private QuizServerService quizServerService;

    @PostConstruct
    public void startServer() throws IOException, LifecycleException {
        start();
    }

    private void start() throws IOException, LifecycleException {
        Tomcat tomcat = new Tomcat();
        String contextPath = "/";
        String appBase = new File(".").getAbsolutePath();

        Context ctx = tomcat.addContext(contextPath, appBase);
        Tomcat.addServlet(ctx, "communicationServlet", new CommunicationServlet(quizServerService));
        ctx.addServletMappingDecoded("/*", "communicationServlet");

        tomcat.setPort(8085);
        tomcat.start();
        tomcat.getConnector();
        log.info("Communication server started");
    }
}
