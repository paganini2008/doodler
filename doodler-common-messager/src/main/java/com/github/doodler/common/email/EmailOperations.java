package com.github.doodler.common.email;

import java.util.Map;

/**
 * @Description: EmailOperations
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
public interface EmailOperations {

    default void sendPlainTextEmail(String subject, String from, String to, String template,
                                    Map<String, Object> variables) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(subject);
        emailMessage.setFrom(from);
        emailMessage.setTo(new String[]{to});
        emailMessage.setTemplate(template);
        emailMessage.setVariables(variables);
        sendPlainTextEmail(emailMessage);
    }

    void sendPlainTextEmail(EmailMessage emailMessage);

    default void sendRichTextEmail(String subject, String from, String to, RichTextType richTextType, String template,
                                   Map<String, Object> variables) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(subject);
        emailMessage.setFrom(from);
        emailMessage.setTo(new String[]{to});
        emailMessage.setRichTextType(richTextType);
        emailMessage.setTemplate(template);
        emailMessage.setVariables(variables);
        sendRichTextEmail(emailMessage);
    }

    void sendRichTextEmail(EmailMessage emailMessage);
}