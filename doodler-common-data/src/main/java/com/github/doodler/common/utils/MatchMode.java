package com.github.doodler.common.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.doodler.common.enums.EnumConstant;
import com.github.doodler.common.enums.EnumUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: MatchMode
 * @Author: Fred Feng
 * @Date: 26/03/2023
 * @Version 1.0.0
 */
public enum MatchMode implements EnumConstant {

    EXACT("exact") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && str.equals(pattern);
        }
    },

    CASE_INSENSITIVE("case_insensitive") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && str.equalsIgnoreCase(pattern);
        }
    },

    STARTS_WITH("starts_with") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && str.startsWith(pattern);
        }
    },

    ENDS_WITH("ends_with") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && str.endsWith(pattern);
        }
    },

    ANY_WHERE("any_where") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && str.contains(pattern);
        }
    },

    REGEX("regex") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && (str.equals(pattern) || str.matches(pattern));
        }
    },

    WILDCARD("wildcard") {
        public boolean matches(String str, String pattern) {
            return StringUtils.isNotBlank(str) && (str.equals(pattern) || FilenameUtils.wildcardMatch(str, pattern));
        }
    };

    public abstract boolean matches(String str, String pattern);

    private MatchMode(String value) {
        this.value = value;
    }

    private final String value;

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getRepr() {
        return this.value;
    }

    @JsonCreator
    public static MatchMode getBy(String value) {
        return EnumUtils.valueOf(MatchMode.class, value);
    }
}