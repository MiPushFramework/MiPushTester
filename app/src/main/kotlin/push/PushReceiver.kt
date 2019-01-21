package moe.yuuta.mipushtester.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elvishew.xlog.XLog
import moe.yuuta.mipushtester.utils.Utils

/**
 * A wrapper of InternalPushReceiver which records the income Intent.
 * InternalPushReceiver should not be declared in manifest.
 * To pass the manifest check, we enable an empty receiver (StubPushReceiver) at first, then
 * register push, finally disable it and re-enable this receiver.
 */
class PushReceiver : BroadcastReceiver() {
    private val logger = XLog.tag(PushReceiver::class.simpleName).build()

    override fun onReceive(p0: Context, p1: Intent) {
        logger.i("Received $p1")
        try {
            logger.json(Utils.dumpIntent(p1))
        } catch (e: Throwable) {
            logger.e("Unable to dump intent", e)
        }
        InternalPushReceiver().onReceive(p0, p1)
    }
}