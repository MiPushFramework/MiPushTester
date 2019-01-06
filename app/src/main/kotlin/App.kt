package moe.yuuta.mipushtester

import android.app.Application
import android.os.SystemClock
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import io.fabric.sdk.android.Fabric
import moe.yuuta.mipushtester.log.LogUtils

class App : Application() {
    @Override
    override fun onCreate() {
        super.onCreate()
        val logConfiguration = LogConfiguration.Builder()
                .tag("MiPushTester")
                .jsonFormatter(DefaultJsonFormatter())
                .build()
        val androidPrinter = AndroidPrinter()
        val filePrinter = FilePrinter.Builder(LogUtils.getLogFolder(this))
                .fileNameGenerator(DateFileNameGenerator())
                .cleanStrategy(FileLastModifiedCleanStrategy(1000 * 60 * 60 * 24 * 5))
                .build()
        XLog.init(logConfiguration, androidPrinter, filePrinter)

        if (!BuildConfig.DEBUG && !BuildConfig.FABRIC_KEY.equals("disabled")) {
            Fabric.with(Fabric.Builder(this)
                    .kits(Crashlytics(), Answers())
                    .debuggable(true)
                    .build())
        }
        val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread?, e: Throwable?) {
                val logger = XLog.tag("Crash").build()
                logger.e("App crashed", e)
                SystemClock.sleep(100)
                if (currentHandler != null) currentHandler.uncaughtException(t, e)
            }
        })

        val newLogger = object: LoggerInterface {
            private var logger: com.elvishew.xlog.Logger =
                    XLog.tag("XMPush").build()

            @Override
            override fun setTag(tag: String) {
                logger = XLog.tag("XMPush-$tag").build()
            }
            @Override
            override fun log(content: String, t: Throwable) {
                logger.d(content, t)
            }
            @Override
            override fun log(content: String) {
                logger.d(content)
            }
        }
        Logger.setLogger(this, newLogger)
    }
}
