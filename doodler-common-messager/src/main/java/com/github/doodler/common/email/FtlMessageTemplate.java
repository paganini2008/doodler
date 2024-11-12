package com.github.doodler.common.email;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * @Description: FtlMessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class FtlMessageTemplate implements MessageTemplate {

    public FtlMessageTemplate() {
        this(defaultConfiguration());
    }

    public FtlMessageTemplate(Configuration configuration) {
        this.configuration = configuration;
    }

    private final Configuration configuration;

    @Override
    public String loadContent(String templateName, String templateContent, Map<String, Object> kwargs) throws Exception {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        configuration.setTemplateLoader(templateLoader);

        Template template = new Template(templateName, templateContent, configuration);
        StringWriter stringWriter = new StringWriter();
        template.process(kwargs, stringWriter);
        return stringWriter.toString();
    }

    private static Configuration defaultConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setNumberFormat("#");
        configuration.setDateFormat("dd/MM/yyyy");
        configuration.setDateTimeFormat("dd/MM/yyyy HH:mm:ss");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setURLEscapingCharset("UTF-8");
        configuration.setLocale(Locale.getDefault());
        return configuration;
    }

    public static void main(String[] args) throws Exception {
        FtlMessageTemplate emailTemplate = new FtlMessageTemplate();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("messageCode", "1");
        dataMap.put("messageStatus", "200");
        dataMap.put("cause", "123");
        dataMap.put("lastModified", new Date());
        String template = FileUtils.readFileToString(new File("d:/work/test.ftl"), "UTF-8");
        String content = emailTemplate.loadContent("test", template, Collections.singletonMap("params", dataMap));
        System.out.println(content);
    }
}