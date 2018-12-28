package moe.yuuta.mipushtester.push;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.os.Build;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.google.gson.GsonBuilder;
import com.xiaomi.mipush.sdk.MiPushMessage;

import androidx.databinding.BindingAdapter;

import java.lang.reflect.Method;

import moe.yuuta.common.Constants;
import moe.yuuta.mipushtester.R;

public class MessageDetailBindingUtils {
    private static final Logger logger = XLog.tag(MessageDetailBindingUtils.class.getSimpleName()).build();

    @BindingAdapter("message")
    public static void setMessage (TextView textView, MiPushMessage message) {
        StringBuilder miInternalExtraBuilder = new StringBuilder();
        StringBuilder mptExtraBuilder = new StringBuilder();
        for (String key : message.getExtra().keySet()) {
            StringBuilder builder = key.startsWith(Constants.EXTRA_MIPUSHTESTER_PREFIX) ?
                    mptExtraBuilder : miInternalExtraBuilder;
            builder.append(key);
            builder.append("=");
            builder.append(message.getExtra().get(key));
            builder.append("\n");
        }

        String miuiVersion = "Unkonwn";
        try {        
            Method methodGetString = Build.class.getDeclaredMethod("getString", String.class);
            methodGetString.setAccessible(true);
            miuiVersion = methodGetString.invoke(null, "ro.miui.ui.version.name").toString();
        } catch (Exception e) {
            logger.e("Unable to get property", e);
        }

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(textView.getContext().getString(R.string.detail,
                message.getMessageId(),
                mptExtraBuilder.toString(),
                miInternalExtraBuilder.toString(),
                Boolean.toString(message.getPassThrough() == 1),
                new GsonBuilder().setPrettyPrinting().create().toJson(message),
                Build.BRAND,
                Build.PRODUCT,
                miuiVersion));
    }
}
