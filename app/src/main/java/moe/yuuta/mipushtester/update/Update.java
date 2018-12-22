package moe.yuuta.mipushtester.update;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Update {
    @SerializedName("version_name")
    private String versionName;
    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("html_link")
    private String htmlLink;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getHtmlLink() {
        return htmlLink;
    }

    public void setHtmlLink(String htmlLink) {
        this.htmlLink = htmlLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Update update = (Update) o;
        return versionCode == update.versionCode &&
                Objects.equals(versionName, update.versionName) &&
                Objects.equals(htmlLink, update.htmlLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionName, versionCode, htmlLink);
    }
}
