package com.hill30.android.serviceTracker;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by mfeingol on 2/13/14.
 */
public class ActionBarTab implements ActionBar.TabListener {
    private Fragment fragment;
    private Context activity;
    private String title;
    private String fragmentClass;

    public ActionBarTab(ActionBarActivity activity, String title, String fragmentClass) {
        this.activity = activity;
        this.title = title;
        this.fragmentClass = fragmentClass;
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.addTab(
                actionBar.newTab()
                .setText(title)
                .setTabListener(this));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, fragmentClass);
            ft.add(android.R.id.content, fragment, title);
        } else
            ft.attach(fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment != null)
            ft.detach(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
