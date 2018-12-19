package moe.yuuta.mipushtester.status;

import android.widget.ImageView;
import android.widget.TextView;

import com.elvishew.xlog.XLog;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import moe.yuuta.mipushtester.R;

public class StatusBindingUtils {
    @BindingAdapter(value = {"textRegistered", "textUseMIUIPush"}, requireAll = true)
    public static void setTextStatus (TextView textView, boolean registered, boolean useMIUIPush) {
        XLog.d("setTextStatus() with " + registered + "," + useMIUIPush);
        textView.setText(String.format(textView.getContext().getString(
                registered ?
                        R.string.status_registered :
                        R.string.status_not_registered
                ),
                textView.getContext().getString(
                        useMIUIPush ?
                                R.string.status_miui_push_detected :
                                R.string.status_miui_push_not_detected
                )));
        textView.setTextColor(ContextCompat.getColor(textView.getContext(),
                registered ?
                        R.color.material_green_600 :
                        R.color.material_gray_600));
    }

    @BindingAdapter("imageStatus")
    public static void setImageStatus (ImageView imageView, boolean registered) {
        XLog.d("setImageStatus() with " + registered);
        imageView.setImageResource(registered ?
                R.drawable.ic_check_circle_black_48dp :
                R.drawable.ic_error_black_48dp);
        imageView.setBackgroundColor(ContextCompat.getColor(imageView.getContext(),
                registered ?
                        R.color.material_green_600 :
                        R.color.material_gray_600));
    }
}
