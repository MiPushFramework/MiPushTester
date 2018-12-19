package moe.yuuta.mipushtester.status;

import android.content.Context;

import com.xiaomi.mipush.sdk.MiPushClient;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public class RegistrationStatus {
    private static RegistrationStatus instance;

    public final ObservableBoolean registered = new ObservableBoolean(false);
    public final ObservableBoolean useMIUIPush = new ObservableBoolean(false);
    public final ObservableField<String> regId = new ObservableField<>();

    public static RegistrationStatus get (@NonNull Context context) {
        context = context.getApplicationContext();
        if (instance == null)
            instance = new RegistrationStatus(context);
        else
            instance.fetchStatus(context);
        return instance;
    }

    private RegistrationStatus(@NonNull Context context) {
        fetchStatus(context);
    }

    public void fetchStatus (@NonNull Context context) {
        useMIUIPush.set(MiPushClient.shouldUseMIUIPush(context));
        // FIXME: It will start push, we should prevent it
        regId.set(MiPushClient.getRegId(context));
        // SDK will detect it's registered or not. Only registered client will return a non-null value.
        // The detection code is optimized, the best way is to use public APIs.
        // BTW, after we unregister it, it will still return a non-null value.....
        registered.set(regId.get() != null);
    }
}
