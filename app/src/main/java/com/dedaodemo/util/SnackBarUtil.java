package com.dedaodemo.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class SnackBarUtil {

    public static void showSimpleSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}
