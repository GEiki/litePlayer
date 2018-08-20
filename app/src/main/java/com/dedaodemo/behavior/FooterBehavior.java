package com.dedaodemo.behavior;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

/**
 * Created by 01377578 on 2018/7/26.
 */

public class FooterBehavior extends BottomSheetBehavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final String TAG = "FooterBehavior";

    private boolean isAnimationOn;
    private int preTop = 0;
    private boolean isShow = true;

    public FooterBehavior() {
        super();
    }

    public FooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        int top = Math.abs(dependency.getTop());
        if (isShow && child.getVisibility() == View.VISIBLE && !isAnimationOn && preTop - top < 0) {
            hide(child);
        } else if (!isShow && child.getVisibility() == View.INVISIBLE && !isAnimationOn && preTop - top > 0) {
            show(child);
        }
        preTop = top;
        return true;
    }


    private void hide(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(view.getHeight()).
                setInterpolator(INTERPOLATOR).setDuration(800);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimationOn = true;

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isShow = false;
                isAnimationOn = false;
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void show(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(0).
                setInterpolator(INTERPOLATOR).
                setDuration(800);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimationOn = true;
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isShow = true;
                isAnimationOn = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();


    }
}
