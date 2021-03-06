package moe.yuuta.mipushtester.push.internal

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.annotation.NonNull
import com.oasisfeng.condom.*
import com.xiaomi.mipush.sdk.MiPushClient
import moe.yuuta.mipushtester.push.InternalPushReceiver

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

    private fun _wrapContext(@NonNull context: Context): Application =
        // Wrap the context with Condom to prevent it check the permissions.
        // Use it with caution - it may cause unexpected behaviours. We do not
        // want to "optimize" the SDK to get the realist results.
        CondomContext.wrap(context.applicationContext, "MiPushSDK", createOptions()).applicationContext as Application

    fun isDisabled(@NonNull context: Context): Boolean =
            when (context.packageManager.getComponentEnabledSetting(ComponentName(context, CoreProvider::class.java))) {
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> true
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> true
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> false
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> true
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER -> true
                else -> true
            }

    private fun wrapContext(@NonNull context: Context): Application =
            if (isDisabled(context))
                context.applicationContext as Application
            else
                _wrapContext(context)

    private fun createOptions(): CondomOptions =
            CondomOptions()
                    .preventBroadcastToBackgroundPackages(false)
                    .preventServiceInBackgroundPackages(false)
                    .setDryRun(false)
                    .setPackageManagerFactory { base, downstream ->
                        object : PackageManagerWrapper(downstream) {
                            @SuppressLint("RestrictedApi")
                            override fun queryBroadcastReceivers(intent: Intent, flags: Int): MutableList<ResolveInfo> {
                                if ((intent.action ?: "").equals("com.xiaomi.mipush.RECEIVE_MESSAGE") &&
                                        (intent.`package` ?: "").equals(base.packageName)) {
                                    val resolveInfo: ResolveInfo = ResolveInfo()
                                    val activityInfo: ActivityInfo = ActivityInfo()
                                    resolveInfo.activityInfo = activityInfo
                                    activityInfo.name = InternalPushReceiver::class.java.name
                                    return mutableListOf(resolveInfo)
                                }
                                return super.queryBroadcastReceivers(intent, flags)
                            }
                        }
                    }
                    .addKit {
                            it.addPermissionSpoof(android.Manifest.permission.INTERNET)
                            it.addPermissionSpoof(android.Manifest.permission.ACCESS_NETWORK_STATE)
                            it.addPermissionSpoof(android.Manifest.permission.ACCESS_WIFI_STATE)
                            it.addPermissionSpoof(android.Manifest.permission.READ_PHONE_STATE)
                            it.addPermissionSpoof(android.Manifest.permission.GET_TASKS)
                            it.addPermissionSpoof(android.Manifest.permission.VIBRATE)
                            it.addPermissionSpoof("${BuildConfig.APPLICATION_ID}.permission.MIPUSH_RECEIVE")
                        }

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