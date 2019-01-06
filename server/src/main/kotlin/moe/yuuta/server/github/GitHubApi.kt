package moe.yuuta.server.github

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.RequestOptions
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec

// TODO: Add tests
open class GitHubApi(private val httpClient: HttpClient?) {
    open fun getLatestRelease(owner: String, repo: String, handler: Handler<AsyncResult<HttpResponse<Release>>>) {
        generateHttpCall(GET, String.format("/repos/%1\$s/%2\$s/releases/latest", owner, repo))
                .`as`(BodyCodec.json(Release::class.java))
                .send(handler)
    }

    private fun generateHttpCall(method: HttpMethod, path: String): HttpRequest<Buffer> {
        val webClient = WebClient.wrap(httpClient)
        return webClient.request(method, RequestOptions()
                .setPort(443)
                .setHost("api.github.com")
                .setSsl(true)
                .setURI(path))
    }
}
