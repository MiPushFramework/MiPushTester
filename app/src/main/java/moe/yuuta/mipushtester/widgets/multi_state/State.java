package moe.yuuta.mipushtester.widgets.multi_state;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public class State {
    public final ObservableBoolean showProgress;
    public final ObservableField<Drawable> icon;
    public final ObservableField<CharSequence> text;
    public final ObservableField<CharSequence> description;
    public final ObservableBoolean showTitle;
    public final ObservableBoolean showIcon;
    public final ObservableBoolean showDescription;
    public View.OnClickListener onRetryListener;
    public final ObservableBoolean showRetry;
    public final ObservableField<CharSequence> contentDescription;

    public State() {
        showProgress = new ObservableBoolean(false);
        icon = new ObservableField<>();
        text = new ObservableField<>();
        description = new ObservableField<>();
        showRetry = new ObservableBoolean(false);
        showTitle = new ObservableBoolean(true);
        showDescription = new ObservableBoolean(true);
        contentDescription = new ObservableField<>();
        showIcon = new ObservableBoolean(true);
    }

    public void showProgress () {
        this.showProgress.set(true);
        this.showTitle.set(false);
        this.showDescription.set(false);
        this.showRetry.set(false);
        this.showIcon.set(false);
    }

    public void hideProgress () {
        this.showProgress.set(false);
        this.showTitle.set(true);
        this.showDescription.set(true);
        this.showRetry.set(true);
        this.showIcon.set(true);
    }

    public void hideAll () {
        this.showProgress.set(false);
        this.showTitle.set(false);
        this.showDescription.set(false);
        this.showRetry.set(false);
        this.showIcon.set(false);
    }
}
