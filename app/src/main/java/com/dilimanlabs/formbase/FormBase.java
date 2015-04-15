package com.dilimanlabs.formbase;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by user on 3/5/2015.
 */
public class FormBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }


}
