package moe.yuuta.mipushtester.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Process
import moe.yuuta.mipushtester.MainActivity

object Utils {
    fun restart(context: Context) {
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 2333
        val mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)

        Handler(Looper.getMainLooper()).postDelayed( {
            System.exit(0)
            Process.killProcess(Process.myPid())
            Runtime.getRuntime().exit(0)
        }, 100)
    }
}