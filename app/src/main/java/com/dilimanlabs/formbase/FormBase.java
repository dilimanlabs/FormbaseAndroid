package com.dilimanlabs.formbase;

import android.app.Application;
import android.widget.LinearLayout;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by user on 3/5/2015.
 */
public class FormBase extends Application {
    public static LinearLayout globalQuestion;
    public  static String currentPath;
    public static boolean isSaved;
    public static boolean isCaptured;
    public static Deque<LinearLayout> viewDeque = new ArrayDeque<>();
    public static Deque<String> labelDeque = new ArrayDeque<>();
    private static FormBase formbaseSingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        formbaseSingleton = this;
    }

    public static FormBase getInstance() {
        return formbaseSingleton;
    }

}
