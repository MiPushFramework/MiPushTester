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

class InternalPushReceiver : PushMessageReceiver() {
    private val logger = XLog.tag(InternalPushReceiver::class.simpleName).build()

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

    override fun onCommandResult(context: Context, message: MiPushCommandMessage) {
        val command = message.command
        logger.i("Handle command: $command, code： ${message.resultCode}")
        val commandHumanValue: String
        commandHumanValue = when(command) {
            PushSdkWrapper.COMMAND_REGISTER -> {
                RegistrationStatus.get(context).registered.set(message.resultCode == (ErrorCode.SUCCESS.toLong()))
                context.getString(R.string.command_register)
            }
            else ->
                message.command.toString()
        }
        if (message.resultCode != ErrorCode.SUCCESS.toLong()) {
            logger.e("Received error code ${message.resultCode}")
            Handler(Looper.getMainLooper()).post(object : Runnable {
                override fun run() {
                    Toast.makeText(context.applicationContext, context.getString(R.string.push_receiver_command_error,
                            commandHumanValue, message.resultCode.toString()), Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
