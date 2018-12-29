package moe.yuuta.mipushtester.log

import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import com.elvishew.xlog.XLog
import moe.yuuta.mipushtester.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object LogUtils {
    fun getLogFolder(@NonNull context: Context): String =
            context.cacheDir.path + "/logs"

    @Nullable
    fun getShareIntent(context: Context): Intent? {
        val zipFile = File("${context.externalCacheDir.absolutePath}/logs/logs-" +
                "${SimpleDateFormat("yyyy-mm-dd-H-m-s", Locale.US).format(Date())}.zip")
        try {
            com.elvishew.xlog.LogUtils.compress(getLogFolder(context),
                    zipFile.absolutePath)
            val fileUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    zipFile)
            if (fileUri == null || !zipFile.exists()) {
                throw NullPointerException()
            }
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            var type = context.contentResolver.getType(fileUri)
            if (type == null || type.trim().equals("")) {
                type = "application/zip"
            }
            intent.type = type
            intent.putExtra(EXTRA_STREAM, fileUri)
            return intent
        } catch (e: Exception) {
            try {
                XLog.tag(LogUtils::class.simpleName).build()
                        .e("Share logs", e)
            } catch (ignored: Exception) {}
            System.err.println("Unable to share logs, ${Log.getStackTraceString(e)}")
            return null
        }
    }
}
