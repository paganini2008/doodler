package com.github.doodler.common.email;

/**
 * @Description: HtmlMessageTemplate
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class HtmlMessageTemplate extends TextMessageTemplate {

    @Override
    public boolean isHtml() {
        return true;
    }
}