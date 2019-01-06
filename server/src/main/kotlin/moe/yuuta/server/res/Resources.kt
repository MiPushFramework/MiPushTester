package moe.yuuta.server.res

import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.LanguageHeader
import io.vertx.ext.web.RoutingContext
import moe.yuuta.common.Constants.HEADER_LOCALE
import java.nio.charset.StandardCharsets
import java.util.*

object Resources {
    @JvmStatic
    fun getBundle(locale: Locale): ResourceBundle =
        ResourceBundle.getBundle("strings", locale)

    @JvmStatic
    fun getString(key: String, locale: Locale, vararg formatArgs: Any): String {
        val strings = getBundle(locale)
        return String.format(getStringInBundleInUTF8(key, strings), formatArgs)
    }

    @JvmStatic
    private fun getStringInBundleInUTF8(key: String, resourceBundle: ResourceBundle): String {
        return String(resourceBundle.getString(key).toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }

    private val CHINESE_COUNTRY_AND_REGIONS = arrayOf(
            "CN", // 大陆
            "HK", // 香港
            "MO", // 澳门
            "TW", // 台湾
            "CHS", // 简体中文
            "CHT", // 繁体中文
            "Hans", // = CHS
            "Hant", // = CHT
            "SG" // 新加坡
    )

    @JvmStatic
    private fun getRequestHeaderLocale(request: HttpServerRequest?): Locale {
        // TODO: Fix this BAD logic
        if (request == null) {
            return Locale.getDefault()
        }
        val clientCountryOrRegion = request.getHeader(HEADER_LOCALE)
        if (clientCountryOrRegion == null) {
            return Locale.getDefault()
        }
        for (cOR in CHINESE_COUNTRY_AND_REGIONS) {
            if (clientCountryOrRegion.toLowerCase().contains(cOR.toLowerCase())) {
                return Locale("zh" /* We only support zh now*/)
            }
        }
        return Locale.getDefault()
    }

    @JvmStatic
    fun getRequestLocale(languageHeader: LanguageHeader?, request: HttpServerRequest?): Locale {
        return if (languageHeader == null) getRequestHeaderLocale(request) else Locale(getNonNullString(languageHeader.tag()),
                getNonNullString(languageHeader.subtag()),
                getNonNullString(languageHeader.subtag(2)))
    }

    @JvmStatic
    fun getString (key: String, languageHeader: LanguageHeader, vararg formatArgs: Any): String {
        return getValueOrResourcesString(key, getRequestLocale(languageHeader, null), formatArgs)
    }

    @JvmStatic
    fun getString(key: String, routingContext: RoutingContext, vararg formatArgs: Any): String {
        return getValueOrResourcesString(key, getRequestLocale(routingContext.preferredLanguage(), null), formatArgs)
    }

    @JvmStatic
    private fun getNonNullString(nullableString: String?): String {
        if (nullableString == null)
            return ""
        return nullableString
    }

    @JvmStatic
    fun getValueOrResourcesString(key: String, locale: Locale, vararg formatArgs: Any): String {
        return getString(key, locale, formatArgs)
    }
}