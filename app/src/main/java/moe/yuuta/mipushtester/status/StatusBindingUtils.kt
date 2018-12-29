package moe.yuuta.mipushtester.status

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.elvishew.xlog.XLog
import moe.yuuta.mipushtester.R

object StatusBindingUtils {
    @JvmStatic
    @BindingAdapter("textRegistered", "textUseMIUIPush", requireAll = true)
    fun setTextStatus (textView: TextView, registered: Boolean, useMIUIPush: Boolean) {
        XLog.d("setTextStatus() with " + registered + "," + useMIUIPush)
        textView.text = String.format(textView.context.getString(
                if (registered)
                    R.string.status_registered else
                    R.string.status_not_registered
        ),
                textView.context.getString(
                        if(useMIUIPush)
                            R.string.status_miui_push_detected else
                            R.string.status_miui_push_not_detected
                ))
        textView.setTextColor(ContextCompat.getColor(textView.context,
                if(registered)
                        R.color.material_green_600 else
                        R.color.material_gray_600))
    }

    @JvmStatic
    @BindingAdapter("imageStatus")
    fun setImageStatus (imageView: ImageView, registered: Boolean) {
        XLog.d("setImageStatus() with " + registered)
        imageView.setImageResource(if(registered)
                R.drawable.ic_check_circle_black_48dp else
                R.drawable.ic_error_black_48dp)
        imageView.setBackgroundColor(ContextCompat.getColor(imageView.context,
                if(registered)
                        R.color.material_green_600 else
                        R.color.material_gray_600))
    }
}
