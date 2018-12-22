package moe.yuuta.mipushtester.push;

import android.content.Context;

import com.elvishew.xlog.XLog;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import moe.yuuta.mipushtester.status.RegistrationStatus;

public class PushReceiver extends PushMessageReceiver {
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        // TODO: Add actions
        super.onReceivePassThroughMessage(context, miPushMessage);
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
