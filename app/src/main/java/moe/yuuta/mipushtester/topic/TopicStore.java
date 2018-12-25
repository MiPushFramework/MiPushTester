package moe.yuuta.mipushtester.topic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TopicStore {
    private static TopicStore instance;
    private final Object lock = new Object();

    public TopicStore getInstance (@Nullable Context context) {
        if (instance == null) {
            if (context == null)
                throw new IllegalArgumentException("Context shouldn't be null if it isn't created yet");
            instance = TopicStore.create(context.getApplicationContext());
        }
        return instance;
    }

    public static TopicStore create (@NonNull Context context) {
        return new TopicStore(context.getSharedPreferences("subscription", Context.MODE_PRIVATE));
    }

    private final SharedPreferences sharedPreferences;

    private TopicStore (@NonNull SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Set<String> getSubscribedIds () {
        synchronized (this.lock) {
            return sharedPreferences.getStringSet("subscribed", Collections.emptySet());
        }
    }

    public boolean isSubscribed (@NonNull String id) {
        return getSubscribedIds().contains(id);
    }

    @SuppressLint("ApplySharedPref")
    public void subscribe (@NonNull String id) {
        synchronized (this.lock) {
            Set<String> current = new HashSet<>(getSubscribedIds());
            current.add(id);
            sharedPreferences.edit()
                    .putStringSet("subscribed", current)
                    .commit();
        }
    }

    @SuppressLint("ApplySharedPref")
    public void unsubscribe (@NonNull String id) {
        synchronized (this.lock) {
            Set<String> current = new HashSet<>(getSubscribedIds());
            current.remove(id);
            sharedPreferences.edit()
                    .putStringSet("subscribed", current)
                    .commit();
        }
    }
}
