package moe.yuuta.server.github;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import static io.vertx.core.http.HttpMethod.GET;

// TODO: Add tests
public class GitHubApi {
    private final HttpClient httpClient;

    public GitHubApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void getLatestRelease (String owner, String repo, Handler<AsyncResult<HttpResponse<Release>>> handler) {
        generateHttpCall(GET, String.format("/repos/%1$s/%2$s/releases/latest", owner, repo))
                .as(BodyCodec.json(Release.class))
                .send(handler);
    }

    private HttpRequest<Buffer> generateHttpCall (HttpMethod method, String path) {
        WebClient webClient = WebClient.wrap(httpClient);
        return webClient.request(method, new RequestOptions()
                .setPort(443)
                .setHost("api.github.com")
                .setSsl(true)
                .setURI(path));
    }
}
