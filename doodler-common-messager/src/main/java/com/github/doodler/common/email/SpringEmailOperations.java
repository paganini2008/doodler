package com.github.doodler.common.email;

import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import com.github.doodler.common.BizException;

/**
 * @Description: SpringEmailOperations
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class SpringEmailOperations implements EmailOperations {

    @Qualifier("textTemplate")
    @Autowired
    private MessageTemplate textTemplate;

    @Qualifier("htmlTemplate")
    @Autowired
    private MessageTemplate htmlTemplate;

    @Qualifier("mdTemplate")
    @Autowired
    private MessageTemplate mdTemplate;

    @Qualifier("ftlTemplate")
    @Autowired
    private MessageTemplate ftlTemplate;

    @Qualifier("thymeleafTemplate")
    @Autowired
    private MessageTemplate thymeleafTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendPlainTextEmail(EmailMessage emailMessage) {
        checkRequiredParameters(emailMessage);
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(emailMessage.getTo());
            if (StringUtils.isNotBlank(emailMessage.getFrom())) {
                simpleMailMessage.setFrom(emailMessage.getFrom());
            }
            if (StringUtils.isNotBlank(emailMessage.getReplyTo())) {
                simpleMailMessage.setReplyTo(emailMessage.getReplyTo());
            }
            if (ArrayUtils.isNotEmpty(emailMessage.getCc())) {
                simpleMailMessage.setCc(emailMessage.getCc());
            }
            if (ArrayUtils.isNotEmpty(emailMessage.getBcc())) {
                simpleMailMessage.setBcc(emailMessage.getBcc());
            }
            simpleMailMessage.setSubject(emailMessage.getSubject());
            String text = textTemplate.loadContent(emailMessage.getSubject(), emailMessage.getTemplate(),
                    emailMessage.getVariables());
            simpleMailMessage.setText(text);
            simpleMailMessage.setSentDate(new Date());
            javaMailSender.send(simpleMailMessage);
            if (log.isInfoEnabled()) {
                log.info("Send mail to '{}' successfully.", String.join(",", emailMessage.getTo()));
            }
        } catch (MessagingException e) {
            throw new BizException(ErrorCodes.EMAIL_SETTING_FAULT, e);
        } catch (MailException e) {
            throw new BizException(ErrorCodes.EMAIL_SENDING_FAULT, e);
        } catch (Exception e) {
            throw new BizException(ErrorCodes.EMAIL_SENDING_FAULT, e);
        }
    }

    public void sendRichTextEmail(EmailMessage emailMessage) {
        checkRequiredParameters(emailMessage);
        try {
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setTo(emailMessage.getTo());
            if (StringUtils.isNotBlank(emailMessage.getFrom())) {
                mimeMessageHelper.setFrom(emailMessage.getFrom());
            }
            if (StringUtils.isNotBlank(emailMessage.getReplyTo())) {
                mimeMessageHelper.setReplyTo(emailMessage.getReplyTo());
            }
            if (ArrayUtils.isNotEmpty(emailMessage.getCc())) {
                mimeMessageHelper.setCc(emailMessage.getCc());
            }
            if (ArrayUtils.isNotEmpty(emailMessage.getBcc())) {
                mimeMessageHelper.setBcc(emailMessage.getBcc());
            }
            mimeMessageHelper.setSubject(emailMessage.getSubject());

            String text = "";
            switch (emailMessage.getRichTextType()) {
                case HTML:
                    text = htmlTemplate.loadContent(emailMessage.getSubject(), emailMessage.getTemplate(),
                            emailMessage.getVariables());
                    break;
                case MARKDOWN:
                    text = mdTemplate.loadContent(emailMessage.getSubject(), emailMessage.getTemplate(),
                            emailMessage.getVariables());
                    break;
                case FREEMARKER:
                    text = ftlTemplate.loadContent(emailMessage.getSubject(), emailMessage.getTemplate(),
                            emailMessage.getVariables());
                    break;
                case THYMELEAF:
                    text = thymeleafTemplate.loadContent(emailMessage.getSubject(), emailMessage.getTemplate(),
                            emailMessage.getVariables());
                    break;
                default:
                    throw new UnsupportedOperationException(emailMessage.getRichTextType().toString());
            }
            if (MapUtils.isNotEmpty(emailMessage.getAttachments())) {
                emailMessage.getAttachments().entrySet().forEach(att -> {
                    try {
                        mimeMessageHelper.addAttachment(att.getKey(), att.getValue());
                    } catch (MessagingException e) {
                        if (log.isErrorEnabled()) {
                            log.info(e.getMessage(), e);
                        }
                    }
                });
            }
            mimeMessageHelper.setSentDate(new Date());
            mimeMessageHelper.setText(text, true);
            javaMailSender.send(mimeMailMessage);
            if (log.isInfoEnabled()) {
                log.info("Send mail to '{}' successfully.", String.join(",", emailMessage.getTo()));
            }
        } catch (MessagingException e) {
            throw new BizException(ErrorCodes.EMAIL_SETTING_FAULT, e);
        } catch (MailException e) {
            throw new BizException(ErrorCodes.EMAIL_SENDING_FAULT, e);
        } catch (Exception e) {
            throw new BizException(ErrorCodes.EMAIL_SENDING_FAULT, e);
        }
    }

    private void checkRequiredParameters(EmailMessage emailMessage) {
        Assert.hasText(emailMessage.getSubject(), "Email subject must be required");
        Assert.hasText(emailMessage.getFrom(), "Email sender must be required");
        Assert.notEmpty(emailMessage.getTo(), "Email receiver must be required");
        Assert.hasText(emailMessage.getTemplate(), "Email template content must be required");
    }
}