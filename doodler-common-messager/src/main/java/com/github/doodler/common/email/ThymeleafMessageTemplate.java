package com.github.doodler.common.email;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * @Description: ThymeleafMessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class ThymeleafMessageTemplate implements MessageTemplate {

    public ThymeleafMessageTemplate(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public ThymeleafMessageTemplate() {
        this(defaultConfiguration());
    }

    private final TemplateEngine templateEngine;

    @Override
    public String loadContent(String templateName, String templateContent, Map<String, Object> kwargs) throws Exception {
        Context context = new Context();
        context.setVariables(kwargs);
        return templateEngine.process(templateContent, context);
    }

    private static TemplateEngine defaultConfiguration() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setEnableSpringELCompiler(false);
        engine.setRenderHiddenMarkersBeforeCheckboxes(false);
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setCacheable(false);
        stringTemplateResolver.setTemplateMode(TemplateMode.HTML);
        engine.setTemplateResolver(stringTemplateResolver);
        return engine;
    }
    
}