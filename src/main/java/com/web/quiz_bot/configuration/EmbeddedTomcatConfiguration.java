package com.web.quiz_bot.configuration;

import com.web.quiz_bot.service.listener.UserEmailConformationListener;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.*;

@Configuration
@EnableWebMvc
@ComponentScan
public class EmbeddedTomcatConfiguration implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEmailConformationListener.class.getName());

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.management}")
    private String managementPort;

    @Value("${server.additionalPorts}")
    private String additionalPorts;

    @Value("${ssl.alias}")
    private String keystoreAlias;
    
    @Value("${ssl.password}")
    private String keystorePass;

    @Value("${ssl.path}")
    private String keystorePath;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {

        return factory -> {
            Connector[] additionalConnectors = additionalConnector();
            if (additionalConnectors.length > 0) {
                factory.addAdditionalTomcatConnectors(additionalConnectors);
            }
        };
    }

    private Connector[] additionalConnector() {
        if (this.additionalPorts == null || this.additionalPorts.isEmpty()) {
            return new Connector[0];
        }
        Set<String> defaultPorts = new HashSet<>(Arrays.asList(this.serverPort, this.managementPort));
        String[] ports = this.additionalPorts.split(",");
        List<Connector> result = new ArrayList<>();
        for (String port : ports) {
            if (StringUtils.hasText(port) && !"null".equalsIgnoreCase(port)
                    && !defaultPorts.contains(port)) {
                result.add(getConnector(port));
            }
        }
        return result.toArray(new Connector[] {});
    }

    private Connector getConnector(String port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(Integer.parseInt(port.trim()));
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setAttribute("keystoreAlias", keystoreAlias);
        connector.setAttribute("keystorePass", keystorePass);
        connector.setAttribute("keystoreFile", keystorePath);
        connector.setAttribute("clientAuth", "false");
        connector.setAttribute("sslProtocol", "TLS");
        connector.setAttribute("SSLEnabled", true);
        return connector;
    }
}
