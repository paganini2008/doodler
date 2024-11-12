package com.github.doodler.common.email;

import java.util.Map;

import org.pegdown.PegDownProcessor;

/**
 * @Description: MdMessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class MdMessageTemplate extends TextMessageTemplate {

    @Override
    public String loadContent(String templateName, String templateContent, Map<String, Object> kwargs) throws Exception {
        String filledTemplateContent = super.loadContent(templateName, templateContent, kwargs);
        PegDownProcessor pdp = new PegDownProcessor(Integer.MAX_VALUE);
        String htmlContent = pdp.markdownToHtml(filledTemplateContent);
        return htmlContent;
    }
}