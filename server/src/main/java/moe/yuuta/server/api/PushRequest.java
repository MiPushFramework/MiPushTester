package moe.yuuta.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import moe.yuuta.server.dataverify.GreatLess;
import moe.yuuta.server.dataverify.Nonnull;
import moe.yuuta.server.dataverify.NumberIn;

import static moe.yuuta.common.Constants.DISPLAY_ALL;
import static moe.yuuta.common.Constants.DISPLAY_LIGHTS;
import static moe.yuuta.common.Constants.DISPLAY_SOUND;
import static moe.yuuta.common.Constants.DISPLAY_VIBRATE;
import static moe.yuuta.common.Constants.PUSH_DELAY_MS_MAX;

@SuppressWarnings("unused")
class PushRequest {
    @JsonProperty("registration_id")
    @Nonnull(nonEmpty = true)
    private String registrationId;
    @JsonProperty("delay_ms")
    @GreatLess(value = 0, flags = GreatLess.GREATER | GreatLess.EQUAL)
    @GreatLess(value = PUSH_DELAY_MS_MAX, flags = GreatLess.LESSER | GreatLess.EQUAL)
    private int delayMs;
    @JsonProperty("pass_through")
    private boolean passThrough;
    @JsonProperty("notify_foreground")
    private boolean notifyForeground;
    @JsonProperty("enforce_wifi")
    private boolean enforceWifi;
    @JsonProperty("display")
    @NumberIn({DISPLAY_ALL, DISPLAY_LIGHTS, DISPLAY_SOUND, DISPLAY_VIBRATE})
    private int display;
    @GreatLess(value = 0, flags = GreatLess.GREATER | GreatLess.EQUAL)
    @JsonProperty("notify_id")
    private int notifyId;
    @JsonProperty("sound_uri")
    private String soundUri;
    @JsonProperty("callback")
    private String callback;
    /**
     * The action when the notification is clicked.
     * Null - Launch app
     * else - Launch URL
     * intent: - Launch Intent
     */
    @JsonProperty("click_action")
    private String clickAction;
    @JsonProperty("locales")
    private List<String> locales;
    @JsonProperty("locales_except")
    private List<String> localesExcept;
    @JsonProperty("models")
    private List<String> models;
    @JsonProperty("models_except")
    private List<String> modelsExcept;
    @JsonProperty("versions")
    private List<String> versions; // Version name
    @JsonProperty("versions_except")
    private List<String> versionsExcept; // Version name
    @JsonProperty("extras")
    private Map<String, String> extras;

    Map<String, String> getExtras() {
        return extras;
    }

    List<String> getVersions() {
        return versions;
    }

    List<String> getVersionsExcept() {
        return versionsExcept;
    }

    List<String> getLocalesExcept() {
        return localesExcept;
    }

    List<String> getModelsExcept() {
        return modelsExcept;
    }

    List<String> getModels() {
        return models;
    }

    List<String> getLocales() {
        return locales;
    }

    String getCallback() {
        return callback;
    }

    String getClickAction() {
        return clickAction;
    }

    String getSoundUri() {
        return soundUri;
    }

    int getNotifyId() {
        return notifyId;
    }

    int getDisplay() {
        return display;
    }

    boolean isEnforceWifi() {
        return enforceWifi;
    }

    boolean isNotifyForeground() {
        return notifyForeground;
    }

    boolean isPassThrough() {
        return passThrough;
    }

    String getRegistrationId() {
        return registrationId;
    }

    int getDelayMs() {
        return delayMs;
    }
}
