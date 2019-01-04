package moe.yuuta.mipushtester.accountAlias

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull

class AccountAliasStore(sharedPreferences: SharedPreferences) {
    private val lock = Any()

    companion object {
        private var instance: AccountAliasStore? = null

        @Synchronized
        fun get(@NonNull context: Context): AccountAliasStore {
            if (instance == null) {
                instance = AccountAliasStore(context.getSharedPreferences("account_and_alias", Context.MODE_PRIVATE))
            }
            return instance as AccountAliasStore
        }
    }

    private val sharedPreferences: SharedPreferences = sharedPreferences

    fun getAlias(): MutableSet<String> {
        synchronized (this.lock) {
            return sharedPreferences.getStringSet("alias", mutableSetOf()) ?: mutableSetOf()
        }
    }

    fun hasAlias (@NonNull id: String): Boolean =
            getAlias().contains(id)

    @SuppressLint("ApplySharedPref")
    fun addAlias (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getAlias()
            current.add(id)
            sharedPreferences.edit()
                    .putStringSet("alias", current)
                    .apply()
        }
    }
    
    fun removeAlias (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getAlias()
            current.remove(id)
            sharedPreferences.edit()
                    .putStringSet("alias", current)
                    .apply()
        }
    }

    fun getAccount(): MutableSet<String> {
        synchronized (this.lock) {
            return sharedPreferences.getStringSet("account", mutableSetOf()) ?: mutableSetOf()
        }
    }

    fun hasAccount (@NonNull id: String): Boolean =
            getAccount().contains(id)

    fun addAccount (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getAccount()
            current.add(id)
            sharedPreferences.edit()
                    .putStringSet("account", current)
                    .apply()
        }
    }

    fun removeAccount (@NonNull id: String) {
        synchronized (this.lock) {
            val current = getAccount()
            current.remove(id)
            sharedPreferences.edit()
                    .putStringSet("account", current)
                    .apply()
        }
    }
}
