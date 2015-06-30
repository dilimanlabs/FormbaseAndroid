package com.dilimanlabs.formbase;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    public static boolean isQuestionGroup = false;
    public static Answers answers = null;
    public static  String TOKEN = "";
    public static String DOWNLOAD_ID = "";
    public static String ANSWER_URL = "";
    public static TextView latitude = null;
    public static TextView longitude = null;
    public static boolean isDraft = false;
    public static String currentPath = "";
    public static boolean isLogout = false;
    public static List<Integer> repeaterIdList = new ArrayList<>();
    public static List<String> repeaterQuestionNameList = new ArrayList<>();
    public static Map<String, List> questionListMap = new HashMap<>();
    public static boolean isLogin = false;
    public static final String GLOBAL_URL = "http://api.formbase.com.ph/";
    public static Map<String,EditText> editTextMap = new HashMap<>();
    public static Map<String, ToggleButton> toggleButtonMap = new HashMap<>();
    public static Map<Button, String> buttonMap = new HashMap<>();
    public static Map<String, Spinner> spinnerMap = new HashMap<>();
    public static Map<String, CheckBox> checkBoxMap = new HashMap<>();
    public static Map<String, RadioGroup> radioGroupMap = new HashMap<>();
    public static Map<String, TextView> dateTextViewMap = new HashMap<>();
    public static Map<String, TextView> imageTextViewMap = new HashMap<>();
    public static Map<String, TextView> getImageTextViewMapPrevious = new HashMap<>();
    public static List<TextView> imageTextViewList = new ArrayList<>();
    public static boolean isDatePickRepeater = false;
    public static String datePickRepeaterID = "";

    public static Map<String, EditText> repeaterBasicTextEditText = new HashMap<>();
    public static Map<String, ToggleButton> repeaterToggleButton = new HashMap<>();
    public static Map<String, Spinner> repeaterSpinner = new HashMap<>();
    public static Map<String, CheckBox> repeaterCheckBox = new HashMap<>();
    public static Map<String, RadioGroup> repeaterRadioGroup = new HashMap<>();
    public static Map<String, TextView> repeaterDateTextView = new HashMap<>();
    public static Map<String, EditText> repeaterNumberTextEditText = new HashMap<>();
    public static Map<String, TextView> repeaterImageTextViewMap = new HashMap<>();


    public static LinearLayout questionsLayout = null;
    public static final String STATUS = "FOR EMAIL";
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
    public static void clearAllStaticVariables(){
        hasViews = false;
        isQuestionGroup = false;
        answers = null;
        TOKEN = "";
        DOWNLOAD_ID = "";
        ANSWER_URL = "";
        latitude = null;
        longitude = null;
        isDraft = false;
        currentPath = "";
        isLogout = false;
        repeaterIdList.clear();
        repeaterQuestionNameList.clear();
        questionListMap.clear();
        isLogin = false;
        editTextMap.clear();
        toggleButtonMap.clear();
        spinnerMap.clear();
        checkBoxMap.clear();
        radioGroupMap.clear();
        dateTextViewMap.clear();
        imageTextViewMap.clear();
        getImageTextViewMapPrevious.clear();
        imageTextViewList.clear();
        isDatePickRepeater = false;
        datePickRepeaterID = "";
        repeaterBasicTextEditText.clear();
        repeaterToggleButton.clear();
        repeaterSpinner.clear();
        repeaterCheckBox.clear();
        repeaterRadioGroup.clear();
        repeaterDateTextView.clear();
        repeaterNumberTextEditText.clear();
        repeaterImageTextViewMap.clear();
        questionsLayout = null;
        answer = null;
        saved = false;
    }
}
