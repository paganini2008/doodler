package com.github.doodler.common.email;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @Description: EmailMessageSenderConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class EmailMessageSenderConfig {

    @Autowired
    private MailProperties config;

    @ConditionalOnMissingBean
    @Bean
    public EmailOperations emailOperations() {
        return new SpringEmailOperations();
    }

    @ConditionalOnMissingBean(name = "textTemplate")
    @Bean
    public MessageTemplate textTemplate() {
        return new TextMessageTemplate();
    }

    @ConditionalOnMissingBean(name = "htmlTemplate")
    @Bean
    public MessageTemplate htmlTemplate() {
        return new HtmlMessageTemplate();
    }

    @ConditionalOnMissingBean(name = "mdTemplate")
    @Bean
    public MessageTemplate mdTemplate() {
        return new MdMessageTemplate();
    }

    @ConditionalOnMissingBean(name = "ftlTemplate")
    @Bean
    public MessageTemplate ftlTemplate() {
        return new FtlMessageTemplate();
    }

    @ConditionalOnMissingBean(name = "thymeleafTemplate")
    @Bean
    public MessageTemplate thymeleafTemplate() {
        return new ThymeleafMessageTemplate();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(config.getHost());
        javaMailSender.setUsername(config.getUsername());
        javaMailSender.setPassword(config.getPassword());
        javaMailSender.setDefaultEncoding(config.getDefaultEncoding().name());
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.debug", "false");
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        javaMailProperties.setProperty("mail.smtp.starttls.required", "true");
        javaMailProperties.setProperty("mail.smtp.ssl.enable", "true");
        javaMailProperties.setProperty("mail.imap.ssl.socketFactory.fallback", "true");
        javaMailProperties.setProperty("mail.smtp.ssl.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.setProperty("mail.smtp.timeout", "60000");
        javaMailProperties.putAll(config.getProperties());
        javaMailSender.setJavaMailProperties(javaMailProperties);
        return javaMailSender;
    }
}