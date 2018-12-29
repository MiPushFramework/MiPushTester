package moe.yuuta.mipushtester.topic

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull

class TopicStore(sharedPreferences: SharedPreferences) {
    private val lock = Any()

    companion object {
        private var instance: TopicStore? = null

        @Synchronized
        fun get(@NonNull context: Context): TopicStore {
            if (instance == null) {
                instance = TopicStore(context.getSharedPreferences("subscription", Context.MODE_PRIVATE))
            }
            return instance as TopicStore
        }
    }

    private val sharedPreferences: SharedPreferences = sharedPreferences

    fun getSubscribedIds(): MutableSet<String> {
        synchronized (this.lock) {
            return sharedPreferences.getStringSet("subscribed", mutableSetOf()) ?: mutableSetOf()
        }
    }

    fun isSubscribed (@NonNull id: String): Boolean =
        getSubscribedIds().contains(id)

    @SuppressLint("ApplySharedPref")
    fun subscribe (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getSubscribedIds()
            current.add(id)
            sharedPreferences.edit()
                    .putStringSet("subscribed", current)
                    .commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun unsubscribe (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getSubscribedIds()
            current.remove(id)
            sharedPreferences.edit()
                    .putStringSet("subscribed", current)
                    .commit()
        }
    }
}
