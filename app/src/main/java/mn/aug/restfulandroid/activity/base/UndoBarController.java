package mn.aug.restfulandroid.activity.base;

/**
 * Created by Paul on 15/11/2014.
 */
/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.util.Logger;


public class UndoBarController {
    private final View actionBar;
    private View mBarView;
    private TextView mMessageView;
    private Handler mHideHandler = new Handler();

    private UndoListener mUndoListener;

    // State objects
    private Parcelable mUndoToken=null;
    private CharSequence mUndoMessage="";
    ValueAnimator animator;

    public interface UndoListener {
        void onUndo(Parcelable token);
        void undoDisabled(Parcelable token);
    }

    public UndoBarController(View actionBar, UndoListener undoListener) {
        this.actionBar = actionBar;
        mBarView = (RelativeLayout) actionBar.findViewById(R.id.undobar);

        mUndoListener = undoListener;

        mMessageView = (TextView) actionBar.findViewById(R.id.undobar_message);
        actionBar.findViewById(R.id.undobar_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUndoListener.onUndo(mUndoToken);
                        hideUndoBar(false);
                    }
                });
        hideUndoBar(true);
    }

    public void showUndoBar(boolean immediate, CharSequence message, Parcelable undoToken) {
        mUndoToken = undoToken;
        mUndoMessage = message;
        mMessageView.setText(mUndoMessage);

        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable,
                actionBar.getResources().getInteger(R.integer.undobar_hide_delay));

        //mBarView.setVisibility(View.VISIBLE);

        if (immediate) {
            actionBar.setTranslationY(0);
        } else {
            animator = ValueAnimator.ofInt(mBarView.getMeasuredHeight(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    actionBar.setTranslationY((Integer) valueAnimator.getAnimatedValue());
                }
            });
            animator.setDuration(150);
            animator.start();
        }
    }

    public void hideUndoBar(boolean immediate) {
        Logger.debug("mBarView.getMeasuredHeight","height:"+mBarView.getMeasuredHeight());
        Logger.debug("mBarView.getMeasuredHeight","height:"+mBarView.getHeight());
        mHideHandler.removeCallbacks(mHideRunnable);
        if (immediate) {
            //mBarView.setVisibility(View.GONE);
            actionBar.setTranslationY(150);
            mUndoMessage = null;

        } else {
            animator = ValueAnimator.ofInt(0, mBarView.getMeasuredHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    actionBar.setTranslationY((Integer) valueAnimator.getAnimatedValue());
                }
            });
            animator.setDuration(150);
            animator.start();
            mUndoMessage = null;

        }

    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("undo_message", mUndoMessage);
        outState.putParcelable("undo_token", mUndoToken);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUndoMessage = savedInstanceState.getCharSequence("undo_message");
            mUndoToken = savedInstanceState.getParcelable("undo_token");

            if (mUndoToken != null || !TextUtils.isEmpty(mUndoMessage)) {
                showUndoBar(true, mUndoMessage, mUndoToken);
            }
        }
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideUndoBar(false);
            mUndoListener.undoDisabled(mUndoToken);
        }
    };

    public void clearUndoToken(){
        mUndoToken=null;
    }
}
