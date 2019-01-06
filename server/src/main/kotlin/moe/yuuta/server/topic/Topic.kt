package moe.yuuta.server.topic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.VertxImpl
import io.vertx.core.json.JsonObject

@JsonIgnoreProperties(ignoreUnknown = true)
data class Topic (
        @JsonIgnore var titleResource: String,
        @JsonIgnore var descriptionResource: String,
        @JsonProperty(value = "id") var id: String,
        /**
         * A verticle will be ran as a daemon and send messages to this topic
         * This verticle will be started when the topic is registered, and be stopped when the
         * topic is unregistered
         */
        @JsonIgnore var daemonVerticle: TopicExecuteVerticle,
        @JsonIgnore var daemonVerticleDeploymentId: String? = null,
        // These values will be set in ApiHandlerImpl
        @JsonProperty(value = "title") var title: String? = null,
        @JsonProperty(value = "description") var description: String? = null
) {
    fun onRegister(vertx: Vertx, handler: Handler<AsyncResult<String>>) {
        vertx.deployVerticle(daemonVerticle, DeploymentOptions()
                .setConfig(JsonObject()
                        .put(TopicExecuteVerticle.EXTRA_TOPIC_ID, id)))
                {
                    if (it.succeeded()) {
                        daemonVerticleDeploymentId = it.result()
                    }
                    handler.handle(it)
                }
    }

    fun onUnRegister(vertx: Vertx, handler: Handler<AsyncResult<Void>>) {
        if (daemonVerticleDeploymentId == null)
            throw IllegalStateException("Verticle is not deployed")
        if (vertx is VertxImpl && vertx.getDeployment(daemonVerticleDeploymentId) == null) {
            // Already undeployed. (Still don't know why)
            daemonVerticleDeploymentId = null
        } else {
            vertx.undeploy(daemonVerticleDeploymentId, handler)
        }
    }
}
