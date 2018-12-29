package moe.yuuta.mipushtester.push

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageHelper
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.databinding.ActivityMessageDetailBinding

class MessageDetailActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message: MiPushMessage = intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE) as MiPushMessage
        val binding: ActivityMessageDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_detail)
        binding.message = message
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
}
