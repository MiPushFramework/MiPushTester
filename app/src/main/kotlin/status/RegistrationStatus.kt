package moe.yuuta.mipushtester.status

import android.content.Context
import androidx.annotation.NonNull
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import moe.yuuta.mipushtester.push.internal.PushSdkWrapper

data class RegistrationStatus(
        val registered: ObservableBoolean = ObservableBoolean(false),
        val useMIUIPush: ObservableBoolean = ObservableBoolean(false),
        val regId: ObservableField<String> = ObservableField(),
        val regRegion: ObservableField<String> = ObservableField()
) {
    companion object {
        private var instance: RegistrationStatus? = null
                    get() {
                        if (field == null) {
                            field = RegistrationStatus()
                        }
                        return field
                    }
        @Synchronized
        fun get(@NonNull context: Context): RegistrationStatus {
            val status = instance!!
            status.fetchStatus(context)
            return status
        }
    }

    fun fetchStatus (@NonNull context: Context) {
        useMIUIPush.set(PushSdkWrapper.shouldUseMIUIPush(context))
        // It will register push
        regId.set(PushSdkWrapper.getRegId(context))
        regRegion.set(PushSdkWrapper.getAppRegion(context))
        // SDK will detect it's registered or not. Only registered client will return a non-null value.
        // The detection code is optimized, the best way is to use public APIs.
        // BTW, after we unregister it, it will still return a non-null value.....
        registered.set(regId.get() != null)
    }
}
