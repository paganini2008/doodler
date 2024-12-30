package com.github.doodler.common.utils;

import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: CharsetUtils
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class CharsetUtils {

    public static final String ISO_8859_1_NAME = "ISO-8859-1";

    public static final Charset ISO_8859_1 = toCharset(ISO_8859_1_NAME);

    public static final String US_ASCII_NAME = "US-ASCII";

    public static final Charset US_ASCII = toCharset(US_ASCII_NAME);

    public static final String UTF_16_NAME = "UTF-16";

    public static final Charset UTF_16 = toCharset(UTF_16_NAME);

    public static final String UTF_16BE_NAME = "UTF-16BE";

    public static final Charset UTF_16BE = toCharset(UTF_16BE_NAME);

    public static final String UTF_16LE_NAME = "UTF-16LE";

    public static final Charset UTF_16LE = toCharset(UTF_16LE_NAME);

    public static final String UTF_8_NAME = "UTF-8";

    public static final Charset UTF_8 = toCharset(UTF_8_NAME);

    public static final String GBK_NAME = "GBK";

    public static final Charset GBK = toCharset(GBK_NAME);

    public static final String GB_2312_NAME = "GB2312";

    public static final Charset GB_2312 = toCharset(GB_2312_NAME);

    public static final String BIG_5_NAME = "BIG_5";

    public static final Charset BIG_5 = toCharset(BIG_5_NAME);

    public static final Charset DEFAULT = Charset.defaultCharset();

    public static final byte[] BOM_UTF_8 = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public static final byte[] BOM_UTF_16LE = new byte[] {(byte) 0xFF, (byte) 0xFE};

    public static final byte[] BOM_UTF_16BE = new byte[] {(byte) 0xFE, (byte) 0xFF};

    public static Charset toCharset(String name) {
        if (name == null) {
            return null;
        }
        try {
            return Charset.forName(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

}
