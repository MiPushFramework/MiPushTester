package moe.yuuta.server.api

import com.fasterxml.jackson.annotation.JsonProperty
import moe.yuuta.common.Constants.DISPLAY_ALL
import moe.yuuta.common.Constants.DISPLAY_LIGHTS
import moe.yuuta.common.Constants.DISPLAY_SOUND
import moe.yuuta.common.Constants.DISPLAY_VIBRATE
import moe.yuuta.common.Constants.PUSH_DELAY_MS_MAX
import moe.yuuta.common.Constants.REG_ID_TYPE_ACCOUNT
import moe.yuuta.common.Constants.REG_ID_TYPE_ALIAS
import moe.yuuta.common.Constants.REG_ID_TYPE_REG_ID
import moe.yuuta.server.dataverify.GreatLess
import moe.yuuta.server.dataverify.GreatLessGroup
import moe.yuuta.server.dataverify.Nonnull
import moe.yuuta.server.dataverify.NumberIn

@SuppressWarnings("unused")
data class PushRequest(
        @JsonProperty("registration_id")
        @Nonnull(nonEmpty = true)
        var registrationId: String? = null,
        @JsonProperty("reg_id_type")
        @NumberIn([REG_ID_TYPE_REG_ID.toDouble(), REG_ID_TYPE_ACCOUNT.toDouble(), REG_ID_TYPE_ALIAS.toDouble()])
        var regIdType: Int = REG_ID_TYPE_REG_ID,
        @JsonProperty("delay_ms")
        @GreatLessGroup([GreatLess(targetValue = 0, greater = true, equal = true),
                                        GreatLess(targetValue = PUSH_DELAY_MS_MAX.toLong(), lesser = true, equal = true)])
        var delayMs: Int = 0,
        @JsonProperty("pass_through")
        var passThrough: Boolean = false,
        @JsonProperty("notify_foreground")
        var notifyForeground: Boolean = true,
        @JsonProperty("enforce_wifi")
        var enforceWifi: Boolean = false,
        @JsonProperty("display")
        @NumberIn([DISPLAY_ALL.toDouble(), DISPLAY_LIGHTS.toDouble(), DISPLAY_SOUND.toDouble(), DISPLAY_VIBRATE.toDouble()])
        var display: Int = DISPLAY_ALL,
        @GreatLess(targetValue = 0, greater = true, equal = true)
        @JsonProperty("notify_id")
        var notifyId: Int = 0,
        @JsonProperty("sound_uri")
        var soundUri: String? = null,
        @JsonProperty("callback")
        var callback: String? = null,
        /**
         * The action when the notification is clicked.
         * Null - Launch app
         * else - Launch URL
         * intent: - Launch Intent
         */
        @JsonProperty("click_action")
        var clickAction: String? = null,
        @JsonProperty("locales")
        var locales: MutableList<String>? = null,
        @JsonProperty("locales_except")
        var localesExcept: MutableList<String>? = null,
        @JsonProperty("models")
        var models: MutableList<String>? = null,
        @JsonProperty("models_except")
        var modelsExcept: MutableList<String>? = null,
        @JsonProperty("versions")
        var versions: MutableList<String>? = null /* Version name */,
        @JsonProperty("versions_except")
        var versionsExcept: MutableList<String>? = null /* Version name */,
        @JsonProperty("extras")
        var extras: MutableMap<String, String>? = null,
        @JsonProperty("global")
        var global: Boolean = false,
        @JsonProperty("pass_through_notification")
        var passThroughNotification: Boolean = false
)