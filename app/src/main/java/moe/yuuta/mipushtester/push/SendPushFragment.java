package moe.yuuta.mipushtester.push;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.Navigation;
import moe.shizuku.preference.EditTextPreference;
import moe.shizuku.preference.PreferenceFragment;
import moe.shizuku.preference.SimpleMenuPreference;
import moe.shizuku.preference.SwitchPreference;
import moe.yuuta.common.Constants;
import moe.yuuta.mipushtester.BuildConfig;
import moe.yuuta.mipushtester.R;
import moe.yuuta.mipushtester.api.APIManager;
import moe.yuuta.mipushtester.status.RegistrationStatus;
import retrofit2.Response;

public class SendPushFragment extends PreferenceFragment implements Callback {
    private final Logger logger = XLog.tag(SendPushFragment.class.getSimpleName()).build();

    private PushTask mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_send_push);
        updateUriLimitStatus(null);
        updatePassThroughLimitStatus(null);
        findPreference("click_type").setOnPreferenceChangeListener((preference, newValue) -> {
            updateUriLimitStatus(newValue);
            return true;
        });
        findPreference("pass_through").setOnPreferenceChangeListener((preference, newValue) -> {
            updatePassThroughLimitStatus(Boolean.parseBoolean(newValue.toString()));
            return true;
        });
        SimpleMenuPreference limitLocale = (SimpleMenuPreference) findPreference("limit_locale");
        limitLocale.setEntries(new CharSequence[]{
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, Locale.getDefault().getDisplayName()),
                getString(R.string.send_push_limit_cannot_template, Locale.getDefault().getDisplayName()),
        });
        limitLocale.setEntryValues(R.array.send_push_limit_ev_default);
        SimpleMenuPreference limitVersion = (SimpleMenuPreference) findPreference("limit_version");
        limitVersion.setEntries(new CharSequence[]{
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, BuildConfig.VERSION_NAME),
                getString(R.string.send_push_limit_cannot_template, BuildConfig.VERSION_NAME),
        });
        limitVersion.setEntryValues(R.array.send_push_limit_ev_default);
        SimpleMenuPreference limitModel = (SimpleMenuPreference) findPreference("limit_model");
        limitModel.setEntries(new CharSequence[]{
                getString(R.string.send_push_limit_none),
                getString(R.string.send_push_limit_only_template, Build.MODEL),
                getString(R.string.send_push_limit_cannot_template, Build.MODEL),
        });
        limitModel.setEntryValues(R.array.send_push_limit_ev_default);

        // According to the feedback, the SDK will detect region when registration.
        // If its result is China, it will not be able to receive messages which are sent via
        // global API. If not, it will not be able to receive messages which are sent via China API as well.
        // But probably it isn't associated with user's IP. Users in Republic of Singapore (Result: Singapore) will
        // return Singapore, but users in Canada will return China. Maybe because there are official MiPush servers in Republic of Singapore?
        //
        // NOTE 1: The result is associated with "configurations" in Push service (xmsf) as well. But I still not found any evidence
        // which proofs xmsf records user's region. (The mipush.xml in xmsf not contains region info).
        // NOTE 2: User may need to reset the tester and re-enable system-side push service to update this record.
        findPreference("global").setSummary(Html.fromHtml(getString(R.string.send_push_global_summary,
                "China".equals(RegistrationStatus.get(requireContext()).regRegion.get()) ?
                        getString(R.string.send_push_global_summary_may_not_be_able_to_receive_after_enabling) :
                        getString(R.string.send_push_global_summary_may_not_be_able_to_receive_after_disabling))));
    }

    private void updateUriLimitStatus(@Nullable Object newValue) {
        findPreference("click_url").setEnabled("1".equals(newValue == null ? ((SimpleMenuPreference) findPreference("click_type")).getValue() : newValue));
    }

    private void updatePassThroughLimitStatus(@Nullable Boolean passThrough) {
        findPreference("sound_uri").setEnabled(!(passThrough == null ? ((SwitchPreference) findPreference("pass_through")).isChecked() :
                passThrough));
        findPreference("pass_through_notification").setEnabled((passThrough == null ? ((SwitchPreference) findPreference("pass_through")).isChecked() :
                passThrough));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_send_push, menu);
        MenuItem item = menu.findItem(R.id.action_send);
        Drawable icon = item.getIcon();
        TypedArray typedArray = requireContext().obtainStyledAttributes(new int[]{ android.R.attr.textColorSecondary });
        int color = typedArray.getColor(0, Color.WHITE);
        typedArray.recycle();
        DrawableCompat.setTint(icon, color);
        item.setIcon(icon);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                handleSend();
                return true;
        }
        return false;
    }

    private void handleSend () {
        if (mTask != null && !mTask.isCancelled()) return;
        PushRequest request = new PushRequest();
        request.setRegistrationId(RegistrationStatus.get(requireContext()).regId.get());
        try {
            String delayStr = ((EditTextPreference) findPreference("delay")).getText();
            int delay = Integer.parseInt(delayStr);
            if (delay < 0 || delay > Constants.PUSH_DELAY_MS_MAX) throw new IllegalArgumentException();
            request.setDelayMs(delay * 1000);
        } catch (NullPointerException | IllegalArgumentException e) {
            Snackbar.make(getView(), R.string.error_delay_invalid, Snackbar.LENGTH_SHORT).show();
            return;
        }
        request.setDisplay(Integer.parseInt(((SimpleMenuPreference) findPreference("display")).getValue()));
        request.setPassThrough(((SwitchPreference) findPreference("pass_through")).isChecked());
        request.setEnforceWifi(((SwitchPreference) findPreference("enforce_wifi")).isChecked());
        request.setNotifyForeground(((SwitchPreference) findPreference("notify_foreground")).isChecked());
        String limitLocaleValue = ((SimpleMenuPreference) findPreference("limit_locale")).getValue();
        String limitVersionValue = ((SimpleMenuPreference) findPreference("limit_version")).getValue();
        String limitModelValue = ((SimpleMenuPreference) findPreference("limit_model")).getValue();
        switch (limitLocaleValue) {
            case "1":
                // Current ONLY
                request.setLocales(Collections.singletonList(Locale.getDefault().toString()));
                break;
            case "2":
                // Except current
                request.setLocalesExcept(Collections.singletonList(Locale.getDefault().toString()));
                break;
            case "0":
            default:
                break;
        }
        switch (limitVersionValue) {
            case "1":
                // Current ONLY
                request.setVersions(Collections.singletonList(BuildConfig.VERSION_NAME));
                break;
            case "2":
                // Except current
                request.setVersionsExcept(Collections.singletonList(BuildConfig.VERSION_NAME));
                break;
            case "0":
            default:
                break;
        }
        switch (limitModelValue) {
            case "1":
                // Current ONLY
                request.setModels(Collections.singletonList(Build.MODEL));
                break;
            case "2":
                // Except current
                request.setModelsExcept(Collections.singletonList(Build.MODEL));
                break;
            case "0":
            default:
                break;
        }
        switch (((SimpleMenuPreference) findPreference("click_type")).getValue()) {
            case "1":
                // Launch URL
                String url = ((EditTextPreference) findPreference("click_url")).getText();
                if (url == null || url.trim().equals("")) {
                    Snackbar.make(getView(), R.string.error_url_invalid, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                request.setClickAction(url);
                break;
            case "2":
                // Launch intent (detail page)
                Intent intent = new Intent(requireActivity(), MessageDetailActivity.class);
                String uriString = intent.toUri(Intent.URI_INTENT_SCHEME);
                request.setClickAction(uriString);
                break;
            case "0":
            default:
                // Default, skip
                break;
        }
        if (((SwitchPreference) findPreference("sound_uri")).isChecked()) {
            request.setSoundUri("android.resource://" + BuildConfig.APPLICATION_ID + "/raw/centaurus");
        }
        request.setGlobal(((SwitchPreference) findPreference("global")).isChecked());
        request.setPassThroughNotification(((SwitchPreference) findPreference("pass_through_notification")).isChecked());

        stopTask();
        mTask = new PushTask(this, request);
        mTask.execute();
    }

    @Override
    public void onPreExecute() {
        getPreferenceScreen().setEnabled(false);
        Snackbar.make(getView(), R.string.send_push_sending, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onPostExecute(Exception result) {
        getPreferenceScreen().setEnabled(true);
        stopTask();
        if (result == null) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content)
                    , R.string.send_push_sent, Snackbar.LENGTH_SHORT).show();
            Navigation.findNavController(requireActivity(), R.id.nav_host).navigateUp();
        } else {
            logger.e("Send push", result);
            Snackbar.make(getView(), R.string.send_push_fail, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.error_detail, v -> {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(R.string.send_push_fail)
                                .setMessage(Html.fromHtml("<tt>" +
                                        result.getMessage() +
                                                "</tt>"))
                                .show();
                    })
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        stopTask();
        super.onDestroyView();
    }

    private void stopTask () {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    private static class PushTask extends AsyncTask<Void, Void, Exception> {
        private final Callback callback;
        private final PushRequest request;

        public PushTask(Callback callback, PushRequest request) {
            this.callback = callback;
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.onPreExecute();
        }

        @Override
        protected void onPostExecute(Exception exception) {
            super.onPostExecute(exception);
            callback.onPostExecute(exception);
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                Response<JsonObject> response = APIManager.getInstance().push(request).execute();
                if (!response.isSuccessful()) {
                    return new Exception("Unsuccessful code " + response.code() + ", " +
                            "response: " + response.body());
                }
                return null;
            } catch (IOException e) {
                return e;
            }
        }
    }
}
