package moe.yuuta.mipushtester.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import moe.yuuta.mipushtester.MainActivity
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object Utils {
    fun restart(context: Context) {
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 2333
        val mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)

        Handler(Looper.getMainLooper()).postDelayed( {
            System.exit(0)
            Process.killProcess(Process.myPid())
            Runtime.getRuntime().exit(0)
        }, 100)
    }

    fun dumpIntent(intent: Intent): String {
        val trackMs: Long = System.currentTimeMillis()
        val rootJson: JsonObject = JsonObject()
        rootJson.addProperty("action", intent.action)
        val categoriesJson: JsonArray = JsonArray()
        if (intent.categories != null) {
            for (category in intent.categories) {
                categoriesJson.add(category)
            }
        }
        rootJson.add("categories", categoriesJson)
        rootJson.addProperty("clip_data_has", intent.clipData != null)
        val componentJson: JsonObject = JsonObject()
        componentJson.addProperty("package_name", intent.component?.packageName ?: "(Null)")
        componentJson.addProperty("class_name", intent.component?.className ?: "(Null)")
        rootJson.add("component", componentJson)
        rootJson.addProperty("data_string", intent.dataString)
        rootJson.addProperty("flag_raw", intent.flags)
        val flagFields: Array<Field>? = Intent::class.java.declaredFields
        val flagsJson: JsonArray = JsonArray()
        if (flagFields != null) {
            for (flag in flagFields) {
                if (Modifier.isFinal(flag.modifiers) &&
                        Modifier.isPublic(flag.modifiers) &&
                        Modifier.isStatic(flag.modifiers) &&
                        flag.name.startsWith("FLAG_")) {
                    try {
                        val value: Int = flag.get(null) as Int
                        if ((intent.flags and value) != 0) {
                            flagsJson.add(flag.name)
                        }
                    } catch (ignored: Exception) {}
                }
            }
        }
        rootJson.add("flags", flagsJson)
        rootJson.addProperty("package", intent.`package`)
        rootJson.addProperty("scheme", intent.scheme)
        rootJson.addProperty("selector_has", intent.selector != null)
        rootJson.addProperty("source_bounds_has", intent.sourceBounds != null)
        rootJson.addProperty("type", intent.type)
        rootJson.add("extras", dumpExtras(intent.extras))
        if (intent.hasExtra("mipush_payload")) {
            val payload: ByteArray = intent.getByteArrayExtra("mipush_payload")
            // TODO: Deserialize payload
            val payloadArray: JsonArray = JsonArray()
            for (byte in payload) {
                payloadArray.add(byte)
            }
            rootJson.add("payload", payloadArray)
        }
        rootJson.addProperty("took", System.currentTimeMillis() - trackMs)
        return Gson().toJson(rootJson)
    }

    fun dumpExtras(bundle: Bundle?): JsonArray {
        val extrasJson: JsonArray = JsonArray()
        if (bundle != null) {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                val obj: JsonObject = JsonObject()
                obj.addProperty("value", value?.toString())
                obj.addProperty("key", key)
                obj.addProperty("value_type", value.javaClass.name)
                extrasJson.add(obj)
            }
        }
        return extrasJson
    }
}