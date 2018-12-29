package moe.yuuta.mipushtester.push

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.Navigation
import com.elvishew.xlog.XLog
import com.google.android.material.snackbar.Snackbar
import moe.shizuku.preference.*
import moe.yuuta.common.Constants
import moe.yuuta.mipushtester.BuildConfig
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.api.APIManager
import moe.yuuta.mipushtester.status.RegistrationStatus
import java.io.IOException
import java.util.*

class SendPushFragment : PreferenceFragment(), Callback {
    val logger = XLog.tag(SendPushFragment::class.simpleName).build()

    private var mTask: PushTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_send_push)
        updateUriLimitStatus(null)
        updatePassThroughLimitStatus(null)
        findPreference("click_type").onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                updateUriLimitStatus(newValue)
                return true
            }
        }
        findPreference("pass_through").onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                updatePassThroughLimitStatus(newValue.toString().toBoolean())
                return true
            }
        }
        val limitLocale: SimpleMenuPreference = findPreference("limit_locale") as SimpleMenuPreference
        limitLocale.entries = arrayOf(
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, Locale.getDefault().displayName),
                getString(R.string.send_push_limit_cannot_template, Locale.getDefault().displayName)
        )
        limitLocale.setEntryValues(R.array.send_push_limit_ev_default)
        val limitVersion: SimpleMenuPreference = findPreference("limit_version") as SimpleMenuPreference
        limitVersion.entries = arrayOf(
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, BuildConfig.VERSION_NAME),
                getString(R.string.send_push_limit_cannot_template, BuildConfig.VERSION_NAME)
        )
        limitVersion.setEntryValues(R.array.send_push_limit_ev_default)
        val limitModel: SimpleMenuPreference = findPreference("limit_model") as SimpleMenuPreference
        limitModel.entries = arrayOf(
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, Build.MODEL),
                getString(R.string.send_push_limit_cannot_template, Build.MODEL)
        )
        limitModel.setEntryValues(R.array.send_push_limit_ev_default)

        // According to the feedback, the SDK will detect region when registration.
        // If its result is China, it will not be able to receive messages which are sent via
        // global API. If not, it will not be able to receive messages which are sent via China API as well.
        // But probably it isn't associated with user's IP. Users in Republic of Singapore (Result: Singapore) will
        // return Singapore, but users in Canada will return China. Maybe because there are official MiPush servers in Republic of Singapore?
        //
        // NOTE 1: The result is associated with "configurations" in Push service (xmsf) as well. But I still not found any evidence
        // which proofs xmsf records user's region. (The mipush.xml in xmsf not contains region info).
        // NOTE 2: User may need to reset the tester and re-enable system-side push service to update this record.
        findPreference("global").summary = Html.fromHtml(getString(R.string.send_push_global_summary,
                if ("China".equals(RegistrationStatus.get(requireContext()).regRegion.get()))
                    getString(R.string.send_push_global_summary_may_not_be_able_to_receive_after_enabling) else
                    getString(R.string.send_push_global_summary_may_not_be_able_to_receive_after_disabling)))
    }

    private fun updateUriLimitStatus(@Nullable newValue: Any?) {
        findPreference("click_url").isEnabled = "1".equals(if(newValue != null) newValue else (findPreference("click_type") as SimpleMenuPreference).value)
    }

    private fun updatePassThroughLimitStatus(@Nullable passThrough: Boolean?) {
        findPreference("sound_uri").isEnabled = !(if (passThrough == null) (findPreference("pass_through") as SwitchPreference).isChecked else
            passThrough)
        findPreference("pass_through_notification").isEnabled = (if(passThrough == null) (findPreference("pass_through") as SwitchPreference).isChecked else
            passThrough)
    }

    @Override
    override fun onCreateOptionsMenu(@NonNull menu: Menu, @NonNull inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_send_push, menu)
        val item = menu.findItem(R.id.action_send)
        val icon = item.icon
        val typedArray = requireContext().obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        val color = typedArray.getColor(0, Color.WHITE)
        typedArray.recycle()
        DrawableCompat.setTint(icon, color)
        item.icon = icon
    }

    @Override
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean =
        when(item.itemId) {
            R.id.action_send -> {
                handleSend()
                true
            }
            else -> false
        }

    private fun handleSend () {
        if (mTask?.isCancelled == false) return
        val request = PushRequest()
        request.registrationId = (RegistrationStatus.get(requireContext()).regId.get() ?: "")
        try {
            val delayStr = (findPreference("delay") as EditTextPreference).text
            val delay = Integer.parseInt(delayStr)
            if (delay < 0 || delay > Constants.PUSH_DELAY_MS_MAX) throw IllegalArgumentException()
            request.delayMs = delay * 1000
        } catch (e: Exception) {
            Snackbar.make(view!!, R.string.error_delay_invalid, Snackbar.LENGTH_SHORT).show()
            return
        }
        request.display = (Integer.parseInt((findPreference("display") as SimpleMenuPreference).value))
        request.passThrough = ((findPreference("pass_through") as SwitchPreference).isChecked)
        request.enforceWiFi = ((findPreference("enforce_wifi") as SwitchPreference).isChecked)
        request.notifyForeground = ((findPreference("notify_foreground") as SwitchPreference).isChecked)
        val limitLocaleValue: String = (findPreference("limit_locale") as SimpleMenuPreference).value
        val limitVersionValue: String = (findPreference("limit_version") as SimpleMenuPreference).value
        val limitModelValue = (findPreference("limit_model") as SimpleMenuPreference).value
        when (limitLocaleValue) {
            "1" -> {
                // Current ONLY
                request.locales = (Collections.singletonList(Locale.getDefault().toString()))
            }
            "2" -> {
                // Except current
                request.localesExcept = (Collections.singletonList(Locale.getDefault().toString()))
            }
            else -> {
            }
        }
        when (limitVersionValue) {
            "1" -> {
                // Current ONLY
                request.versions = (Collections.singletonList(BuildConfig.VERSION_NAME))
            }
            "2" -> {
                // Except current
                request.versionsExcept = (Collections.singletonList(BuildConfig.VERSION_NAME))
            }
            else -> {}
        }
        when (limitModelValue) {
            "1" ->
                // Current ONLY
                request.models = (Collections.singletonList(Build.MODEL))
            "2" ->
                // Except current
                request.modelsExcept = (Collections.singletonList(Build.MODEL))
            else -> {}
        }
        when ((findPreference("click_type") as SimpleMenuPreference).value) {
            "1" -> {
                // Launch URL
                val url: String? = (findPreference ("click_url") as EditTextPreference).text
                if (url == null || url.trim().equals("")) {
                    Snackbar.make(view!!, R.string.error_url_invalid, Snackbar.LENGTH_SHORT).show()
                    return
                }
                request.clickAction = (url)
            }
            "2" ->  {
                // Launch intent (detail page)
                val intent = Intent(requireActivity(), MessageDetailActivity::class.java)
                val uriString = intent.toUri(Intent.URI_INTENT_SCHEME)
                request.clickAction = (uriString)
            }
            else -> {
                // Default, skip
            }
        }
        if ((findPreference("sound_uri") as SwitchPreference).isChecked) {
            request.soundUri = ("android.resource://" + BuildConfig.APPLICATION_ID + "/raw/centaurus")
        }
        request.global = ((findPreference("global") as SwitchPreference).isChecked)
        request.passThroughNotification = ((findPreference("pass_through_notification") as SwitchPreference).isChecked)

        stopTask()
        mTask = PushTask(this, request)
        (mTask as PushTask).execute()
    }

    @Override
    override fun onPreExecute() {
        preferenceScreen.isEnabled = false
        Snackbar.make(view!!, R.string.send_push_sending, Snackbar.LENGTH_INDEFINITE).show()
    }

    @Override
    override fun onPostExecute(result: Exception?) {
        preferenceScreen.isEnabled = true
        stopTask()
        if (result == null) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content)
                    , R.string.send_push_sent, Snackbar.LENGTH_SHORT).show()
            Navigation.findNavController(requireActivity(), R.id.nav_host).navigateUp()
        } else {
            logger.e("Send push", result)
            Snackbar.make(view!!, R.string.send_push_fail, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.error_detail, object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.send_push_fail)
                                    .setMessage(Html.fromHtml("<tt>" +
                                            result.message +
                                            "</tt>"))
                                    .show()
                        }
                    })
                    .show()
        }
    }

    @Override
    override fun onDestroyView() {
        stopTask()
        super.onDestroyView()
    }

    private fun stopTask () {
        if (mTask != null) {
            (mTask as PushTask).cancel(true)
            mTask = null
        }
    }

    private class PushTask(callback: Callback, request: PushRequest) : AsyncTask<Void, Void, Exception?>() {
        private val logger = XLog.tag(PushTask::class.simpleName).build()

        private val callback: Callback
        private val request: PushRequest

        init {
            this.callback = callback
            this.request = request
        }

        @Override
        override fun onPreExecute() {
            super.onPreExecute()
            callback.onPreExecute()
        }

        @Override
        override fun onPostExecute(exception: Exception?) {
            super.onPostExecute(exception)
            callback.onPostExecute(exception)
        }

        @Override
        override fun doInBackground(vararg voids: Void): Exception? =
                try {
                    logger.d("Request: ${request}")
                    val response = APIManager.push(request).execute()
                    if (!response.isSuccessful) {
                        Exception("Unsuccessful code " + response.code() + ", " +
                                "response: " + response.body())
                    }
                    null
                } catch (e: IOException) {
                    e
                }
    }
}
