package com.web.quiz_bot;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@PWA(
		name="quiz-bot",
		shortName="quiz-bot",
		offlinePath="offline.html"
)
public class QuizBotApplication implements AppShellConfigurator {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
		SpringApplication.run(QuizBotApplication.class, args);
	}

}
