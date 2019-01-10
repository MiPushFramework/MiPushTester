/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import moe.yuuta.mipushtester.R;

public class SwitchBar extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    public interface OnSwitchChangeListener {
        /**
         * Called when the checked state of the Switch has changed.
         *
         * @param switchView The Switch view whose state has changed.
         * @param isChecked  The new checked state of switchView.
         */
        void onSwitchChanged(Switch switchView, boolean isChecked);
    }

    private static final int[] XML_ATTRIBUTES = {
            R.attr.switchBarMarginStart,
            R.attr.switchBarMarginEnd,
            R.attr.switchBarBackgroundColor,
            R.attr.switchBarBackgroundActivatedColor};

    private final List<OnSwitchChangeListener> mSwitchChangeListeners = new ArrayList<>();
    private final TextAppearanceSpan mSummarySpan;

    private ToggleSwitch mSwitch;
    private TextView mTextView;
    private String mLabel;
    private String mSummary;
    @ColorInt
    private int mBackgroundColor;
    @ColorInt
    private int mBackgroundActivatedColor;
    @StringRes
    private int mOnTextId;
    @StringRes
    private int mOffTextId;

    public SwitchBar(Context context) {
        this(context, null);
    }

    public SwitchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(context).inflate(R.layout.switch_bar, this);

        final TypedArray a = context.obtainStyledAttributes(attrs, XML_ATTRIBUTES);
        int switchBarMarginStart = (int) a.getDimension(0, 0);
        int switchBarMarginEnd = (int) a.getDimension(1, 0);
        mBackgroundColor = a.getColor(2, 0);
        mBackgroundActivatedColor = a.getColor(3, 0);
        a.recycle();

        mTextView = findViewById(R.id.switch_text);
        mSummarySpan = new TextAppearanceSpan(getContext(), R.style.TextAppearance_Small_SwitchBar);
        ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mTextView.getLayoutParams();
        lp.setMarginStart(switchBarMarginStart);

        mSwitch = findViewById(R.id.switch_widget);
        // Prevent onSaveInstanceState() to be called as we are managing the state of the Switch
        // on our own
        mSwitch.setSaveEnabled(false);

        lp = (MarginLayoutParams) mSwitch.getLayoutParams();
        lp.setMarginEnd(switchBarMarginEnd);
        setBackgroundColor(mBackgroundColor);

        setSwitchBarText(R.string.switch_on_text, R.string.switch_off_text);

        addOnSwitchChangeListener(
                (switchView, isChecked) -> setTextViewLabelAndBackground(isChecked));

        // Default is hide
        setVisibility(View.GONE);
    }

    public void setTextViewLabelAndBackground(boolean isChecked) {
        mLabel = getResources().getString(isChecked ? mOnTextId : mOffTextId);
        setBackgroundColor(isChecked ? mBackgroundActivatedColor : mBackgroundColor);
        updateText();
    }

    public void setSwitchBarText(int onText, int offText) {
        mOnTextId = onText;
        mOffTextId = offText;
        setTextViewLabelAndBackground(isChecked());
    }

    public void setSummary(String summary) {
        mSummary = summary;
        updateText();
    }

    private void updateText() {
        if (TextUtils.isEmpty(mSummary)) {
            mTextView.setText(mLabel);
            return;
        }
        final SpannableStringBuilder ssb = new SpannableStringBuilder(mLabel).append('\n');
        final int start = ssb.length();
        ssb.append(mSummary);
        ssb.setSpan(mSummarySpan, start, ssb.length(), 0);
        mTextView.setText(ssb);
    }

    public void setChecked(boolean checked) {
        setTextViewLabelAndBackground(checked);
        mSwitch.setChecked(checked);
    }

    public void setCheckedInternal(boolean checked) {
        setTextViewLabelAndBackground(checked);
        mSwitch.setCheckedInternal(checked);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            setDisabledByAdmin();
            return;
        }
        super.setEnabled(enabled);
        mTextView.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
    }

    @VisibleForTesting
    View getDelegatingView() {
        return mSwitch;
    }

    /**
     * If admin is not null, disables the text and switch but keeps the view clickable.
     * Otherwise, calls setEnabled which will enables the entire view including
     * the text and switch.
     */
    public void setDisabledByAdmin() {
        mSwitch.setVisibility(View.VISIBLE);
        setEnabled(true);
        setTouchDelegate(new TouchDelegate(new Rect(0, 0, getWidth(), getHeight()),
                getDelegatingView()));
    }

    public final ToggleSwitch getSwitch() {
        return mSwitch;
    }

    public void show() {
        if (!isShowing()) {
            setVisibility(View.VISIBLE);
            mSwitch.setOnCheckedChangeListener(this);
            // Make the entire bar work as a switch
            post(() -> setTouchDelegate(
                    new TouchDelegate(new Rect(0, 0, getWidth(), getHeight()),
                            getDelegatingView())));
        }
    }

    public void hide() {
        if (isShowing()) {
            setVisibility(View.GONE);
            mSwitch.setOnCheckedChangeListener(null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if ((w > 0) && (h > 0)) {
            setTouchDelegate(new TouchDelegate(new Rect(0, 0, w, h),
                    getDelegatingView()));
        }
    }

    public boolean isShowing() {
        return (getVisibility() == View.VISIBLE);
    }

    public void propagateChecked(boolean isChecked) {
        final int count = mSwitchChangeListeners.size();
        for (int n = 0; n < count; n++) {
            mSwitchChangeListeners.get(n).onSwitchChanged(mSwitch, isChecked);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        propagateChecked(isChecked);
    }

    public void addOnSwitchChangeListener(OnSwitchChangeListener listener) {
        if (mSwitchChangeListeners.contains(listener)) {
            throw new IllegalStateException("Cannot add twice the same OnSwitchChangeListener");
        }
        mSwitchChangeListeners.add(listener);
    }

    public void removeOnSwitchChangeListener(OnSwitchChangeListener listener) {
        if (!mSwitchChangeListeners.contains(listener)) {
            throw new IllegalStateException("Cannot remove OnSwitchChangeListener");
        }
        mSwitchChangeListeners.remove(listener);
    }

    static class SavedState extends BaseSavedState {
        boolean checked;
        boolean visible;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
            visible = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
            out.writeValue(visible);
        }

        @Override
        public String toString() {
            return "SwitchBar.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked
                    + " visible=" + visible + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.checked = mSwitch.isChecked();
        ss.visible = isShowing();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        mSwitch.setCheckedInternal(ss.checked);
        setTextViewLabelAndBackground(ss.checked);
        setVisibility(ss.visible ? View.VISIBLE : View.GONE);
        mSwitch.setOnCheckedChangeListener(ss.visible ? this : null);

        requestLayout();
    }
}