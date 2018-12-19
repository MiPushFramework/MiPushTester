package moe.yuuta.mipushtester.log;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import moe.yuuta.mipushtester.BuildConfig;

import static android.content.Intent.EXTRA_STREAM;

public class LogUtils {
    public static String getLogFolder (@NonNull Context context) {
        return context.getCacheDir().getPath() + "/logs";
    }

    @Nullable
    public static Intent getShareIntent(Context context) {
        File zipFile = new File(context.getExternalCacheDir().getAbsolutePath() + "/logs/logs-" +
                new SimpleDateFormat("yyyy-mm-dd-H-m-s", Locale.US).format(new Date()) + ".zip");
        try {
            com.elvishew.xlog.LogUtils.compress(getLogFolder(context),
                    zipFile.getAbsolutePath());
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    zipFile);
            if (fileUri == null || !zipFile.exists()) {
                throw new NullPointerException();
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String type = context.getContentResolver().getType(fileUri);
            if (type == null || type.trim().equals("")) {
                type = "application/zip";
            }
            intent.setType(type);
            intent.putExtra(EXTRA_STREAM, fileUri);
            return intent;
        } catch (IOException | NullPointerException e) {
            // TODO: Maybe some error are occurred?
            return null;
        }
    }
}
