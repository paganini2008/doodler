package com.github.doodler.common.utils;/**
 * @Description: LocaleUtils
 * @Author: Fred Feng
 * @Date: 27/03/2024
 * @Version: 1.0.0
 */

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: LocaleUtils
 * @Author: Fred Feng
 * @Date: 27/03/2024
 * @Version: 1.0.0
 */
@UtilityClass
public class LocaleUtils {

    private static final Map<String, List<Locale>> langAndCountries;
    private static final Map<String, Locale> countryAndLocales;

    static {
        langAndCountries = Arrays.stream(Locale.getAvailableLocales()).collect(Collectors.groupingBy(Locale::getLanguage));
        countryAndLocales = Arrays.stream(Locale.getAvailableLocales()).collect(ConcurrentHashMap::new,
                (m, e) -> m.put(e.getCountry(), e), ConcurrentHashMap::putAll);
    }

    public Locale getLocale(String lang, String region) {
        return getLocale(lang, region, Locale.UK);
    }

    public Locale getLocale(String lang, String region, Locale defaultLocale) {
        if (StringUtils.isNotBlank(lang) && StringUtils.isNotBlank(region)) {
            Locale matched = Optional.ofNullable(langAndCountries.get(lang)).filter(list -> {
                return list.stream().anyMatch(l -> l.getCountry().equals(region));
            }).map(list -> list.get(0)).orElse(null);
            if (matched == null) {
                return Optional.ofNullable(countryAndLocales.get(region)).orElse(defaultLocale);
            }
            return matched;
        } else if (StringUtils.isNotBlank(lang) && StringUtils.isBlank(region)) {
            return Optional.ofNullable(langAndCountries.get(lang)).map(list -> list.get(0)).orElse(defaultLocale);
        } else if (StringUtils.isBlank(lang) && StringUtils.isNotBlank(region)) {
            return Optional.ofNullable(countryAndLocales.get(region)).orElse(defaultLocale);
        }
        return defaultLocale;
    }
}