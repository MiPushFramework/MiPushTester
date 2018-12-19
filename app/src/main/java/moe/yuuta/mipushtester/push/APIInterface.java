package moe.yuuta.mipushtester.push;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("/push")
    Call<JsonObject> push (@Body PushRequest request);
}
