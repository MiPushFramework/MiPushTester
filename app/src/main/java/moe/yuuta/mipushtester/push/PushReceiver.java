package moe.yuuta.mipushtester.push;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import moe.yuuta.mipushtester.R;
import moe.yuuta.mipushtester.status.RegistrationStatus;

public class PushReceiver extends PushMessageReceiver {
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        Toast.makeText(context.getApplicationContext(), context.getString(R.string.push_receiver_pass_through_received,
                miPushMessage.getMessageId()), Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, MessageDetailActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(PushMessageHelper.KEY_MESSAGE, miPushMessage));
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        XLog.i("Handle register result: " + command);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            XLog.i("Register result = " + message.getResultCode());
            RegistrationStatus.get(context).registered.set(message.getResultCode() == ErrorCode.SUCCESS);
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        // TODO: Add actions
        super.onCommandResult(context, miPushCommandMessage);
    }
}
