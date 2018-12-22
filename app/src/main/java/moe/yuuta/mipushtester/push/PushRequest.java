package moe.yuuta.mipushtester.push;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
class PushRequest {
    @SerializedName("registration_id")
    private String registrationId;
    @SerializedName("delay_ms")
    private int delayMs;
    @SerializedName("pass_through")
    private boolean passThrough;
    @SerializedName("notify_foreground")
    private boolean notifyForeground;
    @SerializedName("enforce_wifi")
    private boolean enforceWifi;
    @SerializedName("display")
    private int display;
    @SerializedName("notify_id")
    private int notifyId;
    @SerializedName("sound_uri")
    private String soundUri;
    @SerializedName("callback")
    private String callback;
    /**
     * The action when the notification is clicked.
     * Null - Launch app
     * else - Launch URL
     * intent: - Launch Intent
     */
    @SerializedName("click_action")
    private String clickAction;
    @SerializedName("locales")
    private List<String> locales;
    @SerializedName("locales_except")
    private List<String> localesExcept;
    @SerializedName("models")
    private List<String> models;
    @SerializedName("models_except")
    private List<String> modelsExcept;
    @SerializedName("versions")
    private List<String> versions; // Version name
    @SerializedName("versions_except")
    private List<String> versionsExcept; // Version name
    @SerializedName("extras")
    private Map<String, String> extras;
    @SerializedName("global")
    private boolean global;
    @SerializedName("pass_through_notification")
    private boolean passThroughNotification;

    public boolean isPassThroughNotification() {
        return passThroughNotification;
    }

    public void setPassThroughNotification(boolean passThroughNotification) {
        this.passThroughNotification = passThroughNotification;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public boolean isPassThrough() {
        return passThrough;
    }

    public void setPassThrough(boolean passThrough) {
        this.passThrough = passThrough;
    }

    public boolean isNotifyForeground() {
        return notifyForeground;
    }

    public void setNotifyForeground(boolean notifyForeground) {
        this.notifyForeground = notifyForeground;
    }

    public boolean isEnforceWifi() {
        return enforceWifi;
    }

    public void setEnforceWifi(boolean enforceWifi) {
        this.enforceWifi = enforceWifi;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public String getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(String soundUri) {
        this.soundUri = soundUri;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public List<String> getLocales() {
        return locales;
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public List<String> getLocalesExcept() {
        return localesExcept;
    }

    public void setLocalesExcept(List<String> localesExcept) {
        this.localesExcept = localesExcept;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public List<String> getModelsExcept() {
        return modelsExcept;
    }

    public void setModelsExcept(List<String> modelsExcept) {
        this.modelsExcept = modelsExcept;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<String> getVersionsExcept() {
        return versionsExcept;
    }

    public void setVersionsExcept(List<String> versionsExcept) {
        this.versionsExcept = versionsExcept;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }
}
