package moe.yuuta.server.topic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {
    @JsonIgnore
    private String titleResource;
    @JsonIgnore
    private String descriptionResource;
    // These values will be set in ApiHandlerImpl
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "description")
    private String description;
    @JsonProperty(value = "id")
    private String id;
    /**
     * A verticle will be ran as a daemon and send messages to this topic
     * This verticle will be started when the topic is registered, and be stopped when the
     * topic is unregistered
     */
    @JsonIgnore
    private TopicExecuteVerticle daemonVerticle;
    @JsonIgnore
    private String daemonVerticleDeploymentId;

    public <T extends TopicExecuteVerticle> Topic (String titleRes, String descriptionRes, String id, TopicExecuteVerticle verticle) {
        this(titleRes, descriptionRes, id, verticle, null);
    }

    private Topic(String titleResource, String descriptionResource, String id, TopicExecuteVerticle verticle, String daemonVerticleDeploymentId) {
        this.titleResource = titleResource;
        this.descriptionResource = descriptionResource;
        this.id = id;
        this.daemonVerticle = verticle;
        this.daemonVerticleDeploymentId = daemonVerticleDeploymentId;
    }

    public String getTitleResource() {
        return titleResource;
    }

    public void setTitleResource(String titleResource) {
        this.titleResource = titleResource;
    }

    public String getDescriptionResource() {
        return descriptionResource;
    }

    public void setDescriptionResource(String descriptionResource) {
        this.descriptionResource = descriptionResource;
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

    void onRegister (Vertx vertx, Handler<AsyncResult<String>> handler) {
        vertx.deployVerticle(daemonVerticle, new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put(TopicExecuteVerticle.EXTRA_TOPIC_ID, id)),
                ar -> {
                    if (ar.succeeded()) {
                        daemonVerticleDeploymentId = ar.result();
                    }
                    handler.handle(ar);
                });
    }

    void onUnRegister (Vertx vertx, Handler<AsyncResult<Void>> handler) {
        if (daemonVerticleDeploymentId == null)
            throw new IllegalStateException("Verticle is not deployed");
        vertx.undeploy(daemonVerticleDeploymentId, handler);
    }
}
