package com.web.quiz_bot.configuration;

import com.google.common.base.Preconditions;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:dao.properties", "classpath:application.properties"})
@EnableJpaRepositories(basePackages = {"com.web.quiz_bot.dao"})
public class ApplicationConfig {

    private final Environment environment;

    @Autowired
    public ApplicationConfig(Environment environment) {
        super();
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(environment.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(Preconditions.checkNotNull(environment.getProperty("jdbc.url")));
        dataSource.setUsername(Preconditions.checkNotNull(environment.getProperty("jdbc.user")));
        dataSource.setPassword(Preconditions.checkNotNull(environment.getProperty("jdbc.password")));

        return dataSource;
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(environment.getProperty("spring.mail.port", Integer.class, 25));
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol",
                environment.getProperty("spring.mail.protocol"));
        props.put("mail.smtp.auth",
                environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable",
                environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.smtp.starttls.required",
                environment.getProperty("spring.mail.properties.mail.smtp.starttls.required"));
        props.put("mail.smtp.ssl.enable",
                environment.getProperty("spring.mail.properties.mail.smtp.ssl.enable"));
        props.put("mail.debug",
                environment.getProperty("spring.mail.properties.mail.debug"));

        return mailSender;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       Environment environment) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaProperties(getHibernateProperties());
        entityManagerFactory.setPackagesToScan("com.web.quiz_bot.domain");

        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory(dataSource(),environment).getObject());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    final Properties getHibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        hibernateProperties.setProperty("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        hibernateProperties.setProperty("hibernate.format_sql", environment.getProperty("hibernate.format_sql"));
        hibernateProperties.setProperty("hibernate.enable_lazy_load_no_trans", environment.getProperty("hibernate.enable_lazy_load_no_trans"));
        hibernateProperties.setProperty("hibernate.implicit_naming_strategy", environment.getProperty("hibernate.implicit_naming_strategy"));
        hibernateProperties.setProperty("hibernate.physical_naming_strategy", environment.getProperty("hibernate.physical_naming_strategy"));

        return hibernateProperties;
    }
}
