package com.dedaodemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dedaodemo.R;

/**
 * Created by 01377578 on 2018/7/19.
 */

public class PlayFragment extends BottomSheetDialogFragment {

    public static final String TAG = "PlayFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), "test", Snackbar.LENGTH_SHORT).show();
                Log.i("PlayShowFragment", "show");
            }
        }, 1000);
        return view;
    }
}
