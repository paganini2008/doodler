package com.github.doodler.common.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: TextUtils
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
@UtilityClass
public class TextUtils {

    public boolean containsGarbledText(String text, String charset) {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(text.getBytes());
        CharsetMatch match = detector.detect();
        return match == null || !charset.equalsIgnoreCase(match.getName());
    }

    public static void main(String[] args) {
        String str = "�stream";
        System.out.println(containsGarbledText(str, "UTF-8"));
    }

}
