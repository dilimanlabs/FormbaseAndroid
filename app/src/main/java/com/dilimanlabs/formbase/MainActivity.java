package com.dilimanlabs.formbase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.activeandroid.ActiveAndroid;
import com.dilimanlabs.formbase.model.Category;
import com.dilimanlabs.formbase.model.Form;
import com.dilimanlabs.formbase.objects.Answers;
import com.dilimanlabs.formbase.objects.CategoryObject;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.Choices;
import com.dilimanlabs.formbase.objects.FormObject;
import com.dilimanlabs.formbase.objects.FormObjectWrapper;
import com.dilimanlabs.formbase.objects.JsonAnswer;
import com.dilimanlabs.formbase.objects.QuestionCheckList;
import com.dilimanlabs.formbase.objects.QuestionDateField;
import com.dilimanlabs.formbase.objects.QuestionGroup;
import com.dilimanlabs.formbase.objects.QuestionImageField;
import com.dilimanlabs.formbase.objects.QuestionMultipleChoice;
import com.dilimanlabs.formbase.objects.QuestionNumberField;
import com.dilimanlabs.formbase.objects.QuestionSwitch;
import com.dilimanlabs.formbase.objects.QuestionTextField;
import com.dilimanlabs.formbase.objects.Token;
import com.dilimanlabs.formbase.views.CategoryView;
import com.dilimanlabs.formbase.views.FormsView;
import com.dilimanlabs.formbase.views.InnerQuestionView;
import com.dilimanlabs.formbase.views.LoginView;
import com.dilimanlabs.formbase.views.QuestionBasicTextField;
import com.dilimanlabs.formbase.views.QuestionCheckListView;
import com.dilimanlabs.formbase.views.QuestionDateFieldView;
import com.dilimanlabs.formbase.views.QuestionGroupView;
import com.dilimanlabs.formbase.views.QuestionImageFieldView;
import com.dilimanlabs.formbase.views.QuestionMultipleChoiceView;
import com.dilimanlabs.formbase.views.QuestionNumberFieldView;
import com.dilimanlabs.formbase.views.QuestionSwitchView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    Gson gson;
    String categories;
    boolean categories_retrieved;
    File file;
    String jsonAnswer;
    Context context;
    String json;
    LoginView loginView;
    CategoryView categoryView;
    CategoryWrapper categoryWrapper;
    FormObjectWrapper formObjectWrapper;
    FormsView formsView;
    Button login;
    Deque<LinearLayout> viewDeque;
    String questionGroup = "Question group";
    ImageView back;
    LinearLayout current;
    LinearLayout previous;
    Toolbar toolbar;
    String imageField = "Image field";
    String switchQuestion = "Switch (True/False)";
    String numberField = "Number field";
    String multipleChoice = "Multiple Choice";
    String basicTextField = "Basic Text Field";
    String checkList = "Check List";
    String dateField = "Date field";
    String date = "";
    String dateId = "";
    boolean isDateSet = false;
    private int month, year, day;
    private Calendar calendar;
    Token token=null;
    List<Integer> previousID;
    QuestionGroupView questionGroupView;
    Map<String,EditText> editTextMap;
    Map<String, ToggleButton> toggleButtonMap;
    Map<Button, String> buttonMap;
    Map<String, CheckBox> checkBoxMap;
    Map<String, RadioGroup> radioGroupMap;
    Map<String, TextView> dateTextViewMap;
    Map<String, InnerQuestionView> innerQuestionViewMap;
    List<InnerQuestionView> innerQuestionViewsList;
    List childrenRepeat;
    private Handler handler;
    private com.dilimanlabs.formbase.objects.Form formWithAnswer;
    private static final int CAMERA_REQUEST = 1888;
    private EditText username;
    private EditText password;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        handler = new Handler();
        final LinearLayout questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        final LinearLayout innerQuestionsLayout = (LinearLayout) findViewById(R.id.innerQuestionsLayout);
        final LinearLayout original = (LinearLayout) findViewById(R.id.innerQuestionsCopyLayout);
        loginView = (LoginView) findViewById(R.id.loginView);
        categoryView = (CategoryView) findViewById(R.id.categoryView);
        login = (Button) loginView.findViewById(R.id.login);
        username = (EditText) loginView.findViewById(R.id.username);
        password = (EditText) loginView.findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Login().execute();
                int tries = 0;
                for (tries = 0; tries < 8; tries++) {
                    try {
                        Thread.sleep(500);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (token != null) {

                        Log.e("Login Successful", "");
                        Log.e("Retrieving Categories", "");
                        categories_retrieved = false;
                        new GetCategories().execute();
                        break;
                    }
                }
                for (tries = 0; tries < 5; tries++) {
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (!categories_retrieved) {
                        new GetForms().execute();
                        break;
                    }
                }
                if (categories_retrieved) {
                    Log.e("Categories Retrieval Failed", "");
                }
                loginView.setVisibility(View.GONE);
                LinearLayout layoutCategories = (LinearLayout) categoryView.findViewById(R.id.layoutCategories);
                List<Category> categories = Category.getAllCategories();
                for (final Category category : categories) {

                    Button button = new Button(MainActivity.this);
                    button.setText(category.getName());
                    buttonMap.put(button, category.getName());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            formsView = (FormsView) findViewById(R.id.formsView);
                            LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
                            Log.e("Category: ", Category.getCategoryName(buttonMap.get(v)).getUrl());
                            List<Form> form = Form.getAllFormsByCategory(Category.getCategoryName(buttonMap.get(v)).getUrl());
                            Log.e("Size: ", ""+Form.countTable());
                            linearLayoutForms.removeAllViews();
                            for (Form f : form) {
                                final Button button = new Button(MainActivity.this);
                                button.setText(f.getName());
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Form fo = Form.getFormByName(button.getText().toString());
                                        Log.e("Content: ", fo.getContent());
                                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                        createAllQuestions(form, questionsLayout, innerQuestionsLayout, original);
                                        formsView.setVisibility(View.GONE);
                                        viewDeque.addLast(formsView);
                                        questionsLayout.setVisibility(View.VISIBLE);
                                        current = questionsLayout;
                                    }
                                });
                                linearLayoutForms.addView(button);
                            }
                            categoryView.setVisibility(View.GONE);
                            viewDeque.addLast(categoryView);
                            back.setVisibility(View.VISIBLE);
                            formsView.setVisibility(View.VISIBLE);
                            current = formsView;
                        }
                    });
                    layoutCategories.addView(button);
                }
                categoryView.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onDestroy(){
        ActiveAndroid.clearCache();
    }


    public void init(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        back = (ImageView) toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backView(current, viewDeque.removeLast());
            }
        });
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        context = getApplicationContext();
        file = new File(context.getExternalCacheDir(),"testJson.json");
        previousID = new ArrayList<>();
        current = new LinearLayout(this);
        previous = new LinearLayout(this);
        editTextMap = new HashMap<>();
        toggleButtonMap = new HashMap<>();
        buttonMap = new HashMap<>();
        checkBoxMap = new HashMap<>();
        radioGroupMap = new HashMap<>();
        dateTextViewMap = new HashMap<>();
        innerQuestionViewMap = new HashMap<>();
        innerQuestionViewsList = new ArrayList<>();
        viewDeque = new ArrayDeque<>();
        gson = new Gson();
        json = loadJSONFromAsset();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
              photo = (Bitmap) data.getExtras().get("data");


