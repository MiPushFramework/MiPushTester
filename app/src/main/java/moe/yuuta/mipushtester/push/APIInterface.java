package moe.yuuta.mipushtester.push;

import com.google.gson.JsonObject;

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
}
