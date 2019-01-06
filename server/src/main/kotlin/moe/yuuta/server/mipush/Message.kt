package moe.yuuta.server.mipush

import moe.yuuta.server.formprocessor.FormData

@SuppressWarnings("unused")
data class Message(@FormData("title") var title: String = "",
                   @FormData(name = "payload", urlEncode = true) var payload: String = "",
                   @FormData("restricted_package_name") var restrictedPackageName: String = "",
                   @FormData("pass_through") var passThrough: Int = PASS_THROUGH_DISABLED,
                   @FormData("description") var description: String = "",
                   @FormData("time_to_live") var timeToLive: Long = 0,
                   @FormData("time_to_send") var timeToSend: Long = 0,
                   @FormData("notify_id") var notifyId: Int = 0,
                   @FormData("extra.sound_uri") var soundUri: String? = null,
                   @FormData("extra.ticker") var ticker: String? = null,
                   @FormData(name = "extra.notify_foreground", ignorable = false) var notifyForeground: Int = NOTIFY_FOREGROUND_ENABLE,
                   @FormData("extra.notify_effect") var notifyEffect: String = NOTIFY_NOTIFY_EFFECT_LAUNCHER_APP,
                   @FormData("extra.flow_control") var flowControl: Int = FLOW_CONTROL_DISABLE,
                   @FormData("extra.layout_name") var layoutName: Int = 0,
                   @FormData("extra.jobkey") var jobKey: String = "",
                   @FormData("extra.callback") var callback: String = "",
                   @FormData("extra.locale") var locale: String = "",
                   @FormData("extra.locale_not_in") var localeNotIn: String = "",
                   @FormData("extra.model") var model: String = "",
                   @FormData("extra.model_not_in") var modelNotIn: String = "",
                   @FormData("extra.app_version") var appVersion: String = "",
                   @FormData("extra.app_version_not_in") var appVersionNotIn: String = "",
                   @FormData("extra.connpt") var connpt: String? = null,
                   @FormData("notify_type") var notifyType: Int = NOTIFY_TYPE_DEFAULT_ALL,
                   @FormData("extra.intent_uri") var intentUrl: String = "",
                   @FormData("extra.web_uri") var webUri: String? = null,
                   @FormData("registration_id") var regId: String? = null,
                   @FormData("alias") var alias: String? = null,
                   @FormData("user_account") var account: String? = null) {
    companion object {
        const val PASS_THROUGH_DISABLED = 0
        const val PASS_THROUGH_ENABLED = 1

        const val NOTIFY_TYPE_DEFAULT_ALL = -1
        const val NOTIFY_TYPE_DEFAULT_SOUND = 1
        const val NOTIFY_TYPE_DEFAULT_VIBRATE = 2
        const val NOTIFY_TYPE_DEFAULT_LIGHTS = 4

        const val NOTIFY_FOREGROUND_DISABLE = 0
        const val NOTIFY_FOREGROUND_ENABLE = 1

        const val NOTIFY_NOTIFY_EFFECT_LAUNCHER_APP = "1"
        const val NOTIFY_NOTIFY_EFFECT_SPECIFIED_ACTIVITY = "2"
        const val NOTIFY_NOTIFY_EFFECT_URL = "3"

        const val FLOW_CONTROL_DISABLE = 0
        const val FLOW_CONTROL_ENABLE = 1

        const val CONNPT_WIFI = "wifi"
    }
}
