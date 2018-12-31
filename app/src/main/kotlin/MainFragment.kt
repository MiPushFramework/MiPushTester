package moe.yuuta.mipushtester

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.os.Process
import android.text.Html
import android.view.*
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.elvishew.xlog.XLog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.xiaomi.mipush.sdk.MiPushClient
import moe.yuuta.mipushtester.accept_time.AcceptTimePeriod
import moe.yuuta.mipushtester.accountAlias.AccountAliasStore
import moe.yuuta.mipushtester.api.APIManager
import moe.yuuta.mipushtester.databinding.FragmentMainBinding
import moe.yuuta.mipushtester.log.LogUtils
import moe.yuuta.mipushtester.status.RegistrationStatus
import moe.yuuta.mipushtester.topic.TopicStore
import moe.yuuta.mipushtester.update.Update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment(), MainFragmentUIHandler {
    private val logger = XLog.tag(MainFragment::class.simpleName).build()

    private lateinit var mRegistrationStatus: RegistrationStatus
    private lateinit var mAcceptTimePeriod: AcceptTimePeriod
    private lateinit var binding: FragmentMainBinding
    private lateinit var mGetUpdateCall: Call<Update>

    @Override
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mRegistrationStatus = RegistrationStatus.get(requireContext())
        mAcceptTimePeriod = AcceptTimePeriod()
    }

    @Nullable
    @Override
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false) as FragmentMainBinding
        mRegistrationStatus.registered.addOnPropertyChangedCallback(mRestoreConfigurationListener)
        mAcceptTimePeriod.startHour.addOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.startMinute.addOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.endHour.addOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.endMinute.addOnPropertyChangedCallback(mApplyAcceptTimeListener)
        binding.status = mRegistrationStatus
        binding.acceptTime = mAcceptTimePeriod
        binding.uiHandler = this
        mAcceptTimePeriod.restoreFromSharedPreferences(requireContext())
        return binding.root
    }

    private val mRestoreConfigurationListener: Observable.OnPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
        @Override
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            if (mRegistrationStatus.registered.get()) {
                for (id in TopicStore.get(requireContext()).getSubscribedIds())
                    MiPushClient.subscribe(requireContext(), id, null)
                for (alias in AccountAliasStore.get(requireContext()).getAlias())
                    MiPushClient.setAlias(requireContext(), alias, null)
                for (account in AccountAliasStore.get(requireContext()).getAccount())
                    MiPushClient.setUserAccount(requireContext(), account, null)
                // TODO: Unset values if it is not contain in stores
            }
        }
    }

    private val mApplyAcceptTimeListener: Observable.OnPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
        @Override
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            // Custom binding adapter not works this time >_<
            var alwaysStatus = ""
            val startHour = mAcceptTimePeriod.startHour.get()
            val startMinute = mAcceptTimePeriod.startMinute.get()
            val endHour = mAcceptTimePeriod.endHour.get()
            val endMinute = mAcceptTimePeriod.endMinute.get()
            if (startHour == 0 &&
                startMinute == 0 &&
                endHour == 0 &&
                endMinute == 0) {
                alwaysStatus = getString(R.string.accept_time_card_current_never)
            }
            if (startHour == 0 &&
                    startMinute == 0 &&
                    endHour == 23 &&
                    endMinute == 59) {
                alwaysStatus = getString(R.string.accept_time_card_current_always)
            }
            binding.layoutAcceptTime.textAcceptTimeCurrent.text = Html.fromHtml(requireContext().getString(R.string.accept_time_card_current,
                    startHour,
                    startMinute,
                    endHour,
                    endMinute,
                    alwaysStatus))
            if (mRegistrationStatus.registered.get()) {
                MiPushClient.setAcceptTime(requireContext(),
                        startHour,
                        startMinute,
                        endHour,
                        endMinute,
                        null)
            }

        }
    }

    @Override
    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postCheckUpdate()
    }

    private fun postCheckUpdate () {
        mGetUpdateCall = APIManager.getUpdate()
        mGetUpdateCall.enqueue(object: Callback<Update> {
            @Override
            override fun onResponse(@NonNull call: Call<Update>, @NonNull response: Response<Update>) {
                if (call.isCanceled) return
                if (!response.isSuccessful) return
                val result: Update? = response.body() as Update
                if (result == null) return
                if (result.versionCode <= BuildConfig.VERSION_CODE) return
                Snackbar.make(binding.root, getString(R.string.update_available,
                        result.versionName), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.view, object : View.OnClickListener {
                            override fun onClick(v: View) {
                                val url = if (shouldOpenGooglePlay())
                                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                                else result.htmlLink
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } catch (ignored: ActivityNotFoundException) {}
                            }
                        }).show()
            }

            private fun shouldOpenGooglePlay(): Boolean =
                 "com.android.vending".equals(requireContext().packageManager.getInstallerPackageName(BuildConfig.APPLICATION_ID))

            @Override
            override fun onFailure(@NonNull call: Call<Update>, @NonNull t: Throwable) {
                if (call.isCanceled) return
                logger.e("Unable to get update", t)
            }
        })
    }

    override fun handleToggleRegister (v: View) {
        mRegistrationStatus.fetchStatus(requireContext())
        if (mRegistrationStatus.registered.get() != true) {
            XLog.i("Registering")
            MiPushClient.registerPush(requireContext(), BuildConfig.XM_APP_ID, BuildConfig.XM_APP_KEY)
        } else {
            XLog.i("Unregistering")
            MiPushClient.unregisterPush(requireContext())
            requireContext().getSharedPreferences("mipush", Context.MODE_PRIVATE)
                    .edit()
                    // It will check if all settings are valid. If it's invalid, it won't register.
                    .putString("devId", null)
                    .apply()
            mRegistrationStatus.registered.set(false)
        }
    }

    override fun handleCreatePush (v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_mainFragment_to_sendPushFragment)
    }

    @SuppressLint("ApplySharedPref")
    override fun handleReset (v: View) {
        MiPushClient.unregisterPush(requireContext())
        requireContext().getSharedPreferences("mipush", MODE_PRIVATE).edit().clear().commit()
        requireContext().getSharedPreferences("mipush_extra", MODE_PRIVATE).edit().clear().commit()
        requireContext().getSharedPreferences("mipush_oc", MODE_PRIVATE).edit().clear().commit()
        Toast.makeText(requireContext(), R.string.reset_toast, Toast.LENGTH_LONG).show()

        val mStartActivity = Intent(requireContext(), MainActivity::class.java)
        val mPendingIntentId = 2333
        val mPendingIntent = PendingIntent.getActivity(requireContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)

        Handler(getMainLooper()).postDelayed(object : Runnable {
            override fun run () {
                Navigation.findNavController(requireActivity(), R.id.nav_host).navigateUp()
                System.exit(0)
                Process.killProcess(Process.myPid())
                Runtime.getRuntime().exit(0)
            }
        }, 100)
    }

    private fun handleGetInfo() {
        val typedArray = requireContext().obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        val summaryColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        val id: String? = mRegistrationStatus.regId.get()
        val region = mRegistrationStatus.regRegion.get()
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(Html.fromHtml(getString(R.string.get_reg_info_dialog,
                        (if (id == null)
                                    getString(R.string.get_reg_info_unavailable)
                                    else id),
                                Integer.toHexString(summaryColor),
                        (if (region == null)
                                    getString(R.string.get_reg_info_unavailable)
                                    else region)
                                )))
                .setPositiveButton(R.string.close, object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, p1: Int) {
                        dialog?.dismiss()
                    }
                })
        if (id != null) {
            val clipboardManager = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            if (clipboardManager != null) {
                builder.setNegativeButton(R.string.copy_id, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        clipboardManager.primaryClip = ClipData.newPlainText(null, id)
                    }
                })
            }
        }
        builder.show()
    }

    @Override
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean =
        when(item.itemId) {
            R.id.action_get_info -> {
                handleGetInfo()
                true
            }
            R.id.action_share_logs -> {
                val logIntent = LogUtils.getShareIntent(requireContext())
                if (logIntent == null) {
                    true
                } else {
                    startActivity(Intent.createChooser(logIntent, getString(R.string.share_logs_chooser_title)))
                    true
                }
            }
            R.id.action_open_source_notices -> {
                startActivity(Intent (requireContext(), OssLicensesMenuActivity::class.java))
                true
            }
            R.id.action_view_on_github -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Trumeet/MiPushTester")))
                } catch (ignored: ActivityNotFoundException) {
                }
                true
            }
            else -> false
        }

    @Override
    override fun onCreateOptionsMenu(@NonNull menu: Menu, @NonNull inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    @Override
    override fun onDestroyView() {
        mGetUpdateCall.cancel()
        mRegistrationStatus.registered.removeOnPropertyChangedCallback(mRestoreConfigurationListener)
        mAcceptTimePeriod.startHour.removeOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.startMinute.removeOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.endHour.removeOnPropertyChangedCallback(mApplyAcceptTimeListener)
        mAcceptTimePeriod.endMinute.removeOnPropertyChangedCallback(mApplyAcceptTimeListener)
        super.onDestroyView()
    }

    @Override
    override fun handleSubscribeTopic(v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_mainFragment_to_topicSubscriptionFragment)
    }

    @Override
    override fun handleSetAcceptTimeStart(v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = TimePickerDialog(requireContext(),
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        mAcceptTimePeriod.startHour.set(hourOfDay)
                        mAcceptTimePeriod.startMinute.set(minute)
                        mAcceptTimePeriod.applyToSharedPreferences(requireContext())
                    }
                },
                mAcceptTimePeriod.startHour.get(),
                mAcceptTimePeriod.startMinute.get(),
                true)
        dialog.show()
    }

    @Override
    override fun handleSetAcceptTimeEnd(v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = TimePickerDialog(requireContext(),
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        mAcceptTimePeriod.endHour.set(hourOfDay)
                        mAcceptTimePeriod.endMinute.set(minute)
                        mAcceptTimePeriod.applyToSharedPreferences(requireContext())
                    }
                },
                mAcceptTimePeriod.endHour.get(),
                mAcceptTimePeriod.endMinute.get(),
                true)
        dialog.show()
    }

    override fun handleSetAlias(v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_mainFragment_to_setAliasFragment)
    }

    override fun handleSetAccount(v: View) {
        if (!(mRegistrationStatus.registered.get())) {
            Toast.makeText(requireContext(), R.string.error_need_register, Toast.LENGTH_SHORT).show()
            return
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_mainFragment_to_setAccountFragment)
    }
}
