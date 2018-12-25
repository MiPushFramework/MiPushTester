package moe.yuuta.mipushtester.topic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "description")
    private String description;
    @SerializedName(value = "id")
    private String id;
    @Expose
    private boolean subscribed;

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
