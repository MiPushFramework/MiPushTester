package moe.yuuta.mipushtester;

import android.app.Application;
import android.os.SystemClock;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;

import io.fabric.sdk.android.Fabric;
import moe.yuuta.mipushtester.log.LogUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogConfiguration logConfiguration = new LogConfiguration.Builder()
                .tag("MiPushTester")
                .jsonFormatter(new DefaultJsonFormatter())
                .build();
        Printer androidPrinter = new AndroidPrinter();
        Printer filePrinter = new FilePrinter.Builder(LogUtils.getLogFolder(this))
                .fileNameGenerator(new DateFileNameGenerator())
                .cleanStrategy(new FileLastModifiedCleanStrategy(1000 * 60 * 60 * 24 * 5))
                .build();
        XLog.init(logConfiguration, androidPrinter, filePrinter);

        if (!BuildConfig.DEBUG && !BuildConfig.FABRIC_KEY.equals("disabled")) {
            Fabric.with(this, new Crashlytics(), new Answers());
        }
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            com.elvishew.xlog.Logger logger = XLog.tag("Crash").build();
            logger.e("App crashed", e);
            SystemClock.sleep(100);
            if (currentHandler != null) currentHandler.uncaughtException(t, e);
        });

        LoggerInterface newLogger = new LoggerInterface() {
            private com.elvishew.xlog.Logger logger =
                    XLog.tag("XMPush").build();

            @Override
            public void setTag(String tag) {
                logger = XLog.tag("XMPush-" + tag).build();
            }
            @Override
            public void log(String content, Throwable t) {
                logger.d(content, t);
            }
            @Override
            public void log(String content) {
                logger.d(content);
            }
        };
        Logger.setLogger(this, newLogger);
    }
}
