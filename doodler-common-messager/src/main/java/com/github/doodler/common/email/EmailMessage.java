package com.github.doodler.common.email;

import java.io.File;
import java.util.Map;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: EmailMessage
 * @Author: Fred Feng
 * @Date: 06/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public class EmailMessage implements Message{

    private String subject;
    private String from;
    private String[] to;
    private @Nullable String[] bcc;
    private @Nullable String[] cc;
    private @Nullable String replyTo;
    private String template;
    private @Nullable Map<String, Object> variables;
    private @Nullable RichTextType richTextType;
    private @Nullable Map<String, File> attachments;
	
}
