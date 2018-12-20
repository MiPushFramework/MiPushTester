package moe.yuuta.mipushtester;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.xiaomi.mipush.sdk.MiPushClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import moe.yuuta.mipushtester.databinding.FragmentMainBinding;
import moe.yuuta.mipushtester.log.LogUtils;
import moe.yuuta.mipushtester.status.RegistrationStatus;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;

public class MainFragment extends Fragment implements MainFragmentUIHandler {
    private RegistrationStatus mRegistrationStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mRegistrationStatus = RegistrationStatus.get(requireContext());
        binding.setStatus(mRegistrationStatus);
        binding.setUiHandler(this);
        return binding.getRoot();
    }

    public void handleToggleRegister (View v) {
        mRegistrationStatus.fetchStatus(requireContext());
        if (!mRegistrationStatus.registered.get()) {
            XLog.i("Registering");
            MiPushClient.registerPush(requireContext(), BuildConfig.XM_APP_ID, BuildConfig.XM_APP_KEY);
        } else {
            XLog.i("Unregistering");
            MiPushClient.unregisterPush(requireContext());
            mRegistrationStatus.registered.set(false);
        }
    }

    public void handleCreatePush (View v) {
        if (!mRegistrationStatus.registered.get()) {
            Toast.makeText(requireContext(), R.string.error_send_push_need_register, Toast.LENGTH_SHORT).show();
            return;
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_mainFragment_to_sendPushFragment);
    }

    @SuppressLint("ApplySharedPref")
    public void handleReset (View v) {
        MiPushClient.unregisterPush(requireContext());
        requireContext().getSharedPreferences("mipush", MODE_PRIVATE).edit().clear().commit();
        requireContext().getSharedPreferences("mipush_extra", MODE_PRIVATE).edit().clear().commit();
        requireContext().getSharedPreferences("mipush_oc", MODE_PRIVATE).edit().clear().commit();
        Toast.makeText(requireContext(), R.string.reset_toast, Toast.LENGTH_LONG).show();
        // 安排上重启
        Intent mStartActivity = new Intent(requireContext(), MainActivity.class);
        int mPendingIntentId = 2333;
        PendingIntent mPendingIntent = PendingIntent.getActivity(requireContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        // 重启前挂掉
        new Handler(getMainLooper()).postDelayed(() -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host).navigateUp();
            System.exit(0);
            Process.killProcess(Process.myPid());
            Runtime.getRuntime().exit(0);
        }, 100);
    }

    private void handleGetID () {
        TypedArray typedArray = requireContext().obtainStyledAttributes(new int[]{ android.R.attr.textColorSecondary });
        int summaryColor = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();
        String id = mRegistrationStatus.regId.get();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setMessage(Html.fromHtml(getString(R.string.get_reg_id_dialog,
                        id == null ? getString(R.string.get_reg_id_unavailable) : id,
                        Integer.toHexString(summaryColor))))
                .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss());
        if (id != null) {
            ClipboardManager clipboardManager = ContextCompat.getSystemService(requireContext(), ClipboardManager.class);
            if (clipboardManager != null) {
                builder.setNegativeButton(android.R.string.copy, (dialog, which) -> clipboardManager.setPrimaryClip(ClipData.newPlainText(null, id)));
            }
        }
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_get_id:
                handleGetID();
                return true;
            case R.id.action_share_logs:
                startActivity(Intent.createChooser(LogUtils.getShareIntent(requireContext()), getString(R.string.share_logs_chooser_title)));
                break;
            case R.id.action_open_source_notices:
                startActivity(new Intent(requireContext(), OssLicensesMenuActivity.class));
                break;
            case R.id.action_view_on_github:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Trumeet/MiPushTester")));
                } catch (ActivityNotFoundException ignored) {}
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }
}
