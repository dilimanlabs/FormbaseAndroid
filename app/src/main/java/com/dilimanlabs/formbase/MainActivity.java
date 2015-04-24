package com.dilimanlabs.formbase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.dilimanlabs.formbase.authentication.AccountGeneral;
import com.dilimanlabs.formbase.model.Answers;
import com.dilimanlabs.formbase.model.Category;
import com.dilimanlabs.formbase.model.Form;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.Choices;
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
import com.dilimanlabs.formbase.views.FormView;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private Gson gson;
    private String categories;
    private boolean categories_retrieved;
    private File file;
    private String jsonAnswer;
    private String currentFormURL;
    private Context context;
    private LoginView loginView;
    private LinearLayout previousForms;
    private CategoryView categoryView;
    private CategoryWrapper categoryWrapper;
    private FormObjectWrapper formObjectWrapper;
    private FormsView formsView;
    private FormView formView;
    private Button login;
    private LinearLayout pauseView;
    private Deque<LinearLayout> viewDeque;
    private String questionGroup = "Question group";
    private ImageView back;
    private LinearLayout current;
    private LinearLayout previous;
    private String previousCategory;
    private Toolbar toolbar;
    private String imageField = "Image field";
    private String switchQuestion = "Switch (True/False)";
    private String numberField = "Number field";
    private String multipleChoice = "Multiple Choice";
    private String basicTextField = "Basic Text Field";
    private String checkList = "Check List";
    private String dateField = "Date field";
    private String dateId = "";
    private int month, year, day;
    private Calendar calendar;
    private Token token;
    private List<Integer> previousID;
    private QuestionGroupView questionGroupView;
    private Map<String,EditText> editTextMap;
    private Map<String, ToggleButton> toggleButtonMap;
    private Map<Button, String> buttonMap;
    private Map<String, CheckBox> checkBoxMap;
    private Map<String, RadioGroup> radioGroupMap;
    private Map<String, TextView> dateTextViewMap;
    private Map<String, InnerQuestionView> innerQuestionViewMap;
    private List<InnerQuestionView> innerQuestionViewsList;
    private List childrenRepeat;
    private LinearLayout copyQuestionsLayout;
    private Handler handler;
    private com.dilimanlabs.formbase.objects.Form formWithAnswer;
    private static final int CAMERA_REQUEST = 1888;
    private EditText username;
    private EditText password;
    private LinearLayout questionsLayout;
    private LinearLayout innerQuestionsLayout;
    private LinearLayout original;
    private Bitmap photo;
    private AccountManager accountManager;
    private Account [] accounts;
    private Account account;
    private ImageView capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAccounts();
        init();
        handler = new Handler();
        copyQuestionsLayout = questionsLayout;
        loginView = (LoginView) findViewById(R.id.loginView);
        categoryView = (CategoryView) findViewById(R.id.categoryView);
