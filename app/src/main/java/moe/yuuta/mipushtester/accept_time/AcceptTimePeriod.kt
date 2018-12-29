package moe.yuuta.mipushtester.accept_time

import android.content.Context

import androidx.annotation.NonNull
import androidx.databinding.ObservableInt

data class AcceptTimePeriod(val startHour: ObservableInt = ObservableInt(0),
                            val startMinute: ObservableInt = ObservableInt(0),
                            val endHour: ObservableInt = ObservableInt(23),
                            val endMinute: ObservableInt = ObservableInt(59)) {

    fun resumePush(): Unit {
        startHour.set(0)
        startMinute.set(0)
        endHour.set(0)
        endMinute.set(0)
    }

    fun pausePush(): Unit {
        startHour.set(0)
        startMinute.set(0)
        endHour.set(0)
        endMinute.set(0)
    }

    fun applyToSharedPreferences (@NonNull context: Context): Unit {
        val sharedPreferences = context.applicationContext
                .getSharedPreferences("accept_time", Context.MODE_PRIVATE)
        sharedPreferences.edit()
                .putInt("start_hour", startHour.get())
                .putInt("start_minute", startMinute.get())
                .putInt("end_hour", endHour.get())
                .putInt("end_minute", endMinute.get())
                .apply()
    }

    fun restoreFromSharedPreferences (@NonNull context: Context): Unit {
        val sharedPreferences = context.applicationContext
                .getSharedPreferences("accept_time", Context.MODE_PRIVATE)
        startHour.set(sharedPreferences.getInt("start_hour", 0))
        startMinute.set(sharedPreferences.getInt("start_minute", 0))
        endHour.set(sharedPreferences.getInt("end_hour", 23))
        endMinute.set(sharedPreferences.getInt("end_minute", 59))
        // Maybe they are the same value. We want it to notify listeners to update UI.
        startHour.notifyChange()
        startMinute.notifyChange()
        endHour.notifyChange()
        endMinute.notifyChange()
    }
}
