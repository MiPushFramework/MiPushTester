package moe.yuuta.mipushtester.push;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.xiaomi.mipush.sdk.MiPushMessage;

import androidx.databinding.BindingAdapter;
import moe.yuuta.common.Constants;
import moe.yuuta.mipushtester.R;

public class MessageDetailBindingUtils {
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
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(textView.getContext().getString(R.string.detail,
                message.getMessageId(),
                mptExtraBuilder.toString(),
                miInternalExtraBuilder.toString(),
                Boolean.toString(message.getPassThrough() == 1),
                new GsonBuilder().setPrettyPrinting().create().toJson(message)));
    }
}