//        login = (Button) loginView.findViewById(R.id.login);
//        username = (EditText) loginView.findViewById(R.id.username);
//        password = (EditText) loginView.findViewById(R.id.password);
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//            }
//        });


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    public void init(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        innerQuestionsLayout = (LinearLayout) findViewById(R.id.innerQuestionsLayout);
        original = (LinearLayout) findViewById(R.id.innerQuestionsCopyLayout);
        back = (ImageView) toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout remove = viewDeque.removeLast();
                if(previousForms != null){
                    updatePreviousButton(previousForms);
                }
                backView(current, remove);
            }
        });
        capture = (ImageView)toolbar.findViewById(R.id.captureImage);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
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
        try{
            retrieveAllData(questionsLayout, innerQuestionsLayout, original);
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        account = accounts[0];
//        AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthToken(account, "android", null, this, null, null);
//        Bundle authTokenBundle = null;
//        try {
//            authTokenBundle = accountManagerFuture.getResult();
//        } catch (OperationCanceledException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (AuthenticatorException e) {
//            e.printStackTrace();
//        }
//        token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
    }

    public void initAccounts(){
        accountManager = AccountManager.get(this);
        accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if(accounts.length == 0){
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onRes","");

        init();
    }

    public Bitmap getPhoto() {
        return photo;
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
                questionGroup.setName(map.get("name").toString());
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
                                List<com.dilimanlabs.formbase.objects.Answers> answers = new ArrayList<>();
                                List<Choices> choicesList = new ArrayList<>();
                                for (int z = 0; z < choices.size(); z++) {
                                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                                    Choices c = new Choices(choice.get("name").toString());
                                    choicesList.add(c);
                                    CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+children.get("name").toString());
                                    if(checkBox.isChecked()){
                                        answers.add(new com.dilimanlabs.formbase.objects.Answers(checkBox.getText().toString()));
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
                        objectListInner.add(questionDateField);
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
                        List<com.dilimanlabs.formbase.objects.Answers> answers = new ArrayList<>();
                        for (int z = 0; z < choices.size(); z++) {
                            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                            Choices c = new Choices(choice.get("name").toString());
                            choicesList.add(c);
                            CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+child.get("name").toString());
                            if(checkBox.isChecked()){
                                answers.add(new com.dilimanlabs.formbase.objects.Answers(checkBox.getText().toString()));
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
                List<com.dilimanlabs.formbase.objects.Answers> answers = new ArrayList<>();
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(choice.get("name").toString());
                    choicesList.add(c);
                    CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+map.get("name").toString());
                    if(checkBox.isChecked()){
                        answers.add(new com.dilimanlabs.formbase.objects.Answers(checkBox.getText().toString()));
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

    public QuestionSwitchView createQuestionSwitchViewWithAnswer(String typeName, String questionName, String answer){
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
        toggleButton.setChecked(Boolean.parseBoolean(answer));
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

    @SuppressWarnings("deprecation")
    public QuestionDateFieldView createQuestionDateFieldWithAnswer(String typeName, final String questionName, String answer){
        QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
        TextView type = (TextView)questionDateField.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
        name.setText(questionName);
        final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
        dateTextView.setText(answer);
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

    public QuestionNumberFieldView createQuestionNumberFieldViewWithAnswer(String typeName, String questionName, String numberAnswer){
        QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(this);
        TextView type = (TextView)questionNumberFieldView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionNumberFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        answer.setInputType(InputType.TYPE_CLASS_NUMBER);
        answer.setLines(1);
        answer.setText(numberAnswer);
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

    public QuestionBasicTextField createQuestionBasicTextFieldWithAnswer(String typeName, String questionName, String textFieldAnswer){
        QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(this);
        TextView type = (TextView)questionBasicTextField.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionBasicTextField.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        questionAnswer.addView(answer);
        answer.setLines(1);
        answer.setText(textFieldAnswer);
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

    public QuestionMultipleChoiceView createQuestionMultipleChoiceViewWithAnswer(String typeName, String questionName, List choices, String answer){
        QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(this);
        List<RadioButton> radioButtonList = new ArrayList<>();
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
            radioButton.setId(x);
            radioButton.setText(choice.get("name").toString());
            radioButtonList.add(radioButton);
            radioGroup.addView(radioButton);
        }
        for(RadioButton radioButton : radioButtonList){
            if(radioButton.getText().toString().equals(answer)){
                radioButton.setChecked(true);
            }
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

    public QuestionCheckListView createQuestionCheckListViewWithAnswer(String typeName, String questionName, List choices, List answers){
        List<String> answer = new ArrayList<>();
        for(int i=0; i<answers.size(); i++){
            LinkedTreeMap<String, Object> a = (LinkedTreeMap<String, Object>) answers.get(i);
            answer.add(a.get("answer").toString());
        }
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
            if(answer.contains(choice.get("name"))){
                choiceCheckBox.setChecked(true);
            }
            questionChoices.addView(choiceCheckBox);
        }
        return questionCheckListView;
    }

    public void createAllQuestions(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original, final String json, final Form fo, final String formName){
        capture.setVisibility(View.VISIBLE);
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
        submit.setText("Submit");
        questionsLayout.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Im in", "Im in");
                if(isAllFieldsValid(editTextMap)){
                    Log.e("Json", json);
                    com.dilimanlabs.formbase.objects.Form newForm = createJSONFile(json);
                    jsonAnswer = gson.toJson(newForm);
                    Log.e("Form", gson.toJson(newForm));
                    Answers.insertAnswer(fo, formName, jsonAnswer);
                }





            }
        });
    }

    public void createPreviousForm(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original, final String json, final Answers answer){
        capture.setVisibility(View.VISIBLE);
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
                        questions.addView(createQuestionGroupViewTop(child, original, questionsLayout));
                    }
                    else if(child.get("typeName").equals(dateField)){
                        questions.addView(createQuestionDateFieldWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
                    }
                    else if(child.get("typeName").equals(imageField)){
                        questions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString()));
                    }
                    else if(child.get("typeName").equals(switchQuestion)){
                        questions.addView(createQuestionSwitchViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
                    }
                    else if(child.get("typeName").equals(numberField)){
                        questions.addView(createQuestionNumberFieldViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
                    }
                    else if(child.get("typeName").equals(multipleChoice)){
                        List choices = (List)child.get("choices");
                        questions.addView(createQuestionMultipleChoiceViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), choices, child.get("answer").toString()));
                    }
                    else if(child.get("typeName").equals(basicTextField)){
                        questions.addView(createQuestionBasicTextFieldWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
                    }
                    else if(child.get("typeName").equals(checkList)){
                        List choices = (List)child.get("choices");
                        List answers = (List)child.get("answers");
                        questions.addView(createQuestionCheckListViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), choices, answers));
                    }


                }
                questionsLayout.addView(questionGroupView);
            }
            else if(map.get("typeName").equals(dateField)){
                questionsLayout.addView(createQuestionDateFieldWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(imageField)){
                questionsLayout.addView(createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString()));
            }
            else if(map.get("typeName").equals(switchQuestion)){
                questionsLayout.addView(createQuestionSwitchViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(numberField)){
                questionsLayout.addView(createQuestionNumberFieldViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(multipleChoice)){
                List choices = (List)map.get("choices");
                questionsLayout.addView(createQuestionMultipleChoiceViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(), choices, map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(basicTextField)){
                questionsLayout.addView(createQuestionBasicTextFieldWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(checkList)){
                List choices = (List)map.get("choices");
                List answers = (List)map.get("answers");
                questionsLayout.addView(createQuestionCheckListViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(), choices, answers));

            }
        }
        if(answer.getState().equals("draft")){
            Button submit = new Button(this);
            submit.setText("Submit");
            questionsLayout.addView(submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Im in", "Im in");
                    if(isAllFieldsValid(editTextMap)){
                        Log.e("Json", json);
                        com.dilimanlabs.formbase.objects.Form newForm = createJSONFile(json);
                        jsonAnswer = gson.toJson(newForm);
                        Log.e("Form", gson.toJson(newForm));
                        Answers.updateAnswer(answer, jsonAnswer);
//                    new SendAnswer().execute();
                    }
            }
            });

        }else{
            Button submitted = new Button(this);
            submitted.setText("Submitted");
            submitted.setClickable(false);
            questionsLayout.addView(submitted);
        }

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
                List<com.dilimanlabs.formbase.objects.Answers> answers = new ArrayList<>();
                List<Choices> choicesList = new ArrayList<>();
                for (int z = 0; z < choices.size(); z++) {
                    LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(z);
                    Choices c = new Choices(id);
                    choicesList.add(c);
                    CheckBox checkBox = checkBoxMap.get(choice.get("name").toString()+id);
                    if(checkBox.isChecked()){
                        answers.add(new com.dilimanlabs.formbase.objects.Answers(checkBox.getText().toString()));
                    }
                }

                questionCheckList.setChoices(choicesList);
                questionCheckList.setAnswer(answers);
                questionGroupRepeaterList.add(questionCheckList);

            }
        }
        return questionGroupRepeaterList;
    }



    protected void sendAnswers(String form, String answer)
    {
        HttpURLConnection connection;
        InputStream inputStream;
        InputStreamReader isr;
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
            connection.setRequestProperty("Authorization", "Token " +token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(data);
            request.flush();
            request.close();
            Log.e("request code",""+ connection.getResponseCode());
            String line = "";
            if(connection.getResponseCode() == 400){
                inputStream = connection.getErrorStream();
                isr = new InputStreamReader(inputStream);
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
            else{
                inputStream = connection.getInputStream();
                isr = new InputStreamReader(inputStream);
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
            Log.e("Response Error", connection.getErrorStream().toString());




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


    private class SendAnswer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //retrieve URL and jsonAnswer from params

            sendAnswers(currentFormURL, jsonAnswer);
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


    public void retrieveAllData(final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, final LinearLayout original){
        Log.e("Categort Size", ""+Category.countTable());
        loginView.setVisibility(View.GONE);
        LinearLayout layoutCategories = (LinearLayout) categoryView.findViewById(R.id.layoutCategories);
        layoutCategories.removeAllViews();
        List<Category> categories = Category.getAllCategories();
        //check if there exists a form without category
        if(!Form.getAllFormsByCategory("Uncategorized").isEmpty()){
            Button button = new Button(MainActivity.this);
            button.setText("Uncategorized");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    formsView = (FormsView) findViewById(R.id.formsView);
                    LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
                    Log.e("Category: ", "Uncategorized");
                    List<Form> form = Form.getAllFormsByCategory("Uncategorized");
                    Log.e("Size: ", ""+Form.countTable());
                    linearLayoutForms.removeAllViews();
                    for (final Form f : form) {
                        final Button button = new Button(MainActivity.this);
                        button.setText(f.getName());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentFormURL=f.getUrl();
                                final String formNameString = button.getText().toString();
                                formView = (FormView)findViewById(R.id.formView);
                                Button addButton = (Button) formView.findViewById(R.id.addButton);
                                previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                                previousForms.removeAllViews();
                                previousCategory = f.getCategory();
                                for(Answers answers : Answers.getAllForms(f.getCategory())){
                                    final Button previousFormsButton = new Button(MainActivity.this);
                                    previousFormsButton.setText(answers.getSubName());
                                    previousFormsButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("Click", "Click");
                                            Button button = (Button)v;
                                            Answers fo = Answers.getFormBySubName(button.getText().toString());
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                                            formView.setVisibility(View.GONE);
                                            viewDeque.addLast(formView);
                                            questionsLayout.setVisibility(View.VISIBLE);
                                            current = questionsLayout;
                                        }
                                    });
                                    previousForms.addView(previousFormsButton);
                                }
                                addButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText input = new EditText(MainActivity.this);
                                        input.setHint("Form name");
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Enter form name")
                                                .setView(input)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String value = input.getText().toString();
                                                        Form fo = Form.getFormByName(formNameString);
                                                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                                        createAllQuestions(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(),fo,value);
                                                        formView.setVisibility(View.GONE);
                                                        viewDeque.addLast(formView);
                                                        questionsLayout.setVisibility(View.VISIBLE);
                                                        current = questionsLayout;
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Do nothing.
                                            }
                                        }).show();
                                    }
                                });
                                formsView.setVisibility(View.GONE);
                                viewDeque.addLast(formsView);
                                formView.setVisibility(View.VISIBLE);
                                current = formView;
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
                    Button b = (Button)v;
                    List<Form> form = Form.getAllFormsByCategory(b.getText().toString());
                    Log.e("Size: ", ""+Form.countTable());
                    linearLayoutForms.removeAllViews();
                    for (final Form f : form) {
                        final Button button = new Button(MainActivity.this);
                        button.setText(f.getName());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentFormURL=f.getUrl();
                                final String formNameString = button.getText().toString();
                                formView = (FormView)findViewById(R.id.formView);
                                Button addButton = (Button) formView.findViewById(R.id.addButton);
                                previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                                previousForms.removeAllViews();
                                previousCategory = f.getCategory();
                                for(final Answers answers : Answers.getAllForms(f.getCategory())){
                                    final Button previousFormsButton = new Button(MainActivity.this);
                                    Log.e("Button", answers.getSubName());
                                    previousFormsButton.setText(answers.getSubName());
                                    previousFormsButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("Click", "Click");
                                            Button b = (Button)v;
                                            Answers fo = Answers.getFormBySubName(b.getText().toString());
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                                            formView.setVisibility(View.GONE);
                                            viewDeque.addLast(formView);
                                            questionsLayout.setVisibility(View.VISIBLE);
                                            current = questionsLayout;
                                        }
                                    });
                                    previousForms.addView(previousFormsButton);
                                }
                                addButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText input = new EditText(MainActivity.this);
                                        input.setHint("Form name");
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Enter form name")
                                                .setView(input)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        String value = input.getText().toString();
                                                        Form fo = Form.getFormByName(formNameString);
                                                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                                        createAllQuestions(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(),fo,value);
                                                        formView.setVisibility(View.GONE);
                                                        viewDeque.addLast(formView);
                                                        questionsLayout.setVisibility(View.VISIBLE);
                                                        current = questionsLayout;
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Do nothing.
                                            }
                                        }).show();
                                    }
                                });
                                formsView.setVisibility(View.GONE);
                                viewDeque.addLast(formsView);
                                formView.setVisibility(View.VISIBLE);
                                current = formView;
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

    public void updatePreviousButton(LinearLayout previous){
        if(!previous.equals(null)){
            previous.removeAllViews();
            for(Answers answers : Answers.getAllForms(previousCategory)){
                Button previousFormsButton = new Button(MainActivity.this);
                previousFormsButton.setText(answers.getSubName());
                previousFormsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Click", "Click");
                        Button button = (Button)v;
                        Answers fo = Answers.getFormBySubName(button.getText().toString());
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                        createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                        formView.setVisibility(View.GONE);
                        viewDeque.addLast(formView);
                        questionsLayout.setVisibility(View.VISIBLE);
                        current = questionsLayout;
                    }
                });
                previousForms.addView(previousFormsButton);
            }
        }

    }

    private void addNewAccount(String accountType, String authTokenType) {
        AccountManager.get(this).addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.d("Formbase", "AddNewAccount Bundle is " + bnd);
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                    finish();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    public String convertDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return null;
    }
}
