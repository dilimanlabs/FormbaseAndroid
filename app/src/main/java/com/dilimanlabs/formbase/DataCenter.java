package com.dilimanlabs.formbase;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dilimanlabs.formbase.model.Answers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 3/3/2015.
 */
public class DataCenter {
    public static boolean hasViews = false;
    public static Answers answers = null;
    public static  String TOKEN = "";
    public static String currentPath = "";
    public static List<Integer> repeaterIdList = new ArrayList<>();
    public static List<String> repeaterQuestionNameList = new ArrayList<>();
    public static Map<String, List> questionListMap = new HashMap<>();
    public static boolean isLogin = false;
    public static final String GLOBAL_URL = "http://192.168.0.7/";
    public static Map<String,EditText> editTextMap = new HashMap<>();
    public static Map<String, ToggleButton> toggleButtonMap = new HashMap<>();
    public static Map<Button, String> buttonMap = new HashMap<>();
    public static Map<String, CheckBox> checkBoxMap = new HashMap<>();
    public static Map<String, RadioGroup> radioGroupMap = new HashMap<>();
    public static Map<String, TextView> dateTextViewMap = new HashMap<>();
    public static LinearLayout questionsLayout = null;
    public static Answers answer = null;
    public static boolean saved = false;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
