package com.dedaodemo.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by guoss on 2017/11/12.
 *
 */

public class MyBottomPlayBehavior extends CoordinatorLayout.Behavior<View>
{
    public MyBottomPlayBehavior(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,View child,View dependency)
    {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,View child,View dependency){
        float tranY=Math.abs(dependency.getTop());
        child.setTranslationY(tranY);
        return true;
    }
}
