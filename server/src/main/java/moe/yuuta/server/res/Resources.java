package moe.yuuta.server.res;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.LanguageHeader;
import io.vertx.ext.web.RoutingContext;

import static moe.yuuta.common.Constants.HEADER_LOCALE;

public class Resources {
    static ResourceBundle getBundle (Locale locale) {
        return ResourceBundle.getBundle("strings", locale);
    }

    public static String getString (String key, Locale locale, Object... formatArgs) {
        ResourceBundle strings = getBundle(locale);
        return String.format(getStringInBundleInUTF8(key, strings), formatArgs);
    }

    private static String getStringInBundleInUTF8 (String key, ResourceBundle resourceBundle) {
        return new String(resourceBundle.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static final String[] CHINESE_COUNTRY_AND_REGIONS = new String[]{
            "CN", // 大陆
            "HK", // 香港
            "MO", // 澳门
            "TW", // 台湾
            "CHS", // 简体中文
            "CHT", // 繁体中文
            "Hans", // = CHS
            "Hant", // = CHT
            "SG" // 新加坡
    };

    private static Locale getRequestHeaderLocale (HttpServerRequest request) {
        // TODO: Fix this BAD logic
        if (request == null) {
            return Locale.getDefault();
        }
        String clientCountryOrRegion = request.getHeader(HEADER_LOCALE);
        if (clientCountryOrRegion == null) {
            return Locale.getDefault();
        }
        for (String cOR : CHINESE_COUNTRY_AND_REGIONS) {
            if (clientCountryOrRegion.toLowerCase().contains(cOR.toLowerCase())) {
                return new Locale("zh" /* We only support zh now*/);
            }
        }
        return Locale.getDefault();
    }

    public static Locale getRequestLocale (LanguageHeader languageHeader, HttpServerRequest request) {
        return languageHeader == null ? getRequestHeaderLocale(request) : new Locale(getNonNullString(languageHeader.tag()),
                getNonNullString(languageHeader.subtag()),
                getNonNullString(languageHeader.subtag(2)));
    }

    public static String getString (String key, LanguageHeader languageHeader, Object... formatArgs) {
        return getValueOrResourcesString(key, getRequestLocale(languageHeader, null));
    }

    public static String getString (String key, RoutingContext routingContext, Object... formatArgs) {
        return getValueOrResourcesString(key, getRequestLocale(routingContext.preferredLanguage(), null), formatArgs);
    }

    private static String getNonNullString (String nullableString) {
        if (nullableString == null)
            return "";
        return nullableString;
    }

    public static String getValueOrResourcesString (String key, Locale locale, Object... formatArgs) {
        return getString(key, locale, formatArgs);
    }
}