//            imageView.setImageBitmap(photo);
        }
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("sample.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    public com.dilimanlabs.formbase.objects.Form createJSONFile(String json){
        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(json, com.dilimanlabs.formbase.objects.Form.class);
        formWithAnswer = new com.dilimanlabs.formbase.objects.Form();
        formWithAnswer.setFormName(form.getFormName());
        formWithAnswer.setDescription(form.getDescription());
        formWithAnswer.setType(form.getType());
        formWithAnswer.setLevel(form.getLevel());
        List<Object> objectList = new ArrayList<>();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){

                QuestionGroup questionGroup = new QuestionGroup();
                questionGroup.setType(map.get("type").toString());
                questionGroup.setTypeName(map.get("typeName").toString());
                List<Object> objectListInner = new ArrayList<>();

                List childList = (List)map.get("childList");
                for(int x = 0; x<childList.size(); x++){
                    final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                    if(child.get("typeName").equals("Question group")){
                        QuestionGroup questionGroupInner = new QuestionGroup();
                        questionGroupInner.setType(child.get("type").toString());
                        questionGroupInner.setTypeName(child.get("typeName").toString());
                        questionGroupInner.setName(child.get("name").toString());
                        List<Object> objectListChild = new ArrayList<>();
                        List childrenList = (List)child.get("childList");
                        childrenRepeat = (List)child.get("childList");
                            for (int a = 0; a < childrenList.size(); a++) {
                                    LinkedTreeMap<String, Object> children = (LinkedTreeMap<String, Object>) childrenList.get(a);
                                    if (children.get("typeName").equals(imageField)) {
                                        QuestionImageField questionImageField = new QuestionImageField();
                                        questionImageField.setType(children.get("type").toString());
                                        questionImageField.setTypeName(children.get("typeName").toString());
                                        questionImageField.setName(children.get("name").toString());
                                        Log.e("First", ""+children.get("level"));
                                        questionImageField.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionImageField.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionImageField.setElName(children.get("elName").toString());
                                        objectListChild.add(questionImageField);
                                    }
                                    else if(children.get("typeName").equals(dateField)){
                                        QuestionDateField questionDateField = new QuestionDateField();
                                        questionDateField.setTypeName(children.get("typeName").toString());
                                        questionDateField.setName(children.get("name").toString());
                                        questionDateField.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionDateField.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionDateField.setElName(children.get("elName").toString());
                                        questionDateField.setAnswer(dateTextViewMap.get(children.get("name").toString()).getText().toString());
                                        objectListChild.add(questionDateField);
                                    }
                                    else if (children.get("typeName").equals(switchQuestion)) {
                                        QuestionSwitch questionSwitch = new QuestionSwitch();
                                        questionSwitch.setType(children.get("type").toString());
                                        questionSwitch.setTypeName(children.get("typeName").toString());
                                        questionSwitch.setName(children.get("name").toString());
                                        questionSwitch.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionSwitch.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionSwitch.setElName(children.get("elName").toString());
                                        questionSwitch.setAnswer(toggleButtonMap.get(children.get("name").toString()).getText().toString());
                                        objectListChild.add(questionSwitch);

                                    } else if (children.get("typeName").equals(numberField)) {
                                        QuestionNumberField questionNumberField = new QuestionNumberField();
                                        questionNumberField.setType(children.get("type").toString());
                                        questionNumberField.setTypeName(children.get("typeName").toString());
                                        questionNumberField.setName(children.get("name").toString());
                                        questionNumberField.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionNumberField.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionNumberField.setElName(children.get("elName").toString());
                                        questionNumberField.setRanged(Boolean.parseBoolean(children.get("ranged").toString()));
                                        questionNumberField.setMinimum(Double.parseDouble(children.get("minimum").toString()));
                                        questionNumberField.setMaximum(Double.parseDouble(children.get("maximum").toString()));
                                        questionNumberField.setAnswer(editTextMap.get(children.get("name").toString()).getText().toString());
                                        objectListChild.add(questionNumberField);


                                    } else if (children.get("typeName").equals(multipleChoice)) {
                                        QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
                                        questionMultipleChoice.setType(children.get("type").toString());
                                        questionMultipleChoice.setTypeName(children.get("typeName").toString());
                                        questionMultipleChoice.setName(children.get("name").toString());
                                        questionMultipleChoice.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionMultipleChoice.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionMultipleChoice.setElName(children.get("elName").toString());
                                        List<Choices> choicesList = new ArrayList<>();
                                        List choices = (List) children.get("choices");
                                        for (int z = 0; z < choices.size(); z++) {
                                            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                                            Choices c = new Choices(choice.get("name").toString());
                                            choicesList.add(c);
                                        }
                                        questionMultipleChoice.setChoices(choicesList);
                                        int radioButtonID = radioGroupMap.get(children.get("name").toString()).getCheckedRadioButtonId();
                                        RadioButton radioButton = (RadioButton)radioGroupMap.get(children.get("name").toString()).findViewById(radioButtonID);
                                        questionMultipleChoice.setAnswer(radioButton.getText().toString());
                                        objectListChild.add(questionMultipleChoice);

                                    } else if (children.get("typeName").equals(basicTextField)) {
                                        QuestionTextField questionTextField = new QuestionTextField();
                                        questionTextField.setType(children.get("type").toString());
                                        questionTextField.setTypeName(children.get("typeName").toString());
                                        questionTextField.setName(children.get("name").toString());
                                        questionTextField.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionTextField.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionTextField.setElName(children.get("elName").toString());
                                        questionTextField.setAnswer(editTextMap.get(children.get("name").toString()).getText().toString());
                                        objectListChild.add(questionTextField);

                                    } else if (children.get("typeName").equals(checkList)) {
                                        QuestionCheckList questionCheckList = new QuestionCheckList();
                                        questionCheckList.setType(children.get("type").toString());
                                        questionCheckList.setTypeName(children.get("typeName").toString());
                                        questionCheckList.setName(children.get("name").toString());
                                        questionCheckList.setLevel(Double.parseDouble(children.get("level").toString()));
                                        questionCheckList.setOrder(Double.parseDouble(children.get("order").toString()));
                                        questionCheckList.setElName(children.get("elName").toString());
                                        List choices = (List) children.get("choices");
                                        List<Answers> answers = new ArrayList<>();
                                        List<Choices> choicesList = new ArrayList<>();
                                        for (int z = 0; z < choices.size(); z++) {
                                            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                                            Choices c = new Choices(choice.get("name").toString());
                                            choicesList.add(c);
                                            CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+children.get("name").toString());
                                            if(checkBox.isChecked()){
                                                answers.add(new Answers(checkBox.getText().toString()));
                                            }
                                        }

                                        questionCheckList.setChoices(choicesList);
                                        questionCheckList.setAnswer(answers);
                                        objectListChild.add(questionCheckList);

                                    }
                                }

                        questionGroupInner.setChildList(objectListChild);
                        questionGroupInner.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionGroupInner.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionGroupInner.setRepeating(Boolean.parseBoolean(child.get("isRepeating").toString()));
                        boolean isAdded = objectListInner.add(questionGroupInner);
                        Log.e("Added", ""+isAdded);



                    }
                    else if(child.get("typeName").equals(dateField)){
                        QuestionDateField questionDateField = new QuestionDateField();
                        questionDateField.setTypeName(child.get("typeName").toString());
                        questionDateField.setName(child.get("name").toString());
                        questionDateField.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionDateField.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionDateField.setElName(child.get("elName").toString());
                        questionDateField.setAnswer(dateTextViewMap.get(child.get("name").toString()).getText().toString());
                        objectList.add(questionDateField);
                    }
                    else if(child.get("typeName").equals(imageField)){
                        Log.e("Pumasok", "Nga Dito");
                        QuestionImageField questionImageField = new QuestionImageField();
                        questionImageField.setType(child.get("type").toString());
                        questionImageField.setTypeName(child.get("typeName").toString());
                        questionImageField.setName(child.get("name").toString());
                        Log.e("Number", "" + child.get("level"));
                        questionImageField.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionImageField.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionImageField.setElName(child.get("elName").toString());
                        objectListInner.add(questionImageField);
                    }
                    else if(child.get("typeName").equals(switchQuestion)){
                        QuestionSwitch questionSwitch = new QuestionSwitch();
                        questionSwitch.setType(child.get("type").toString());
                        questionSwitch.setTypeName(child.get("typeName").toString());
                        questionSwitch.setName(child.get("name").toString());
                        questionSwitch.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionSwitch.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionSwitch.setElName(child.get("elName").toString());
                        questionSwitch.setAnswer(toggleButtonMap.get(child.get("name").toString()).getText().toString());
                        objectListInner.add(questionSwitch);
                    }
                    else if(child.get("typeName").equals(numberField)){
                        QuestionNumberField questionNumberField = new QuestionNumberField();
                        questionNumberField.setType(child.get("type").toString());
                        questionNumberField.setTypeName(child.get("typeName").toString());
                        questionNumberField.setName(child.get("name").toString());
                        questionNumberField.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionNumberField.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionNumberField.setElName(child.get("elName").toString());
                        questionNumberField.setRanged(Boolean.parseBoolean(child.get("ranged").toString()));
                        questionNumberField.setMinimum(Double.parseDouble(child.get("minimum").toString()));
                        questionNumberField.setMaximum(Double.parseDouble(child.get("maximum").toString()));
                        questionNumberField.setAnswer(editTextMap.get(child.get("name").toString()).getText().toString());
                        objectListInner.add(questionNumberField);

                    }
                    else if(child.get("typeName").equals(multipleChoice)){
                        QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
                        questionMultipleChoice.setType(child.get("type").toString());
                        questionMultipleChoice.setTypeName(child.get("typeName").toString());
                        questionMultipleChoice.setName(child.get("name").toString());
                        questionMultipleChoice.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionMultipleChoice.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionMultipleChoice.setElName(child.get("elName").toString());
                        List<Choices> choicesList = new ArrayList<>();
                        List choices = (List) child.get("choices");
                        for (int z = 0; z < choices.size(); z++) {
                            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                            Choices c = new Choices(choice.get("name").toString());
                            choicesList.add(c);
                        }
                        questionMultipleChoice.setChoices(choicesList);
                        int radioButtonID = radioGroupMap.get(child.get("name").toString()).getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton)radioGroupMap.get(child.get("name").toString()).findViewById(radioButtonID);
                        questionMultipleChoice.setAnswer(radioButton.getText().toString());
                        objectListInner.add(questionMultipleChoice);
                    }
                    else if(child.get("typeName").equals(basicTextField)){
                        QuestionTextField questionTextField = new QuestionTextField();
                        questionTextField.setType(child.get("type").toString());
                        questionTextField.setTypeName(child.get("typeName").toString());
                        questionTextField.setName(child.get("name").toString());
                        questionTextField.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionTextField.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionTextField.setElName(child.get("elName").toString());
                        questionTextField.setAnswer(editTextMap.get(child.get("name").toString()).getText().toString());
                        objectListInner.add(questionTextField);
                    }
                    else if(child.get("typeName").equals(checkList)){
                        QuestionCheckList questionCheckList = new QuestionCheckList();
                        questionCheckList.setType(child.get("type").toString());
                        questionCheckList.setTypeName(child.get("typeName").toString());
                        questionCheckList.setName(child.get("name").toString());
                        questionCheckList.setLevel(Double.parseDouble(child.get("level").toString()));
                        questionCheckList.setOrder(Double.parseDouble(child.get("order").toString()));
                        questionCheckList.setElName(child.get("elName").toString());
                        List<Choices> choicesList = new ArrayList<>();
                        List choices = (List) child.get("choices");
                        List<Answers> answers = new ArrayList<>();
                        for (int z = 0; z < choices.size(); z++) {
                            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                            Choices c = new Choices(choice.get("name").toString());
                            choicesList.add(c);
                            CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+child.get("name").toString());
                            if(checkBox.isChecked()){
                                answers.add(new Answers(checkBox.getText().toString()));
                            }
                        }
                        questionCheckList.setChoices(choicesList);
                        questionCheckList.setAnswer(answers);
                        objectListInner.add(questionCheckList);


                    }


                }
                questionGroup.setChildList(objectListInner);
                questionGroup.setLevel(Double.parseDouble(map.get("level").toString()));
                questionGroup.setOrder(Double.parseDouble(map.get("order").toString()));
                questionGroup.setRepeating(Boolean.parseBoolean(map.get("isRepeating").toString()));
                objectList.add(questionGroup);
            }
            else if(map.get("typeName").equals(dateField)){
                QuestionDateField questionDateField = new QuestionDateField();
                questionDateField.setTypeName(map.get("typeName").toString());
                questionDateField.setName(map.get("name").toString());
                questionDateField.setLevel(Double.parseDouble(map.get("level").toString()));
                questionDateField.setOrder(Double.parseDouble(map.get("order").toString()));
                questionDateField.setElName(map.get("elName").toString());
                questionDateField.setAnswer(dateTextViewMap.get(map.get("name").toString()).getText().toString());
                objectList.add(questionDateField);

            }
            else if(map.get("typeName").equals(imageField)){
                QuestionImageField questionImageField = new QuestionImageField();
                questionImageField.setType(map.get("type").toString());
                questionImageField.setTypeName(map.get("typeName").toString());
                questionImageField.setName(map.get("name").toString());
                questionImageField.setLevel(Double.parseDouble(map.get("level").toString()));
                questionImageField.setOrder(Double.parseDouble(map.get("order").toString()));
                questionImageField.setElName(map.get("elName").toString());
                objectList.add(questionImageField);
            }
            else if(map.get("typeName").equals(switchQuestion)){
                QuestionSwitch questionSwitch = new QuestionSwitch();
                questionSwitch.setType(map.get("type").toString());
                questionSwitch.setTypeName(map.get("typeName").toString());
                questionSwitch.setName(map.get("name").toString());
                questionSwitch.setLevel(Double.parseDouble(map.get("level").toString()));
                questionSwitch.setOrder(Double.parseDouble(map.get("order").toString()));
                questionSwitch.setElName(map.get("elName").toString());
                questionSwitch.setAnswer(toggleButtonMap.get(map.get("name").toString()).getText().toString());
                objectList.add(questionSwitch);
            }
            else if(map.get("typeName").equals(numberField)){
                QuestionNumberField questionNumberField = new QuestionNumberField();
                questionNumberField.setType(map.get("type").toString());
                questionNumberField.setTypeName(map.get("typeName").toString());
                questionNumberField.setName(map.get("name").toString());
                questionNumberField.setLevel(Double.parseDouble(map.get("level").toString()));
                questionNumberField.setOrder(Double.parseDouble(map.get("order").toString()));
                questionNumberField.setElName(map.get("elName").toString());
                questionNumberField.setRanged(Boolean.parseBoolean(map.get("ranged").toString()));
                questionNumberField.setMinimum(Double.parseDouble(map.get("minimum").toString()));
                questionNumberField.setMaximum(Double.parseDouble(map.get("maximum").toString()));
                questionNumberField.setAnswer(editTextMap.get(map.get("name").toString()).getText().toString());
                objectList.add(questionNumberField);
            }
            else if(map.get("typeName").equals(multipleChoice)){

                QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
                questionMultipleChoice.setType(map.get("type").toString());
                questionMultipleChoice.setTypeName(map.get("typeName").toString());
                questionMultipleChoice.setName(map.get("name").toString());
                questionMultipleChoice.setLevel(Double.parseDouble(map.get("level").toString()));
                questionMultipleChoice.setOrder(Double.parseDouble(map.get("order").toString()));
                questionMultipleChoice.setElName(map.get("elName").toString());
                List<Choices> choicesList = new ArrayList<>();
                List choices = (List) map.get("choices");
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(choice.get("name").toString());
                    choicesList.add(c);
                }
                questionMultipleChoice.setChoices(choicesList);
                int radioButtonID = radioGroupMap.get(map.get("name").toString()).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroupMap.get(map.get("name").toString()).findViewById(radioButtonID);
                questionMultipleChoice.setAnswer(radioButton.getText().toString());
                objectList.add(questionMultipleChoice);

            }
            else if(map.get("typeName").equals(basicTextField)){
                QuestionTextField questionTextField = new QuestionTextField();
                questionTextField.setType(map.get("type").toString());
                questionTextField.setTypeName(map.get("typeName").toString());
                questionTextField.setName(map.get("name").toString());
                questionTextField.setLevel(Double.parseDouble(map.get("level").toString()));
                questionTextField.setOrder(Double.parseDouble(map.get("order").toString()));
                questionTextField.setElName(map.get("elName").toString());
                questionTextField.setAnswer(editTextMap.get(map.get("name").toString()).getText().toString());
                objectList.add(questionTextField);
            }
            else if(map.get("typeName").equals(checkList)){
                QuestionCheckList questionCheckList = new QuestionCheckList();
                questionCheckList.setType(map.get("type").toString());
                questionCheckList.setTypeName(map.get("typeName").toString());
                questionCheckList.setName(map.get("name").toString());
                questionCheckList.setLevel(Double.parseDouble(map.get("level").toString()));
                questionCheckList.setOrder(Double.parseDouble(map.get("order").toString()));
                questionCheckList.setElName(map.get("elName").toString());
                List<Choices> choicesList = new ArrayList<>();
                List choices = (List) map.get("choices");
                List<Answers> answers = new ArrayList<>();
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(choice.get("name").toString());
                    choicesList.add(c);
                    CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+map.get("name").toString());
                    if(checkBox.isChecked()){
                        answers.add(new Answers(checkBox.getText().toString()));
                    }
                }
                questionCheckList.setChoices(choicesList);
                questionCheckList.setAnswer(answers);
                objectList.add(questionCheckList);

            }
        }
        if(previousID.size() != 0){
            List<Object> repeatedGroup = new ArrayList<>();
            for(int a = 0; a<previousID.size(); a++ ){
                repeatedGroup.add(insertRepeaterAnswers(childrenRepeat, previousID.get(a).toString()));
            }
            formWithAnswer.setQuestionGroupRepeaterList(repeatedGroup);
            Log.e("Size", ""+repeatedGroup.size());
        }
        formWithAnswer.setChildList(objectList);
        return formWithAnswer;
    }

    @SuppressWarnings("deprecation")
    public void createQuestionGroup(List childrenList, final LinearLayout innerQuestionsLayout, final LinearLayout questionsLayout, final QuestionGroupView questionGroupView){
        final InnerQuestionView innerQuestionView = new InnerQuestionView(this);
        innerQuestionView.setId(questionGroupView.getId());
        LinearLayout inner = (LinearLayout) innerQuestionView.findViewById(R.id.innerQuestion);
        for (int a = 0; a < childrenList.size(); a++) {
            LinkedTreeMap<String, Object> children = (LinkedTreeMap<String, Object>) childrenList.get(a);
            if (children.get("typeName").equals(imageField)) {
                QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(MainActivity.this);
                TextView type = (TextView) questionImageFieldView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionImageFieldView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                inner.addView(questionImageFieldView);
                questionImageFieldView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            }
            else if(children.get("typeName").equals(dateField)){
                QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
                TextView type = (TextView)questionDateField.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
                dateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateId = String.valueOf(questionGroupView.getId());
                        showDialog(999);
                    }
                });

                dateTextViewMap.put(String.valueOf(questionGroupView.getId()), dateTextView);
                inner.addView(questionDateField);
            }
            else if (children.get("typeName").equals(switchQuestion)) {
                QuestionSwitchView questionSwitchView = new QuestionSwitchView(MainActivity.this);
                TextView type = (TextView) questionSwitchView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionSwitchView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout choices = (LinearLayout) questionSwitchView.findViewById(R.id.questionChoices);
                ToggleButton toggleButton = new ToggleButton(MainActivity.this);
                toggleButtonMap.put(String.valueOf(questionGroupView.getId()), toggleButton);
                toggleButton.setTextOn("True");
                toggleButton.setTextOff("False");
                choices.addView(toggleButton);
                inner.addView(questionSwitchView);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            } else if (children.get("typeName").equals(numberField)) {
                QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(MainActivity.this);
                TextView type = (TextView) questionNumberFieldView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionNumberFieldView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
                EditText answer = new EditText(MainActivity.this);
                answer.setInputType(InputType.TYPE_CLASS_NUMBER);
                answer.setLines(1);
                questionAnswer.addView(answer);
                questionAnswer.setVisibility(View.VISIBLE);
                editTextMap.put(String.valueOf(questionGroupView.getId()), answer);
                inner.addView(questionNumberFieldView);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            } else if (children.get("typeName").equals(multipleChoice)) {
                QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(MainActivity.this);
                TextView type = (TextView) questionMultipleChoiceView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                RadioGroup radioGroup = new RadioGroup(MainActivity.this);
                radioGroupMap.put(String.valueOf(questionGroupView.getId()), radioGroup);
                LinearLayout choicesButton = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
                List choices = (List) children.get("choices");
                for (int z = 0; z < choices.size(); z++) {
                    RadioButton radioButton = new RadioButton(MainActivity.this);
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    radioButton.setText(choice.get("name").toString());
                    radioGroup.addView(radioButton);
                    Log.e("CheckList", (z + 1) + " " + choice.get("name"));
                }
                choicesButton.addView(radioGroup);
                inner.addView(questionMultipleChoiceView);
            } else if (children.get("typeName").equals(basicTextField)) {
                QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(MainActivity.this);
                TextView type = (TextView) questionBasicTextField.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionBasicTextField.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
                EditText answer = new EditText(MainActivity.this);
                answer.setLines(1);
                questionAnswer.addView(answer);
                questionAnswer.setVisibility(View.VISIBLE);
                editTextMap.put(String.valueOf(questionGroupView.getId()), answer);
                inner.addView(questionBasicTextField);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            } else if (children.get("typeName").equals(checkList)) {
                QuestionCheckListView questionCheckListView = new QuestionCheckListView(MainActivity.this);
                TextView type = (TextView) questionCheckListView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionCheckListView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout choicesLayout = (LinearLayout) questionCheckListView.findViewById(R.id.questionChoices);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
                List choices = (List) children.get("choices");

                for (int z = 0; z < choices.size(); z++) {
                    CheckBox checkBox = new CheckBox(MainActivity.this);
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    checkBoxMap.put(choice.get("name").toString()+String.valueOf(questionGroupView.getId()), checkBox);
                    Log.e("CheckList", (z + 1) + " " + choice.get("name"));
                    checkBox.setText(choice.get("name").toString());
                    choicesLayout.addView(checkBox);
                }
                inner.addView(questionCheckListView);
            }
        }

        Button submit = new Button(MainActivity.this);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerQuestionsLayout.setVisibility(View.GONE);
                questionsLayout.setVisibility(View.VISIBLE);
            }
        });
        inner.addView(submit);
        innerQuestionViewsList.add(innerQuestionView);
        Log.e("ID added in map: ", ""+questionGroupView.getId());
        innerQuestionViewMap.put(String.valueOf(questionGroupView.getId()), innerQuestionView);

    }



    public QuestionGroupView createQuestionGroupView(final LinkedTreeMap<String, Object> child, final LinearLayout innerQuestionsLayout, final LinearLayout questionsLayout){
        final QuestionGroupView questionGroupChild = new QuestionGroupView(this);
        int ID = DataCenter.generateViewId();
        Log.e("ID assigned: ",""+ID);
        questionGroupChild.setId(ID);
        Log.e("ID Created: ",""+questionGroupChild.getId());
        TextView type = (TextView)questionGroupChild.findViewById(R.id.questionType);
        type.setText(child.get("typeName").toString());
        TextView name = (TextView)questionGroupChild.findViewById(R.id.questionName);
        name.setText(child.get("name").toString());
        questionGroupChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List childrenList = (List)child.get("childList");
                createQuestionGroup(childrenList, innerQuestionsLayout, questionsLayout, questionGroupChild);
                innerQuestionsLayout.setVisibility(View.VISIBLE);
                questionsLayout.setVisibility(View.GONE);
            }
        });
        return questionGroupChild;
    }

    public QuestionGroupView createQuestionGroupViewTop(final LinkedTreeMap<String, Object> child, final LinearLayout innerQuestionsLayout, final LinearLayout questionsLayout){
        final QuestionGroupView questionGroupChild = new QuestionGroupView(this);
        TextView type = (TextView)questionGroupChild.findViewById(R.id.questionType);
        type.setText(child.get("typeName").toString());
        TextView name = (TextView)questionGroupChild.findViewById(R.id.questionName);
        name.setText(child.get("name").toString());
        List childrenList = (List)child.get("childList");
        createQuestionGroupTop(childrenList, innerQuestionsLayout, questionsLayout);
        questionGroupChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InnerQuestionView innerQuestionView = (InnerQuestionView)innerQuestionsLayout.getChildAt(0);
                LinearLayout linearLayout = (LinearLayout)innerQuestionView.findViewById(R.id.innerQuestion);
                linearLayout.setVisibility(View.VISIBLE);
                innerQuestionView.setVisibility(View.VISIBLE);
                innerQuestionsLayout.setVisibility(View.VISIBLE);
                questionsLayout.setVisibility(View.GONE);
            }
        });
        return questionGroupChild;
    }

    public void createQuestionGroupTop(List childrenList, final LinearLayout innerQuestionsLayout, final LinearLayout questionsLayout){
        InnerQuestionView innerQuestionView = new InnerQuestionView(this);
        LinearLayout inner = (LinearLayout) innerQuestionView.findViewById(R.id.innerQuestion);
        for (int a = 0; a < childrenList.size(); a++) {
            final LinkedTreeMap<String, Object> children = (LinkedTreeMap<String, Object>) childrenList.get(a);
            if (children.get("typeName").equals(imageField)) {
                QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(MainActivity.this);
                TextView type = (TextView) questionImageFieldView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionImageFieldView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                inner.addView(questionImageFieldView);
                questionImageFieldView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            }
            else if(children.get("typeName").equals(dateField)){
                QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
                TextView type = (TextView)questionDateField.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
                dateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateId = children.get("name").toString();
                        showDialog(999);
                    }
                });
                dateTextViewMap.put(children.get("name").toString(), dateTextView);
                inner.addView(questionDateField);
            }
            else if (children.get("typeName").equals(switchQuestion)) {
                QuestionSwitchView questionSwitchView = new QuestionSwitchView(MainActivity.this);
                TextView type = (TextView) questionSwitchView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionSwitchView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout choices = (LinearLayout) questionSwitchView.findViewById(R.id.questionChoices);
                ToggleButton toggleButton = new ToggleButton(MainActivity.this);
                toggleButtonMap.put(children.get("name").toString(), toggleButton);
                toggleButton.setTextOn("True");
                toggleButton.setTextOff("False");
                choices.addView(toggleButton);
                inner.addView(questionSwitchView);
            } else if (children.get("typeName").equals(numberField)) {
                QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(MainActivity.this);
                TextView type = (TextView) questionNumberFieldView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionNumberFieldView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
                EditText answer = new EditText(MainActivity.this);
                answer.setInputType(InputType.TYPE_CLASS_NUMBER);
                answer.setLines(1);
                questionAnswer.addView(answer);
                questionAnswer.setVisibility(View.VISIBLE);
                editTextMap.put(children.get("name").toString(), answer);
                inner.addView(questionNumberFieldView);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            } else if (children.get("typeName").equals(multipleChoice)) {
                QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(MainActivity.this);
                TextView type = (TextView) questionMultipleChoiceView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                RadioGroup radioGroup = new RadioGroup(MainActivity.this);
                radioGroupMap.put(children.get("name").toString(), radioGroup);
                LinearLayout choicesButton = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
                List choices = (List) children.get("choices");
                for (int z = 0; z < choices.size(); z++) {
                    RadioButton radioButton = new RadioButton(MainActivity.this);
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    radioButton.setText(choice.get("name").toString());
                    radioGroup.addView(radioButton);
                    Log.e("CheckList", (z + 1) + " " + choice.get("name"));
                }
                choicesButton.addView(radioGroup);
                inner.addView(questionMultipleChoiceView);
            } else if (children.get("typeName").equals(basicTextField)) {
                QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(MainActivity.this);
                TextView type = (TextView) questionBasicTextField.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionBasicTextField.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
                EditText answer = new EditText(MainActivity.this);
                answer.setLines(1);
                questionAnswer.addView(answer);
                questionAnswer.setVisibility(View.VISIBLE);
                editTextMap.put(children.get("name").toString(), answer);
                inner.addView(questionBasicTextField);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
            } else if (children.get("typeName").equals(checkList)) {
                QuestionCheckListView questionCheckListView = new QuestionCheckListView(MainActivity.this);
                TextView type = (TextView) questionCheckListView.findViewById(R.id.questionType);
                type.setText(children.get("typeName").toString());
                TextView name = (TextView) questionCheckListView.findViewById(R.id.questionName);
                name.setText(children.get("name").toString());
                LinearLayout choicesLayout = (LinearLayout) questionCheckListView.findViewById(R.id.questionChoices);
                Log.e("ChildrenList", (a + 1) + " " + children.get("typeName"));
                Log.e("ChildrenList Name", " " + children.get("name"));
                List choices = (List) children.get("choices");

                for (int z = 0; z < choices.size(); z++) {
                    CheckBox checkBox = new CheckBox(MainActivity.this);
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    checkBoxMap.put(choice.get("name").toString()+children.get("name").toString(), checkBox);
                    Log.e("CheckList", (z + 1) + " " + choice.get("name"));
                    checkBox.setText(choice.get("name").toString());
                    choicesLayout.addView(checkBox);
                }
                inner.addView(questionCheckListView);
            }
        }

        Button submit = new Button(MainActivity.this);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCenter.hasViews = true;
                innerQuestionsLayout.setVisibility(View.GONE);
                questionsLayout.setVisibility(View.VISIBLE);
            }
        });
        inner.addView(submit);

            innerQuestionsLayout.addView(innerQuestionView);



    }

    public QuestionSwitchView createQuestionSwitchView(String typeName, String questionName){
        QuestionSwitchView questionSwitchView = new QuestionSwitchView(this);
        TextView type = (TextView)questionSwitchView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionSwitchView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choices = (LinearLayout)questionSwitchView.findViewById(R.id.questionChoices);
        ToggleButton toggleButton = new ToggleButton(this);
        toggleButtonMap.put(questionName, toggleButton);
        toggleButton.setTextOn("True");
        toggleButton.setTextOff("False");
        choices.addView(toggleButton);
        return questionSwitchView;
    }

    @SuppressWarnings("deprecation")
    public QuestionDateFieldView createQuestionDateField(String typeName, final String questionName){
        QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
        TextView type = (TextView)questionDateField.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
        name.setText(questionName);
        final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateId = questionName;
                showDialog(999);
            }
        });
        dateTextViewMap.put(questionName, dateTextView);
        Log.e("Content", ""+questionName);
        return questionDateField;
    }

    public QuestionNumberFieldView createQuestionNumberFieldView(String typeName, String questionName){
        QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(this);
        TextView type = (TextView)questionNumberFieldView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionNumberFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        answer.setInputType(InputType.TYPE_CLASS_NUMBER);
        answer.setLines(1);
        questionAnswer.addView(answer);
        questionAnswer.setVisibility(View.VISIBLE);
        editTextMap.put(questionName, answer);
        return questionNumberFieldView;
    }

    public QuestionBasicTextField createQuestionBasicTextField(String typeName, String questionName){
        QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(this);
        TextView type = (TextView)questionBasicTextField.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionBasicTextField.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        questionAnswer.addView(answer);
        answer.setLines(1);
        questionAnswer.setVisibility(View.VISIBLE);
        editTextMap.put(questionName, answer);
        return questionBasicTextField;
    }

    public QuestionImageFieldView createQuestionImageFieldView(String typeName, String questionName){
        QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(this);
        TextView type = (TextView)questionImageFieldView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionImageFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        questionImageFieldView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        return questionImageFieldView;
    }

    public QuestionMultipleChoiceView createQuestionMultipleChoiceView(String typeName, String questionName, List choices){
        QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(this);
        TextView type = (TextView) questionMultipleChoiceView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choicesLayout = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroupMap.put(questionName, radioGroup);
        for(int x = 0; x<choices.size(); x++){
            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(x);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(choice.get("name").toString());
            radioGroup.addView(radioButton);
        }
        choicesLayout.addView(radioGroup);
        return questionMultipleChoiceView;
    }

    public QuestionCheckListView createQuestionCheckListView(String typeName, String questionName, List choices){
        QuestionCheckListView questionCheckListView = new QuestionCheckListView(this);
        TextView type = (TextView) questionCheckListView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView) questionCheckListView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionChoices = (LinearLayout)questionCheckListView.findViewById(R.id.questionChoices);
        for(int x = 0; x<choices.size(); x++){
            CheckBox choiceCheckBox = new CheckBox(this);
            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(x);
            Log.e("CheckList", (x+1)+" "+choice.get("name"));
            checkBoxMap.put(choice.get("name").toString()+questionName, choiceCheckBox);
            choiceCheckBox.setText(choice.get("name").toString());
            questionChoices.addView(choiceCheckBox);
        }
        return questionCheckListView;
    }

    public void createAllQuestions(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original){
        questionsLayout.removeAllViews();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                ImageButton add = (ImageButton)questionGroupView.findViewById(R.id.add_new);
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    add.setVisibility(View.VISIBLE);
                }
                TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                questions.setVisibility(View.VISIBLE);
                questionName.setText(map.get("name").toString());
                Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                Log.e("Name: ", " "+map.get("name"));
                List childList = (List)map.get("childList");
                for(int x = 0; x<childList.size(); x++){
                    final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                    if(child.get("typeName").equals(questionGroup)){
                        final QuestionGroupView questionGroupChild = createQuestionGroupViewTop(child, original, questionsLayout);
                        ImageButton addInner = (ImageButton)questionGroupChild.findViewById(R.id.add_new);
                        if(Boolean.parseBoolean(child.get("isRepeating").toString()) == true){
                            addInner.setVisibility(View.VISIBLE);
                        }
                        questions.addView(questionGroupChild);
                        addInner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final QuestionGroupView newQuestionGroupView = createQuestionGroupView(child, innerQuestionsLayout, questionsLayout);
                                List childrenList = (List)child.get("childList");
                                if(!previousID.contains(v.getId())){
                                    Log.e("Prev size is: ", ""+previousID.size() );
                                    createQuestionGroup(childrenList,innerQuestionsLayout, questionsLayout, newQuestionGroupView);
                                    innerQuestionsLayout.addView(innerQuestionViewMap.get(String.valueOf(newQuestionGroupView.getId())));
                                    previousID.add(newQuestionGroupView.getId());
                                    Log.e("Prev size is: ", ""+previousID.size() );
                                    for(int i=0; i<previousID.size(); i++){
                                        Log.e("Prev sample: ", ""+previousID.get(i));
                                    }
                                }
                                newQuestionGroupView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        for(int i=0; i<innerQuestionsLayout.getChildCount(); i++){
                                            if(innerQuestionsLayout.getChildAt(i).getId() == v.getId() ){
                                                Log.e("ID's :",""+ innerQuestionsLayout.getChildAt(i).getId());
                                                InnerQuestionView innerQuestionView = (InnerQuestionView)innerQuestionsLayout.getChildAt(i);
                                                LinearLayout linearLayout = (LinearLayout) innerQuestionView.findViewById(R.id.innerQuestion);
                                                linearLayout.setVisibility(View.VISIBLE);
                                                innerQuestionView.setVisibility(View.VISIBLE);
                                            }
                                            else{
                                                InnerQuestionView innerQuestionView = (InnerQuestionView)innerQuestionsLayout.getChildAt(i);
                                                LinearLayout linearLayout = (LinearLayout) innerQuestionView.findViewById(R.id.innerQuestion);
                                                linearLayout.setVisibility(View.GONE);
                                                innerQuestionView.setVisibility(View.GONE);
                                            }
                                        }
                                        innerQuestionsLayout.setVisibility(View.VISIBLE);
                                        questionsLayout.setVisibility(View.GONE);
                                    }
                                });
                                ImageButton removeInner = (ImageButton)newQuestionGroupView.findViewById(R.id.remove_new);
                                removeInner.setVisibility(View.VISIBLE);
                                questions.addView(newQuestionGroupView);
                                removeInner.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        questions.removeView(newQuestionGroupView);
                                        innerQuestionsLayout.removeView(innerQuestionViewMap.get(String.valueOf(newQuestionGroupView.getId())));
                                        editTextMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                        toggleButtonMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                        checkBoxMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                        radioGroupMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                        dateTextViewMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                        Log.e("Index", ""+String.valueOf(newQuestionGroupView.getId()));
                                        previousID.remove(Integer.valueOf(newQuestionGroupView.getId()));

                                        Log.e("Prev size is: ", ""+previousID.size() );
                                        for(int i=0; i<previousID.size(); i++){
                                            Log.e("Prev size is: ", ""+previousID.get(i));
                                        }
                                        Log.e("Removed", "ID: "+String.valueOf(newQuestionGroupView.getId()));
                                    }
                                });
                            }
                        });

                    }
                    else if(child.get("typeName").equals(dateField)){
                        questions.addView(createQuestionDateField(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(imageField)){
                        questions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(switchQuestion)){
                        questions.addView(createQuestionSwitchView(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(numberField)){
                        questions.addView(createQuestionNumberFieldView(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(multipleChoice)){
                        List choices = (List)child.get("choices");
                        questions.addView(createQuestionMultipleChoiceView(child.get("typeName").toString(), child.get("name").toString(), choices));
                    }
                    else if(child.get("typeName").equals(basicTextField)){
                        questions.addView(createQuestionBasicTextField(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(checkList)){
                        List choices = (List)child.get("choices");
                        questions.addView(createQuestionCheckListView(child.get("typeName").toString(), child.get("name").toString(), choices));
                    }


                }
                questionsLayout.addView(questionGroupView);
            }
            else if(map.get("typeName").equals(dateField)){
                questionsLayout.addView(createQuestionDateField(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(imageField)){
                questionsLayout.addView(createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(switchQuestion)){
                questionsLayout.addView(createQuestionSwitchView(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(numberField)){
                questionsLayout.addView(createQuestionNumberFieldView(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(multipleChoice)){
                List choices = (List)map.get("choices");
                questionsLayout.addView(createQuestionMultipleChoiceView(map.get("typeName").toString(), map.get("name").toString(), choices));
            }
            else if(map.get("typeName").equals(basicTextField)){
                questionsLayout.addView(createQuestionBasicTextField(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(checkList)){
                List choices = (List)map.get("choices");
                questionsLayout.addView(createQuestionCheckListView(map.get("typeName").toString(), map.get("name").toString(), choices));

            }
        }
        Button submit = new Button(this);
        submit.setText("Submit All");
        questionsLayout.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isAllFieldsValid(editTextMap)){
                    com.dilimanlabs.formbase.objects.Form newForm = createJSONFile(json);
                    jsonAnswer = gson.toJson(newForm);

                    try {
                        FileWriter writer = new FileWriter(file);
                        writer.write(jsonAnswer);
                        writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.e("Form", gson.toJson(newForm));
                    new GetToken().execute("");
                }





            }
        });
    }

    public List<Object> insertRepeaterAnswers(List childrenList, String id ){
        List<Object> questionGroupRepeaterList = new ArrayList<>();
        for (int a = 0; a < childrenList.size(); a++) {
            LinkedTreeMap<String, Object> children = (LinkedTreeMap<String, Object>) childrenList.get(a);
            if (children.get("typeName").equals(imageField)) {
                QuestionImageField questionImageField = new QuestionImageField();
                questionImageField.setType(children.get("type").toString());
                questionImageField.setTypeName(children.get("typeName").toString());
                questionImageField.setName(children.get("name").toString());
                Log.e("First", "" + children.get("level"));
                questionImageField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionImageField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionImageField.setElName(children.get("elName").toString());
                questionGroupRepeaterList.add(questionImageField);
            }
            else if(children.get("typeName").equals(dateField)){
                QuestionDateField questionDateField = new QuestionDateField();
                questionDateField.setTypeName(children.get("typeName").toString());
                questionDateField.setName(children.get("name").toString());
                questionDateField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionDateField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionDateField.setElName(children.get("elName").toString());
                questionDateField.setAnswer(dateTextViewMap.get(id).getText().toString());
                questionGroupRepeaterList.add(questionDateField);
            }
            else if (children.get("typeName").equals(switchQuestion)) {
                QuestionSwitch questionSwitch = new QuestionSwitch();
                questionSwitch.setType(children.get("type").toString());
                questionSwitch.setTypeName(children.get("typeName").toString());
                questionSwitch.setName(children.get("name").toString());
                questionSwitch.setLevel(Double.parseDouble(children.get("level").toString()));
                questionSwitch.setOrder(Double.parseDouble(children.get("order").toString()));
                questionSwitch.setElName(children.get("elName").toString());
                questionSwitch.setAnswer(toggleButtonMap.get(id).getText().toString());
                questionGroupRepeaterList.add(questionSwitch);

            } else if (children.get("typeName").equals(numberField)) {
                QuestionNumberField questionNumberField = new QuestionNumberField();
                questionNumberField.setType(children.get("type").toString());
                questionNumberField.setTypeName(children.get("typeName").toString());
                questionNumberField.setName(children.get("name").toString());
                questionNumberField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionNumberField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionNumberField.setElName(children.get("elName").toString());
                questionNumberField.setRanged(Boolean.parseBoolean(children.get("ranged").toString()));
                questionNumberField.setMinimum(Double.parseDouble(children.get("minimum").toString()));
                questionNumberField.setMaximum(Double.parseDouble(children.get("maximum").toString()));
                questionNumberField.setAnswer(editTextMap.get(id).getText().toString());
                questionGroupRepeaterList.add(questionNumberField);


            } else if (children.get("typeName").equals(multipleChoice)) {
                QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
                questionMultipleChoice.setType(children.get("type").toString());
                questionMultipleChoice.setTypeName(children.get("typeName").toString());
                questionMultipleChoice.setName(children.get("name").toString());
                questionMultipleChoice.setLevel(Double.parseDouble(children.get("level").toString()));
                questionMultipleChoice.setOrder(Double.parseDouble(children.get("order").toString()));
                questionMultipleChoice.setElName(children.get("elName").toString());
                List<Choices> choicesList = new ArrayList<>();
                List choices = (List) children.get("choices");
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(choice.get("name").toString());
                    choicesList.add(c);
                }
                questionMultipleChoice.setChoices(choicesList);
                int radioButtonID = radioGroupMap.get(id).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroupMap.get(id).findViewById(radioButtonID);
                questionMultipleChoice.setAnswer(radioButton.getText().toString());
                questionGroupRepeaterList.add(questionMultipleChoice);

            } else if (children.get("typeName").equals(basicTextField)) {
                QuestionTextField questionTextField = new QuestionTextField();
                questionTextField.setType(children.get("type").toString());
                questionTextField.setTypeName(children.get("typeName").toString());
                questionTextField.setName(children.get("name").toString());
                questionTextField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionTextField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionTextField.setElName(children.get("elName").toString());
                questionTextField.setAnswer(editTextMap.get(id).getText().toString());
                questionGroupRepeaterList.add(questionTextField);

            } else if (children.get("typeName").equals(checkList)) {
                QuestionCheckList questionCheckList = new QuestionCheckList();
                questionCheckList.setType(children.get("type").toString());
                questionCheckList.setTypeName(children.get("typeName").toString());
                questionCheckList.setName(children.get("name").toString());
                questionCheckList.setLevel(Double.parseDouble(children.get("level").toString()));
                questionCheckList.setOrder(Double.parseDouble(children.get("order").toString()));
                questionCheckList.setElName(children.get("elName").toString());
                List choices = (List) children.get("choices");
                List<Answers> answers = new ArrayList<>();
                List<Choices> choicesList = new ArrayList<>();
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(id);
                    choicesList.add(c);
                    CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+id);
                    if(checkBox.isChecked()){
                        answers.add(new Answers(checkBox.getText().toString()));
                    }
                }

                questionCheckList.setChoices(choicesList);
                questionCheckList.setAnswer(answers);
                questionGroupRepeaterList.add(questionCheckList);

            }
        }
        return questionGroupRepeaterList;
    }



    protected Token tryLogin(String mUsername, String mPassword)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;
        String parameters = "username="+mUsername+"&password="+mPassword;

        try
        {
            url = new URL("http://192.168.0.7/api-token-auth/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            token = gson.fromJson(response, Token.class);
            token.getToken();
            Log.e("Response: ", token.getToken());
            isr.close();
            reader.close();
            connection.disconnect();
            return token;

        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    protected String getCategory()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL("http://192.168.0.7/categories/?format=json");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("token", token.getToken());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " +token.getToken().trim());
            Log.e(" header", connection.getRequestProperties().get("Authorization").toString());

            Log.e("response code","" + connection.getResponseCode());
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            response = "{\"categoryObjectList\":"+response + "}";
            Log.e("appended", response);
            categoryWrapper = gson.fromJson(response, CategoryWrapper.class);
            for(CategoryObject categoryObject : categoryWrapper.getCategoryObjectList()){
                Category category = new Category();
                Log.e("name", categoryObject.getName());
                category.setName(categoryObject.getName());
                category.setUrl(categoryObject.getUrl());
                category.setCreated_by(categoryObject.getCreated_by());
                category.setParent("");
                category.save();
            }
            Log.e("Response", response);
            isr.close();
            reader.close();
            connection.disconnect();
            return response;

        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
            return "[blank";
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "[blank]";
        }

    }

    protected void sendAnswers(String form, String answer)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        JsonAnswer json = new JsonAnswer();
        json.setForm(form);
        json.setAnswer(answer);
        json.setCreated_by("http://192.168.0.7/users/3/");
        json.setDate_modified("2015-11-11T11:00:00Z");
        json.setModified_by("http://192.168.0.7/users/3/");
        json.setState("asdasdasd");
        json.setDate_created("2015-11-11T11:11:00Z");
        String data = gson.toJson(json);
        Log.e("Json: ", data);
        URL url = null;
        String response = null;

        try
        {
            url = new URL("http://192.168.0.7/answers/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Token " +token.getToken());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(data);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            Log.e("Response: ", response);
            isr.close();
            reader.close();
            connection.disconnect();

        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e("Error", e.toString());
        }
    }


    public void backView(LinearLayout current, LinearLayout previous){
        current.setVisibility(View.GONE);
        previous.setVisibility(View.VISIBLE);
        if(viewDeque.isEmpty()){
            back.setVisibility(View.GONE);
        }
    }


    private class GetToken extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            tryLogin("user", "password");
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            sendAnswers("http://192.168.0.7/formbases/1/", jsonAnswer);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class Login extends AsyncTask<String, Void, String> {
        Token new_token=null;
        @Override
        protected String doInBackground(String... params) {
            new_token = tryLogin(username.getText().toString(), password.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            token=new_token;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    private class GetCategories extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(Category.countTable() <= 0){
                getCategory();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (categories!="") {
                categories_retrieved = true;
            }
            categories_retrieved = false;
         }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    public boolean isAllFieldsValid(Map<String, EditText> editTextMap){
        for (Map.Entry<String, EditText> entry : editTextMap.entrySet())
        {
            if(!isInputValid(entry.getValue().getText().toString())){
                entry.getValue().setError("Must not be empty");
                return false;
            }
        }
        return true;

    }
    public boolean isInputValid(String input){
        if(input != null && input.length()>0){
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(MainActivity.this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            if(!dateId.equals("")){
                dateTextViewMap.get(dateId).setText(showDate(arg1, arg2+1, arg3));
            }
        }
    };

    private String showDate(int year, int month, int day) {
        return new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).toString();
    }

    protected String getForms()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL("http://192.168.0.7/formbases/?format=json");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("token", token.getToken());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " +token.getToken().trim());
            Log.e(" header", connection.getRequestProperties().get("Authorization").toString());

            Log.e("response code","" + connection.getResponseCode());
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            response = "{\"formObjectList\":"+response + "}";
            formObjectWrapper = gson.fromJson(response, FormObjectWrapper.class);
            for(FormObject formObject : formObjectWrapper.getFormObjectList()){
                com.dilimanlabs.formbase.objects.Form fo = gson.fromJson(formObject.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                Form.insertData(formObject.getUrl(), formObject.getCreated_by(), formObject.getCategory(), formObject.getContent(), fo.getFormName());
            }
            Log.e("Response", response);
            isr.close();
            reader.close();
            connection.disconnect();
            return response;

        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
            return "[blank";
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "[blank]";
        }

    }

    private class GetForms extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(Form.countTable() <= 0){
                getForms();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
