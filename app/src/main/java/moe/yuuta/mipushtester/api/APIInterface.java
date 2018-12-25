package moe.yuuta.mipushtester.api;

import com.google.gson.JsonObject;

import java.util.List;

import moe.yuuta.mipushtester.push.PushRequest;
import moe.yuuta.mipushtester.topic.Topic;
import moe.yuuta.mipushtester.update.Update;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("/test")
    Call<JsonObject> push (@Body PushRequest request);

    @GET("/update")
    Call<Update> getUpdate ();

    @GET("/test/topic")
    Call<List<Topic>> getAvailableTopics ();
}
