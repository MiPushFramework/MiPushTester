package moe.yuuta.mipushtester.push

import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.elvishew.xlog.XLog
import com.google.gson.GsonBuilder
import com.xiaomi.mipush.sdk.MiPushMessage
import moe.yuuta.common.Constants
import moe.yuuta.mipushtester.R

object MessageDetailBindingUtils {
    private val logger = XLog.tag(MessageDetailBindingUtils::class.simpleName).build()

    @JvmStatic
    @BindingAdapter("message")
    fun setMessage (textView: TextView, message: MiPushMessage) {
        val miInternalExtraBuilder = StringBuilder()
        val mptExtraBuilder = StringBuilder()
        for (key in message.extra.keys) {
            val builder = if (key.startsWith(Constants.EXTRA_MIPUSHTESTER_PREFIX))
                    mptExtraBuilder else miInternalExtraBuilder
            builder.append(key)
            builder.append("=")
            builder.append(message.extra.get(key))
            builder.append("\n")
        }

        var miuiVersion = "Unkonwn"
        try {
            val methodGetString = Build::class.java.getDeclaredMethod("getString", String::class.java)
            methodGetString.isAccessible = true
            miuiVersion = methodGetString.invoke(null, "ro.miui.ui.version.name").toString()
        } catch (e: Exception) {
            logger.e("Unable to get property", e)
        }

        textView.movementMethod = ScrollingMovementMethod()
        textView.text = textView.context.getString(R.string.detail,
                message.messageId,
                mptExtraBuilder.toString(),
                miInternalExtraBuilder.toString(),
                if (message.passThrough == 1) "true" else "false",
                GsonBuilder().setPrettyPrinting().create().toJson(message),
                Build.BRAND,
                Build.PRODUCT,
                miuiVersion)
    }
}
