package moe.yuuta.mipushtester.accept_time;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

public class AcceptTimePeriod {
    public final ObservableInt startHour = new ObservableInt(0);
    public final ObservableInt startMinute = new ObservableInt(0);
    public final ObservableInt endHour = new ObservableInt(23);
    public final ObservableInt endMinute = new ObservableInt(59);

    public void resumePush () {
        startHour.set(0);
        startMinute.set(0);
        endHour.set(0);
        endMinute.set(0);
    }

    public void pausePush () {
        startHour.set(0);
        startMinute.set(0);
        endHour.set(0);
        endMinute.set(0);
    }

    public void applyToSharedPreferences (@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences("accept_time", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt("start_hour", startHour.get())
                .putInt("start_minute", startMinute.get())
                .putInt("end_hour", endHour.get())
                .putInt("end_minute", endMinute.get())
                .apply();
    }

    public void restoreFromSharedPreferences (@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences("accept_time", Context.MODE_PRIVATE);
        startHour.set(sharedPreferences.getInt("start_hour", 0));
        startMinute.set(sharedPreferences.getInt("start_minute", 0));
        endHour.set(sharedPreferences.getInt("end_hour", 23));
        endMinute.set(sharedPreferences.getInt("end_minute", 59));
        // Maybe they are the same value. We want it to notify listeners to update UI.
        startHour.notifyChange();
        startMinute.notifyChange();
        endHour.notifyChange();
        endMinute.notifyChange();
    }
}
