package moe.yuuta.server.mipush;

import java.util.Objects;

import moe.yuuta.server.formprocessor.FormData;

@SuppressWarnings("unused")
public class Message {
    public static final int PASS_THROUGH_DISABLED = 0;
    public static final int PASS_THROUGH_ENABLED = 1;

    public static final int NOTIFY_TYPE_DEFAULT_ALL = -1;
    public static final int NOTIFY_TYPE_DEFAULT_SOUND = 1;
    public static final int NOTIFY_TYPE_DEFAULT_VIBRATE = 2;
    public static final int NOTIFY_TYPE_DEFAULT_LIGHTS = 4;

    public static final int NOTIFY_FOREGROUND_DISABLE = 0;
    public static final int NOTIFY_FOREGROUND_ENABLE = 1;

    public static final String NOTIFY_NOTIFY_EFFECT_LAUNCHER_APP = "1";
    public static final String NOTIFY_NOTIFY_EFFECT_SPECIFIED_ACTIVITY = "2";
    public static final String NOTIFY_NOTIFY_EFFECT_URL = "3";

    public static final int FLOW_CONTROL_DISABLE = 0;
    public static final int FLOW_CONTROL_ENABLE = 1;

    public static final String CONNPT_WIFI = "wifi";

    @FormData("title")
    private String title;
    @FormData(value = "payload", urlEncode = true)
    private String payload;
    @FormData("restricted_package_name")
    private String restrictedPackageName;
    @FormData("pass_through")
    private int passThrough;
    @FormData("description")
    private String description;
    @FormData("time_to_live")
    private long timeToLive;
    @FormData("time_to_send")
    private long timeToSend;
    @FormData("notify_id")
    private int notifyId;
    @FormData("extra.sound_uri")
    private String soundUri;
    @FormData("extra.ticker")
    private String ticker;
    @FormData(value = "extra.notify_foreground", ignorable = false)
    private int notifyForeground;
    @FormData("extra.notify_effect")
    private String notifyEffect;
    @FormData("extra.flow_control")
    private int flowControl;
    @FormData("extra.layout_name")
    private int layoutName;
    @FormData("extra.layout_value")
    private int layoutValue;
    @FormData("extra.jobkey")
    private String jobKey;
    @FormData("extra.callback")
    private String callback;
    @FormData("extra.locale")
    private String locale;
    @FormData("extra.locale_not_in")
    private String localeNotIn;
    @FormData("extra.model")
    private String model;
    @FormData("extra.model_not_in")
    private String modelNotIn;
    @FormData("extra.app_version")
    private String appVersion;
    @FormData("extra.app_version_not_in")
    private String appVersionNotIn;
    @FormData("extra.connpt")
    private String connpt;
    @FormData("notify_type")
    private int notifyType = NOTIFY_TYPE_DEFAULT_ALL;
    @FormData("extra.intent_uri")
    private String intentUri;
    @FormData("extra.web_uri")
    private String webUri;
    @FormData("registration_id")
    private String regId;
    @FormData("alias")
    private String alias;
    @FormData("user_account")
    private String account;

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIntentUri() {
        return intentUri;
    }

    public void setIntentUri(String intentUri) {
        this.intentUri = intentUri;
    }

    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(String webUri) {
        this.webUri = webUri;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getRestrictedPackageName() {
        return restrictedPackageName;
    }

    public void setRestrictedPackageName(String restrictedPackageName) {
        this.restrictedPackageName = restrictedPackageName;
    }

    public int getPassThrough() {
        return passThrough;
    }

    public void setPassThrough(int passThrough) {
        this.passThrough = passThrough;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getTimeToSend() {
        return timeToSend;
    }

    public void setTimeToSend(long timeToSend) {
        this.timeToSend = timeToSend;
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

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getNotifyForeground() {
        return notifyForeground;
    }

    public void setNotifyForeground(int notifyForeground) {
        this.notifyForeground = notifyForeground;
    }

    public String getNotifyEffect() {
        return notifyEffect;
    }

    public void setNotifyEffect(String notifyEffect) {
        this.notifyEffect = notifyEffect;
    }

    public int getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }

    public int getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(int layoutName) {
        this.layoutName = layoutName;
    }

    public int getLayoutValue() {
        return layoutValue;
    }

    public void setLayoutValue(int layoutValue) {
        this.layoutValue = layoutValue;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocaleNotIn() {
        return localeNotIn;
    }

    public void setLocaleNotIn(String localeNotIn) {
        this.localeNotIn = localeNotIn;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelNotIn() {
        return modelNotIn;
    }

    public void setModelNotIn(String modelNotIn) {
        this.modelNotIn = modelNotIn;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersionNotIn() {
        return appVersionNotIn;
    }

    public void setAppVersionNotIn(String appVersionNotIn) {
        this.appVersionNotIn = appVersionNotIn;
    }

    public String getConnpt() {
        return connpt;
    }

    public void setConnpt(String connpt) {
        this.connpt = connpt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return passThrough == message.passThrough &&
                timeToLive == message.timeToLive &&
                timeToSend == message.timeToSend &&
                notifyId == message.notifyId &&
                notifyForeground == message.notifyForeground &&
                flowControl == message.flowControl &&
                layoutName == message.layoutName &&
                layoutValue == message.layoutValue &&
                notifyType == message.notifyType &&
                Objects.equals(title, message.title) &&
                Objects.equals(payload, message.payload) &&
                Objects.equals(restrictedPackageName, message.restrictedPackageName) &&
                Objects.equals(description, message.description) &&
                Objects.equals(soundUri, message.soundUri) &&
                Objects.equals(ticker, message.ticker) &&
                Objects.equals(notifyEffect, message.notifyEffect) &&
                Objects.equals(jobKey, message.jobKey) &&
                Objects.equals(callback, message.callback) &&
                Objects.equals(locale, message.locale) &&
                Objects.equals(localeNotIn, message.localeNotIn) &&
                Objects.equals(model, message.model) &&
                Objects.equals(modelNotIn, message.modelNotIn) &&
                Objects.equals(appVersion, message.appVersion) &&
                Objects.equals(appVersionNotIn, message.appVersionNotIn) &&
                Objects.equals(connpt, message.connpt) &&
                Objects.equals(intentUri, message.intentUri) &&
                Objects.equals(webUri, message.webUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, payload, restrictedPackageName, passThrough, description, timeToLive, timeToSend, notifyId, soundUri, ticker, notifyForeground, notifyEffect, flowControl, layoutName, layoutValue, jobKey, callback, locale, localeNotIn, model, modelNotIn, appVersion, appVersionNotIn, connpt, notifyType, intentUri, webUri);
    }
}
