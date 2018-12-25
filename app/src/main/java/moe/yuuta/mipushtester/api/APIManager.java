package moe.yuuta.mipushtester.api;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import moe.yuuta.common.Constants;
import moe.yuuta.mipushtester.BuildConfig;
import moe.yuuta.mipushtester.push.PushRequest;
import moe.yuuta.mipushtester.topic.Topic;
import moe.yuuta.mipushtester.update.Update;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {
    private final Logger logger = XLog.tag(APIManager.class.getSimpleName()).build();

    private APIInterface apiInterface;
    private static APIManager instance;
    public static @NonNull APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        Request.Builder originalBuilder = original.newBuilder();
                        originalBuilder.addHeader(Constants.HEADER_LOCALE, Locale.getDefault().toString())
                                .addHeader(Constants.HEADER_VERSION, BuildConfig.VERSION_NAME)
                                .addHeader(Constants.HEADER_PRODUCT, BuildConfig.APPLICATION_ID);
                        Request request = originalBuilder.build();
                        return chain.proceed(request);
                    });
            instance.apiInterface = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.SERVER_URL)
                    .client(builder.build())
                    .build().create(APIInterface.class);
        }
        return instance;
    }

    public Call<JsonObject> push (@NonNull PushRequest request) {
        return apiInterface.push(request);
    }

    public Call<Update> getUpdate () {
        return apiInterface.getUpdate();
    }

    public Call<List<Topic>> getAvailableTopics () {
        return apiInterface.getAvailableTopics();
    }
}
