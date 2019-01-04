package moe.yuuta.mipushtester.push

import com.google.gson.annotations.SerializedName

@SuppressWarnings("unused")
data class PushRequest(@SerializedName("registration_id") var registrationId: String? = null /* The registration key should be usec for other type of values */,
                       @SerializedName("reg_id_type") var registrationIdType: Int? = null,
                       @SerializedName("delay_ms") var delayMs: Int? = null,
                       @SerializedName("pass_through") var passThrough: Boolean? = null,
                       @SerializedName("notify_foreground") var notifyForeground: Boolean? = null,
                       @SerializedName("enforce_wifi") var enforceWiFi: Boolean? = null,
                       @SerializedName("display") var display: Int? = null,
                       @SerializedName("notify_id") var notifyId: Int? = null,
                       @SerializedName("sound_uri") var soundUri: String? = null,
                       @SerializedName("callback") var callback: String? = null,
                       /**
                        * The action when the notification is clicked.
                        * Null - Launch app
                        * else - Launch URL
                        * intent: - Launch Intent
                        */
                       @SerializedName("click_action") var clickAction: String? = null,
                       @SerializedName("locales") var locales: MutableList<String>? = null,
                       @SerializedName("locales_except") var localesExcept: MutableList<String>? = null,
                       @SerializedName("models") var models: MutableList<String>? = null,
                       @SerializedName("models_except") var modelsExcept: MutableList<String>? = null,
                       @SerializedName("versions") var versions: MutableList<String>? = null /* Version name */,
                       @SerializedName("versions_except") var versionsExcept: MutableList<String>? = null /*Version name */,
                       @SerializedName("extras") var extras: MutableMap<String, String>? = null,
                       @SerializedName("global") var global: Boolean? = null,
                       @SerializedName("pass_through_notification") var passThroughNotification: Boolean? = null)