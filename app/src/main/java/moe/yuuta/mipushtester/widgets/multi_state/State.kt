package moe.yuuta.mipushtester.widgets.multi_state

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

data class State (val showProgress: ObservableBoolean = ObservableBoolean(false),
                  val icon: ObservableField<Drawable> = ObservableField(),
                  val text: ObservableField<CharSequence> = ObservableField(),
                  val description: ObservableField<CharSequence> = ObservableField(),
                  val showTitle: ObservableBoolean = ObservableBoolean(true),
                  val showIcon: ObservableBoolean = ObservableBoolean(true),
                  val showDescription: ObservableBoolean = ObservableBoolean(true),
                  var onRetryListener: View.OnClickListener = object : View.OnClickListener {
                      override fun onClick(p0: View?) {
                          // Don't do anything
                          // I can't ensure that if I make this variable
                          // nullable, the databinding is fine.
                      }
                  },
                  val showRetry: ObservableBoolean = ObservableBoolean(false),
                  val contentDescription: ObservableField<CharSequence> = ObservableField()) {
    fun showProgress () {
        showProgress.set(true)
        showTitle.set(false)
        showDescription.set(false)
        showRetry.set(false)
        showIcon.set(false)
    }

    fun hideProgress () {
        showProgress.set(false)
        showTitle.set(true)
        showDescription.set(true)
        showRetry.set(true)
        showIcon.set(true)
    }

    fun hideAll () {
        showProgress.set(false)
        showTitle.set(false)
        showDescription.set(false)
        showRetry.set(false)
        showIcon.set(false)
    }
}
