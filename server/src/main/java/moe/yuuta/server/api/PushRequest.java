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
    @GreatLess(value = 0, greater = true, equal = true)
    @GreatLess(value = PUSH_DELAY_MS_MAX, lesser = true, equal = true)
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
    @GreatLess(value = 0, greater = true, equal = true)
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
    @JsonProperty("global")
    private boolean global;
    @JsonProperty("pass_through_notification")
    private boolean passThroughNotification;

    boolean isPassThroughNotification() {
        return passThroughNotification;
    }

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

    boolean isGlobal() {
        return global;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public void setPassThrough(boolean passThrough) {
        this.passThrough = passThrough;
    }

    public void setNotifyForeground(boolean notifyForeground) {
        this.notifyForeground = notifyForeground;
    }

    public void setEnforceWifi(boolean enforceWifi) {
        this.enforceWifi = enforceWifi;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public void setSoundUri(String soundUri) {
        this.soundUri = soundUri;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public void setLocalesExcept(List<String> localesExcept) {
        this.localesExcept = localesExcept;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setModelsExcept(List<String> modelsExcept) {
        this.modelsExcept = modelsExcept;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public void setVersionsExcept(List<String> versionsExcept) {
        this.versionsExcept = versionsExcept;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setPassThroughNotification(boolean passThroughNotification) {
        this.passThroughNotification = passThroughNotification;
    }
}
