package moe.yuuta.mipushtester.push;

import android.os.Bundle;
import android.view.MenuItem;

import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import moe.yuuta.mipushtester.R;
import moe.yuuta.mipushtester.databinding.ActivityMessageDetailBinding;

public class MessageDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiPushMessage message = (MiPushMessage) getIntent().getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
        ActivityMessageDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_message_detail);
        binding.setMessage(message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
