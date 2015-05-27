package com.dilimanlabs.formbase;

import android.app.Application;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.activeandroid.ActiveAndroid;
import com.dilimanlabs.formbase.model.AnswersForApproval;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static  List<AnswersForApproval> answersForApprovalListForViewing = new ArrayList<>();
    public static List<Button> buttonList = new ArrayList<>();
    public static Map<Button, String> buttonStringMap = new HashMap<>();
    public static String BIN = "";
    public static String SUBMISSION_BIN = "";
    public static Map<String, CardView> stringCardViewHashMap= new HashMap<>();
    public static String FORM;
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
