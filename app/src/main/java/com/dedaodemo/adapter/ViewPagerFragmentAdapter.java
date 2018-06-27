package com.dedaodemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoss on 2018/4/29.
 */

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    public ViewPagerFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments,ArrayList<String> strings) {
        super(fm);
        this.fragments = fragments;
        titles = strings;

    }

    @Override
    public Fragment getItem(int position) {
        Log.i("Fragment",String.valueOf(position));
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
