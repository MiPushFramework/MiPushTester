package moe.yuuta.mipushtester.push

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.xiaomi.mipush.sdk.*
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.push.internal.PushSdkWrapper
import moe.yuuta.mipushtester.status.RegistrationStatus

class PushReceiver : PushMessageReceiver() {
    override fun onReceivePassThroughMessage(context: Context, miPushMessage: MiPushMessage) {
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                Toast.makeText(context.applicationContext, context.getString(R.string.push_receiver_pass_through_received,
                        miPushMessage.messageId), Toast.LENGTH_SHORT).show()
            }
        })
        context.startActivity(Intent(context, MessageDetailActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(PushMessageHelper.KEY_MESSAGE, miPushMessage))
    }

    override fun onReceiveRegisterResult(context: Context, message: MiPushCommandMessage) {
        val command = message.command
        XLog.i("Handle register result: $command")
        if (PushSdkWrapper.COMMAND_REGISTER.equals(command)) {
            XLog.i("Register result = " + message.resultCode)
            RegistrationStatus.get(context).registered.set(message.resultCode == (ErrorCode.SUCCESS.toLong()))
        }
    }

}
