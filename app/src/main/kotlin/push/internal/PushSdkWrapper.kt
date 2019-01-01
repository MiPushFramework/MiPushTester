package moe.yuuta.mipushtester.push.internal

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.oasisfeng.condom.*
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * The utils class to operate MiPushClient and other third-party components.
 */
object PushSdkWrapper {
    const val COMMAND_REGISTER = MiPushClient.COMMAND_REGISTER
    const val COMMAND_UNREGISTER = MiPushClient.COMMAND_UNREGISTER
    const val COMMAND_SET_ALIAS = MiPushClient.COMMAND_SET_ALIAS
    const val COMMAND_UNSET_ALIAS = MiPushClient.COMMAND_UNSET_ALIAS
    const val COMMAND_SET_ACCOUNT = MiPushClient.COMMAND_SET_ACCOUNT
    const val COMMAND_UNSET_ACCOUNT = MiPushClient.COMMAND_UNSET_ACCOUNT
    const val COMMAND_SUBSCRIBE_TOPIC = MiPushClient.COMMAND_SUBSCRIBE_TOPIC
    const val COMMAND_UNSUBSCRIBE_TOPIC = MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC
    const val COMMAND_SET_ACCEPT_TIME = MiPushClient.COMMAND_SET_ACCEPT_TIME
    const val PREF_EXTRA = MiPushClient.PREF_EXTRA

    private fun wrapContext(@NonNull context: Context): Application =
        // Wrap the context with Condom to prevent it check the permissions.
        // Use it with caution - it may cause unexpected behaviours. We do not
        // want to "optimize" the SDK to get the realist results.
        CondomContext.wrap(context.applicationContext, "MiPushSDK", createOptions()).applicationContext as Application

    private fun createOptions(): CondomOptions =
            CondomOptions()
                    .preventBroadcastToBackgroundPackages(false)
                    .preventServiceInBackgroundPackages(false)
                    .setDryRun(false)
                    .setOutboundJudge(object : OutboundJudge{
                        override fun shouldAllow(type: OutboundType, intent: Intent?, target_package: String): Boolean {
                            // Always allow to get hte realist results
                            return true
                        }
                    })
                    .addKit(object : CondomKit{
                        override fun onRegister(registry: CondomKit.CondomKitRegistry) {
                            registry.addPermissionSpoof(android.Manifest.permission.INTERNET)
                            registry.addPermissionSpoof(android.Manifest.permission.ACCESS_NETWORK_STATE)
                            registry.addPermissionSpoof(android.Manifest.permission.ACCESS_WIFI_STATE)
                            registry.addPermissionSpoof(android.Manifest.permission.READ_PHONE_STATE)
                            registry.addPermissionSpoof(android.Manifest.permission.GET_TASKS)
                            registry.addPermissionSpoof(android.Manifest.permission.VIBRATE)
                            registry.addPermissionSpoof("${BuildConfig.APPLICATION_ID}.permission.MIPUSH_RECEIVE")
                        }
                    })

    /**
     * Set up Condom and other necessary things.
     */
    fun setup(@NonNull context: Context) {
        CondomProcess.installInCurrentProcess(context.applicationContext as Application, "MiPushComponent", createOptions())
        // Not necessary, I've found that ManifestChecker runs on the main process. But I'd like to leave it here.
        CondomProcessPatch.patchPM(context)
    }

    fun registerPush(@NonNull context: Context, appId: String, appKey: String) {
        MiPushClient.registerPush(wrapContext(context), appId, appKey)
    }

    fun unregisterPush(@NonNull context: Context) {
        MiPushClient.unregisterPush(wrapContext(context))
    }

    fun subscribe(@NonNull context: Context, topic: String) {
        MiPushClient.subscribe(wrapContext(context), topic, null)
    }

    fun unsubscribe(@NonNull context: Context, topic: String) {
        MiPushClient.unsubscribe(wrapContext(context), topic, null)
    }

    fun getAllTopic(@NonNull context: Context): List<String> =
            MiPushClient.getAllTopic(wrapContext(context))

    fun setAlias(@NonNull context: Context, alias: String) {
        MiPushClient.setAlias(wrapContext(context), alias, null)
    }

    fun unsetAlias(@NonNull context: Context, alias: String) {
        MiPushClient.unsetAlias(wrapContext(context), alias, null)
    }

    fun setUserAccount(@NonNull context: Context, userAccount: String) {
        MiPushClient.setUserAccount(wrapContext(context), userAccount, null)
    }

    fun unsetUserAccount(@NonNull context: Context, userAccount: String) {
        MiPushClient.unsetUserAccount(wrapContext(context), userAccount, null)
    }

    fun setAcceptTime(@NonNull context: Context, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        MiPushClient.setAcceptTime(wrapContext(context), startHour, startMinute, endHour, endMinute, null)
    }

    fun shouldUseMIUIPush(@NonNull context: Context): Boolean =
            MiPushClient.shouldUseMIUIPush(wrapContext(context))

    fun getRegId(@NonNull context: Context): String? =
            MiPushClient.getRegId(wrapContext(context))

    fun getAppRegion(@NonNull context: Context): String? =
            MiPushClient.getAppRegion(wrapContext(context))

    fun clearNotification(@NonNull context: Context) {
        MiPushClient.clearNotification(wrapContext(context))
    }
}