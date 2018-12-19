package moe.yuuta.mipushtester.push;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Locale;

import androidx.annotation.NonNull;
import moe.yuuta.common.Constants;
import moe.yuuta.mipushtester.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {
    private APIInterface apiInterface;
    private static APIManager instance;
    public static @NonNull APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        try {
                            Request original = chain.request();

                            Request.Builder originalBuilder = original.newBuilder();
                            originalBuilder.addHeader(Constants.HEADER_LOCALE, Locale.getDefault().toString())
                                    .addHeader(Constants.HEADER_VERSION, BuildConfig.VERSION_NAME);
                            Request request = originalBuilder.build();
                            return chain.proceed(request);
                        } catch (IOException ignored) {
                            return null;
                        }
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
}
