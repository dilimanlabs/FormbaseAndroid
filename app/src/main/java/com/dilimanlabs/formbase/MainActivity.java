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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dilimanlabs.formbase.authentication.AccountGeneral;
import com.dilimanlabs.formbase.model.Answers;
import com.dilimanlabs.formbase.model.AnswersForApproval;
import com.dilimanlabs.formbase.model.Category;
import com.dilimanlabs.formbase.model.Form;
import com.dilimanlabs.formbase.model.Photos;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.Choices;
import com.dilimanlabs.formbase.objects.FormObjectWrapper;
import com.dilimanlabs.formbase.objects.JsonAnswer;
import com.dilimanlabs.formbase.objects.JsonAnswerWrapper;
import com.dilimanlabs.formbase.objects.PhotoObject;
import com.dilimanlabs.formbase.objects.QuestionCheckList;
import com.dilimanlabs.formbase.objects.QuestionDateField;
import com.dilimanlabs.formbase.objects.QuestionGroup;
import com.dilimanlabs.formbase.objects.QuestionImageField;
import com.dilimanlabs.formbase.objects.QuestionMultipleChoice;
import com.dilimanlabs.formbase.objects.QuestionNumberField;
import com.dilimanlabs.formbase.objects.QuestionRepeater;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    private Gson gson;
    private String categories;
    private boolean categories_retrieved;
    private File file;
    String result;
    private String jsonAnswer;
    private String currentFormURL;
    private Context context;
    private LoginView loginView;
    private TextView path;
    String mCurrentPhotoPath;
    private LinearLayout previousForms;
    private String labelString;
    private CategoryView categoryView;
    private CategoryWrapper categoryWrapper;
    private FormObjectWrapper formObjectWrapper;
    private FormsView formsView;
    private FormView formView;
    private Button login;
    private LinearLayout pauseView;
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
    private Map<String, InnerQuestionView> innerQuestionViewMap;
    private List<InnerQuestionView> innerQuestionViewsList;
    private List childrenRepeat;
    private LinearLayout copyQuestionsLayout;
    private Handler handler;
    private com.dilimanlabs.formbase.objects.Form formWithAnswer;
    static final int REQUEST_TAKE_PHOTO = 1;
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
    private FormBase formBase;
    private Map<String ,List<Object>> repeatedInner;
    private TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        formBase = (FormBase)getApplicationContext();
        initAccounts();
        init();
    }

    public void init(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        path = (TextView)questionsLayout.findViewById(R.id.attachment);
        innerQuestionsLayout = (LinearLayout) findViewById(R.id.innerQuestionsLayout);
        original = (LinearLayout) findViewById(R.id.innerQuestionsCopyLayout);
        back = (ImageView) toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(capture != null){
                    capture.setVisibility(View.GONE);
                }
                LinearLayout remove = FormBase.viewDeque.removeLast();
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
                dispatchTakePictureIntent();
                DataCenter.saved = true;
                DataCenter.questionsLayout = questionsLayout;
                DataCenter.currentPath = mCurrentPhotoPath;
                FormBase.isCaptured = true;
                galleryAddPic();

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
        label = (TextView) toolbar.findViewById(R.id.label);
        previous = new LinearLayout(this);
        innerQuestionViewMap = new HashMap<>();
        innerQuestionViewsList = new ArrayList<>();
        repeatedInner = new HashMap<>();
        handler = new Handler();
        copyQuestionsLayout = questionsLayout;
        loginView = (LoginView) findViewById(R.id.loginView);
        categoryView = (CategoryView) findViewById(R.id.categoryView);
        gson = new Gson();
        try{
            retrieveAllData(questionsLayout, innerQuestionsLayout, original);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initAccounts(){
        accountManager = AccountManager.get(this);
        accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if(accounts.length == 0){
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
    }


    @Override
    public void onPause(){
        Log.e("Pause", "pause");
        super.onPause();
            if(questionsLayout.getVisibility() == View.VISIBLE || FormBase.isCaptured == true){
                Log.e("On Pause", "QuestionsLayout");
                FormBase.isSaved = true;
                FormBase.globalQuestion = questionsLayout;
                FormBase.currentPath = mCurrentPhotoPath;
            }
            DataCenter.questionsLayout = questionsLayout;
            DataCenter.currentPath = mCurrentPhotoPath;

    }

    @Override
    public void onDestroy(){
        Log.e("Destroy", "ondestroy");
        super.onDestroy();
        FormBase.isSaved = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onRes","");
        Log.e("IsSaved: ", ""+FormBase.isSaved);
            if(FormBase.isSaved == true){
                Log.e("Im in", "Save");
                init();
                if(categoryView.getVisibility() == View.VISIBLE){
                    Log.e("category", "onresume category");
                    if(!FormBase.viewDeque.isEmpty()){
                        categoryView.setVisibility(View.GONE);
                    }
                }
                questionsLayout = FormBase.globalQuestion;
                if(FormBase.isCaptured == true){
                    Button attachment = new Button(this);
                    attachment.setText("Attachment: "+FormBase.currentPath);
                    questionsLayout.addView(attachment);

                }
                questionsLayout.setVisibility(View.VISIBLE);
            }
            else if(DataCenter.isLogin ==  true){
                Log.e("Login", "Login");
                init();
            }


    }

    public com.dilimanlabs.formbase.objects.Form createJSONFile(String json){
        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(json, com.dilimanlabs.formbase.objects.Form.class);
        formWithAnswer = new com.dilimanlabs.formbase.objects.Form();
        formWithAnswer.setFormName(form.getFormName());
        Log.e("Form Name: ", form.getFormName());
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
                Multimap<String, Object>  repeatedInner =  ArrayListMultimap.create();
                Log.e("ChildList", ""+childList.size());
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    Log.e("Repeater Id:","Size: "+DataCenter.repeaterIdList.size());
                    for(int r = 0; r<DataCenter.repeaterQuestionNameList.size(); r++){
                        Log.e("ID: ", ""+DataCenter.repeaterIdList.get(r).toString());
                        repeatedInner.put(DataCenter.repeaterQuestionNameList.get(r),insertRepeaterAnswersForOuter(DataCenter.questionListMap.get(DataCenter.repeaterQuestionNameList.get(r).toString()), DataCenter.repeaterIdList.get(r).toString()));
                    }
//                    formWithAnswer.setQuestionGroupRepeaterListOuter(repeatedInner);
                }else{
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
                                    questionDateField.setAnswer(DataCenter.dateTextViewMap.get(children.get("name").toString()).getText().toString());
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
                                    questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(children.get("name").toString()).getText().toString());
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
                                    questionNumberField.setAnswer(DataCenter.editTextMap.get(children.get("name").toString()).getText().toString());
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
                                    int radioButtonID = DataCenter.radioGroupMap.get(children.get("name").toString()).getCheckedRadioButtonId();
                                    RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(children.get("name").toString()).findViewById(radioButtonID);
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
                                    questionTextField.setAnswer(DataCenter.editTextMap.get(children.get("name").toString()).getText().toString());
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
                                        CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+children.get("name").toString());
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
                            questionDateField.setAnswer(DataCenter.dateTextViewMap.get(child.get("name").toString()).getText().toString());
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
                            questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(child.get("name").toString()).getText().toString());
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
                            questionNumberField.setAnswer(DataCenter.editTextMap.get(child.get("name").toString()).getText().toString());
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
                            int radioButtonID = DataCenter.radioGroupMap.get(child.get("name").toString()).getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(child.get("name").toString()).findViewById(radioButtonID);
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
                            questionTextField.setAnswer(DataCenter.editTextMap.get(child.get("name").toString()).getText().toString());
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
                                CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+child.get("name").toString());
                                if(checkBox.isChecked()){
                                    answers.add(new com.dilimanlabs.formbase.objects.Answers(checkBox.getText().toString()));
                                }
                            }
                            questionCheckList.setChoices(choicesList);
                            questionCheckList.setAnswer(answers);
                            objectListInner.add(questionCheckList);


                        }


                    }
                }


                questionGroup.setChildList(objectListInner);
                questionGroup.setLevel(Double.parseDouble(map.get("level").toString()));
                questionGroup.setOrder(Double.parseDouble(map.get("order").toString()));
                questionGroup.setRepeating(Boolean.parseBoolean(map.get("isRepeating").toString()));
                questionGroup.setChildList(createChildList(DataCenter.questionListMap.get(map.get("name").toString())));
                List<QuestionRepeater> questionRepeaterList = new ArrayList<>();
                List<Object> listObject1 = (List)repeatedInner.get(map.get("name").toString());
                for(Object list : listObject1){
                    QuestionRepeater questionRepeater = new QuestionRepeater();
                    questionRepeater.setQuestionRepeater(list);
                    questionRepeaterList.add(questionRepeater);
                }
                questionGroup.setQuestionGroupRepeaterList(questionRepeaterList);
                objectList.add(questionGroup);
            }
            else if(map.get("typeName").equals(dateField)){
                QuestionDateField questionDateField = new QuestionDateField();
                questionDateField.setTypeName(map.get("typeName").toString());
                questionDateField.setName(map.get("name").toString());
                questionDateField.setLevel(Double.parseDouble(map.get("level").toString()));
                questionDateField.setOrder(Double.parseDouble(map.get("order").toString()));
                questionDateField.setElName(map.get("elName").toString());
                questionDateField.setAnswer(DataCenter.dateTextViewMap.get(map.get("name").toString()).getText().toString());
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
                questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(map.get("name").toString()).getText().toString());
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
                questionNumberField.setAnswer(DataCenter.editTextMap.get(map.get("name").toString()).getText().toString());
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
                int radioButtonID = DataCenter.radioGroupMap.get(map.get("name").toString()).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(map.get("name").toString()).findViewById(radioButtonID);
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
                questionTextField.setAnswer(DataCenter.editTextMap.get(map.get("name").toString()).getText().toString());
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
                    CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+map.get("name").toString());
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
            formWithAnswer.setQuestionGroupRepeaterListInner(repeatedGroup);
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
                        dispatchTakePictureIntent();
                        galleryAddPic();
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

                DataCenter.dateTextViewMap.put(String.valueOf(questionGroupView.getId()), dateTextView);
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
                DataCenter.toggleButtonMap.put(String.valueOf(questionGroupView.getId()), toggleButton);
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
                DataCenter.editTextMap.put(String.valueOf(questionGroupView.getId()), answer);
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
                DataCenter.radioGroupMap.put(String.valueOf(questionGroupView.getId()), radioGroup);
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
                DataCenter.editTextMap.put(String.valueOf(questionGroupView.getId()), answer);
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
                    DataCenter.checkBoxMap.put(choice.get("name").toString()+String.valueOf(questionGroupView.getId()), checkBox);
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
                        dispatchTakePictureIntent();
                        galleryAddPic();
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
                DataCenter.dateTextViewMap.put(children.get("name").toString(), dateTextView);
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
                DataCenter.toggleButtonMap.put(children.get("name").toString(), toggleButton);
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
                DataCenter.editTextMap.put(children.get("name").toString(), answer);
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
                DataCenter.radioGroupMap.put(children.get("name").toString(), radioGroup);
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
                DataCenter.editTextMap.put(children.get("name").toString(), answer);
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
                    DataCenter.checkBoxMap.put(choice.get("name").toString()+children.get("name").toString(), checkBox);
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

    public QuestionSwitchView createQuestionSwitchView(String typeName, String questionName, boolean isRepeater, String repeaterId){
        QuestionSwitchView questionSwitchView = new QuestionSwitchView(this);
        TextView type = (TextView)questionSwitchView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionSwitchView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choices = (LinearLayout)questionSwitchView.findViewById(R.id.questionChoices);
        ToggleButton toggleButton = new ToggleButton(this);
        if(isRepeater){
            DataCenter.toggleButtonMap.put(repeaterId, toggleButton);
        }
        else{
            DataCenter.toggleButtonMap.put(questionName, toggleButton);
        }
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
        DataCenter.toggleButtonMap.put(questionName, toggleButton);
        toggleButton.setTextOn("True");
        toggleButton.setTextOff("False");
        toggleButton.setChecked(Boolean.parseBoolean(answer));
        choices.addView(toggleButton);
        return questionSwitchView;
    }

    @SuppressWarnings("deprecation")
    public QuestionDateFieldView createQuestionDateField(String typeName, final String questionName, boolean isRepeater, String repeaterId){
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
        if(isRepeater){
            DataCenter.dateTextViewMap.put(repeaterId, dateTextView);
        }
        else{
            DataCenter.dateTextViewMap.put(questionName, dateTextView);
        }
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
        DataCenter.dateTextViewMap.put(questionName, dateTextView);
        Log.e("Content", ""+questionName);
        return questionDateField;
    }

    public QuestionNumberFieldView createQuestionNumberFieldView(String typeName, String questionName, boolean isRepeater, String repeaterId){
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
        if(isRepeater){
            DataCenter.editTextMap.put(repeaterId, answer);
        }
        else{
            DataCenter.editTextMap.put(questionName, answer);
        }
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
        DataCenter.editTextMap.put(questionName, answer);
        return questionNumberFieldView;
    }

    public QuestionBasicTextField createQuestionBasicTextField(String typeName, String questionName, boolean isRepeater, String repeaterId){
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
        if(isRepeater){
            DataCenter.editTextMap.put(repeaterId, answer);
        }
        else{
            DataCenter.editTextMap.put(questionName, answer);
        }
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
        DataCenter.editTextMap.put(questionName, answer);
        return questionBasicTextField;
    }

    public QuestionImageFieldView createQuestionImageFieldView(String typeName, String questionName, boolean isRepeater, String repeaterId){
        QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(this);
        TextView type = (TextView)questionImageFieldView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView)questionImageFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        questionImageFieldView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                galleryAddPic();
            }
        });
        return questionImageFieldView;
    }

    public QuestionMultipleChoiceView createQuestionMultipleChoiceView(String typeName, String questionName, List choices, boolean isRepeater, String repeaterId){
        QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(this);
        TextView type = (TextView) questionMultipleChoiceView.findViewById(R.id.questionType);
        type.setText(typeName);
        TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choicesLayout = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
        RadioGroup radioGroup = new RadioGroup(this);
        if(isRepeater){
            Log.e("RadioGroup Id: ", ""+repeaterId);
            DataCenter.radioGroupMap.put(repeaterId, radioGroup);
            Log.e("RadioGroup Contains", ""+DataCenter.radioGroupMap.containsKey(repeaterId));
            Log.e("RadioGroup Map Size:", ""+DataCenter.radioGroupMap.size());
        }
        else{
            DataCenter.radioGroupMap.put(questionName, radioGroup);
        }
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
        DataCenter.radioGroupMap.put(questionName, radioGroup);
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

    public QuestionCheckListView createQuestionCheckListView(String typeName, String questionName, List choices, boolean isRepeater, String repeaterId){
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
            if(isRepeater){
                DataCenter.checkBoxMap.put(repeaterId, choiceCheckBox);
            }
            else{
                DataCenter.checkBoxMap.put(choice.get("name").toString()+questionName, choiceCheckBox);
            }
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
            DataCenter.checkBoxMap.put(choice.get("name").toString()+questionName, choiceCheckBox);
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
            final LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                ImageButton add = (ImageButton)questionGroupView.findViewById(R.id.add_new);
                final List childList = (List)map.get("childList");
                TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                questionName.setText(map.get("name").toString());
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                questions.setVisibility(View.VISIBLE);
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Add", "Clicked");
                            questions.addView(createRepeaterQuestion(childList, questions, map.get("name").toString()));
                            Log.e("Map Size: ", ""+DataCenter.repeaterIdList.size());
                        }
                    });
                }
                else{
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    for(int x = 0; x<childList.size(); x++){
                        final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                        if(!Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
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
                                                DataCenter.editTextMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                                DataCenter.toggleButtonMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                                DataCenter.checkBoxMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                                DataCenter.radioGroupMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                                DataCenter.dateTextViewMap.remove(String.valueOf(newQuestionGroupView.getId()));
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
                        }

                        else if(child.get("typeName").equals(dateField)){
                            questions.addView(createQuestionDateField(child.get("typeName").toString(), child.get("name").toString(), false, null));
                        }
                        else if(child.get("typeName").equals(imageField)){
                            questions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString(), false, null));
                        }
                        else if(child.get("typeName").equals(switchQuestion)){
                            questions.addView(createQuestionSwitchView(child.get("typeName").toString(), child.get("name").toString(), false, null));
                        }
                        else if(child.get("typeName").equals(numberField)){
                            questions.addView(createQuestionNumberFieldView(child.get("typeName").toString(), child.get("name").toString(), false, null));
                        }
                        else if(child.get("typeName").equals(multipleChoice)){
                            List choices = (List)child.get("choices");
                            questions.addView(createQuestionMultipleChoiceView(child.get("typeName").toString(), child.get("name").toString(), choices, false, null));
                        }
                        else if(child.get("typeName").equals(basicTextField)){
                            questions.addView(createQuestionBasicTextField(child.get("typeName").toString(), child.get("name").toString(), false, null));
                        }
                        else if(child.get("typeName").equals(checkList)){
                            List choices = (List)child.get("choices");
                            questions.addView(createQuestionCheckListView(child.get("typeName").toString(), child.get("name").toString(), choices, false, null));
                        }


                    }

                }
                questionsLayout.addView(questionGroupView);

            }
            else if(map.get("typeName").equals(dateField)){
                questionsLayout.addView(createQuestionDateField(map.get("typeName").toString(), map.get("name").toString(), false, null));
            }
            else if(map.get("typeName").equals(imageField)){
                questionsLayout.addView(createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null));
            }
            else if(map.get("typeName").equals(switchQuestion)){
                questionsLayout.addView(createQuestionSwitchView(map.get("typeName").toString(), map.get("name").toString(), false, null));
            }
            else if(map.get("typeName").equals(numberField)){
                questionsLayout.addView(createQuestionNumberFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null));
            }
            else if(map.get("typeName").equals(multipleChoice)){
                List choices = (List)map.get("choices");
                questionsLayout.addView(createQuestionMultipleChoiceView(map.get("typeName").toString(), map.get("name").toString(), choices, false, null));
            }
            else if(map.get("typeName").equals(basicTextField)){
                questionsLayout.addView(createQuestionBasicTextField(map.get("typeName").toString(), map.get("name").toString(), false, null));
            }
            else if(map.get("typeName").equals(checkList)){
                List choices = (List)map.get("choices");
                questionsLayout.addView(createQuestionCheckListView(map.get("typeName").toString(), map.get("name").toString(), choices, false, null));

            }
        }
        Button submit = new Button(this);
        submit.setText("Submit");
        questionsLayout.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Im in", "Im in");
                if(isAllFieldsValid(DataCenter.editTextMap)){
                    Log.e("Json", json);
                    com.dilimanlabs.formbase.objects.Form newForm = createJSONFile(json);
                    jsonAnswer = gson.toJson(newForm);
                    Log.e("Form", gson.toJson(newForm));
                    Answers.insertAnswer(fo, formName, jsonAnswer);
                    Photos.insertPhoto(formName, mCurrentPhotoPath);
                    if(previousForms != null){
                        updatePreviousButton(previousForms);
                    }
                    Toast.makeText(MainActivity.this, "Form saved successfully.", Toast.LENGTH_SHORT).show();
                    backView(current, FormBase.viewDeque.removeLast());
                    capture.setVisibility(View.GONE);
                }





            }
        });
    }

    public void createPreviousForm(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original, final String json, final Answers answer){
        questionsLayout.removeAllViews();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                final TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                questions.setVisibility(View.VISIBLE);
                List childListName = (List)map.get("childList");
                LinkedTreeMap<String, Object> name = (LinkedTreeMap<String, Object>) childListName.get(0);
                questionName.setText(name.get("name").toString());
                Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                Log.e("Name: ", " "+map.get("name"));
                List childList = (List)map.get("questionRepeaterList");
                for(int x = 0; x<childList.size(); x++){
                    final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                    List children = (List)child.get("repeaterQuestion");
                    Log.e("Repeater Question", child.get("repeaterQuestion").toString());
                        for(int y = 0; y<children.size(); y++){
                            final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) children.get(y);
                            if(c.get("typeName").equals(questionGroup)){
                                questions.addView(createQuestionGroupViewTop(child, original, questionsLayout));
                            }
                            else if(c.get("typeName").equals(dateField)){
                                questions.addView(createQuestionDateFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(imageField)){
                                questions.addView(createQuestionImageFieldView(c.get("typeName").toString(), c.get("name").toString(), false, null));
                            }
                            else if(c.get("typeName").equals(switchQuestion)){
                                questions.addView(createQuestionSwitchViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(numberField)){
                                questions.addView(createQuestionNumberFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(multipleChoice)){
                                List choices = (List)c.get("choices");
                                questions.addView(createQuestionMultipleChoiceViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(basicTextField)){
                               questions.addView(createQuestionBasicTextFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(checkList)){
                                List choices = (List)c.get("choices");
                                List answers = (List)c.get("answers");
                                questions.addView(createQuestionCheckListViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, answers));
                            }
                        }

                }
                questionsLayout.addView(questionGroupView);
            }
            else if(map.get("typeName").equals(dateField)){
                questionsLayout.addView(createQuestionDateFieldWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(imageField)){
                questionsLayout.addView(createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null));
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
            Button attachment = new Button(this);
            attachment.setText("Attachment: "+Photos.getPhotoPath(answer.local_id));
            Button submit = new Button(this);
            submit.setText("Submit");
            questionsLayout.addView(attachment);
            questionsLayout.addView(submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Im in", "Im in");
                    if(isAllFieldsValid(DataCenter.editTextMap)){
                        Log.e("Json", json);
                        jsonAnswer = json;
                        DataCenter.answers = answer;
                        new SendAnswer().execute();
                        Toast.makeText(MainActivity.this, "Form submitted successfully.", Toast.LENGTH_SHORT).show();
                        backView(current, FormBase.viewDeque.removeLast());
                        capture.setVisibility(View.GONE);
                    }
            }
            });

        }else{
            Button attachment = new Button(this);
            attachment.setText("Attachment: "+Photos.getPhotoPath(answer.local_id));
            Button submitted = new Button(this);
            submitted.setText("Submitted");
            submitted.setClickable(false);
            questionsLayout.addView(attachment);
            questionsLayout.addView(submitted);
        }

    }

    public void createPreviousFormForApproval(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final String url){
        questionsLayout.removeAllViews();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                questions.setVisibility(View.VISIBLE);
                questionName.setText(map.get("name").toString());
//                Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
//                Log.e("Name: ", " "+map.get("name"));
//                List childList = (List)map.get("questionGroupRepeaterList");
//                for(int x = 0; x<childList.size(); x++){
//                    final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
//                    if(child.get("typeName").equals(questionGroup)){
//                        questions.addView(createQuestionGroupViewTop(child, original, questionsLayout));
//                    }
//                    else if(child.get("typeName").equals(dateField)){
//                        questions.addView(createQuestionDateFieldWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
//                    }
//                    else if(child.get("typeName").equals(imageField)){
//                        questions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString(), false, null));
//                    }
//                    else if(child.get("typeName").equals(switchQuestion)){
//                        questions.addView(createQuestionSwitchViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
//                    }
//                    else if(child.get("typeName").equals(numberField)){
//                        questions.addView(createQuestionNumberFieldViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
//                    }
//                    else if(child.get("typeName").equals(multipleChoice)){
//                        List choices = (List)child.get("choices");
//                        questions.addView(createQuestionMultipleChoiceViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), choices, child.get("answer").toString()));
//                    }
//                    else if(child.get("typeName").equals(basicTextField)){
//                        Log.e("Type", child.get("typeName").toString());
//                        Log.e("Name", child.get("name").toString());
//                        Log.e("Answer", child.get("answer").toString());
//                        questions.addView(createQuestionBasicTextFieldWithAnswer(child.get("typeName").toString(), child.get("name").toString(), child.get("answer").toString()));
//                    }
//                    else if(child.get("typeName").equals(checkList)){
//                        List choices = (List)child.get("choices");
//                        List answers = (List)child.get("answers");
//                        questions.addView(createQuestionCheckListViewWithAnswer(child.get("typeName").toString(), child.get("name").toString(), choices, answers));
//                    }
//
//
//                }
                questionsLayout.addView(questionGroupView);
            }
            else if(map.get("typeName").equals(dateField)){
                questionsLayout.addView(createQuestionDateFieldWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString()));
            }
            else if(map.get("typeName").equals(imageField)){
                questionsLayout.addView(createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null));
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
            Button approve = new Button(this);
            approve.setText("Approve");
            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Clicked", "Clicked");
                    new SendApproveAnswer(url).execute();
                }
            });
            questionsLayout.addView(approve);


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
                questionDateField.setAnswer(DataCenter.dateTextViewMap.get(id).getText().toString());
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
                questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(id).getText().toString());
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
                questionNumberField.setAnswer(DataCenter.editTextMap.get(id).getText().toString());
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
                int radioButtonID = DataCenter.radioGroupMap.get(id).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(id).findViewById(radioButtonID);
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
                questionTextField.setAnswer(DataCenter.editTextMap.get(id).getText().toString());
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
                    CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+id);
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

    public List<Object> insertRepeaterAnswersForOuter(List childrenList, String id ){
        Log.e("ChildrenList", ""+childrenList.size());
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
                questionDateField.setAnswer(DataCenter.dateTextViewMap.get(id).getText().toString());
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
                questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(id).getText().toString());
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
                questionNumberField.setAnswer(DataCenter.editTextMap.get(id).getText().toString());
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
                Log.e("Repeater Id", ""+id);
                Log.e("RadioGroupSize", ""+DataCenter.radioGroupMap.size());
                Log.e("RadioGroup", ""+DataCenter.radioGroupMap.size());
                Log.e("RadioGroup Contains", ""+DataCenter.radioGroupMap.containsKey(id));
                Log.e("RadioGroup Id", ""+DataCenter.radioGroupMap.get(id).getCheckedRadioButtonId());

                    int radioButtonID = DataCenter.radioGroupMap.get(id).getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(id).findViewById(radioButtonID);
                    questionMultipleChoice.setAnswer(radioButton.getText().toString());
                    questionGroupRepeaterList.add(questionMultipleChoice);



            } else if (children.get("typeName").equals(basicTextField)) {
                Log.e("TextField", ""+"Im here");
                QuestionTextField questionTextField = new QuestionTextField();
                questionTextField.setType(children.get("type").toString());
                questionTextField.setTypeName(children.get("typeName").toString());
                questionTextField.setName(children.get("name").toString());
                questionTextField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionTextField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionTextField.setElName(children.get("elName").toString());
                questionTextField.setAnswer(DataCenter.editTextMap.get(id).getText().toString());
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
                    CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+id);
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

    public List<Object> createChildList(List childrenList){
        Log.e("ChildrenList", ""+childrenList.size());
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
                Log.e("RadioGroupSize", ""+DataCenter.radioGroupMap.size());
                Log.e("RadioGroup", ""+DataCenter.radioGroupMap.size());
                questionGroupRepeaterList.add(questionMultipleChoice);



            } else if (children.get("typeName").equals(basicTextField)) {
                Log.e("TextField", ""+"Im here");
                QuestionTextField questionTextField = new QuestionTextField();
                questionTextField.setType(children.get("type").toString());
                questionTextField.setTypeName(children.get("typeName").toString());
                questionTextField.setName(children.get("name").toString());
                questionTextField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionTextField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionTextField.setElName(children.get("elName").toString());
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
                questionCheckList.setChoices(choicesList);
                questionCheckList.setAnswer(answers);
                questionGroupRepeaterList.add(questionCheckList);

            }
        }
        return questionGroupRepeaterList;
    }



    protected String sendAnswers(String form, String answer)
    {
        HttpURLConnection connection;
        JsonAnswer ja = null;
        InputStream inputStream;
        InputStreamReader isr;
        OutputStreamWriter request = null;
        JsonAnswer json = new JsonAnswer();
        json.setDate_created("2001-11-11T11:11:00Z");
        json.setDate_modified("2001-11-11T11:11:00Z");
        json.setAnswer(answer);
        json.setState("submitted");
        json.setCreated_by(DataCenter.GLOBAL_URL+"user/1/");
        json.setFormbase(form);
        json.setModified_by(DataCenter.GLOBAL_URL+"user/1/");
        String data = gson.toJson(json);
        Log.e("Json: ", data);
        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"answers/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Token " +accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
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
                ja = gson.fromJson(response, JsonAnswer.class);
                Answers.updateAnswer(DataCenter.answers, answer, ja.getUrl());
                Log.e("Response: ", response);
                isr.close();
                reader.close();
                connection.disconnect();
            }




        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e("Error", e.toString());

        }
        if(ja == null){
         return "";
        } else{
            return ja.getUrl();
        }

    }

    private String multipost(String urlString, MultipartEntityBuilder reqEntity) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Token " + accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            conn.setRequestProperty("Content-Type", "application/multi-part");
            conn.setRequestProperty("Accept", "application/multi-part");
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            reqEntity.build().writeTo(os);
            os.flush();
            os.close();
            conn.connect();
            String resp=readStream(conn.getInputStream());
            Log.e("resp",resp);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

                return resp;
            }

        } catch (Exception e) {
            Log.e("error",e.getMessage());
        }
        return null;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }


    public void backView(LinearLayout current, LinearLayout previous){
        current.setVisibility(View.GONE);
        previous.setVisibility(View.VISIBLE);
        if(FormBase.viewDeque.isEmpty()){
            back.setVisibility(View.GONE);
            label.setText("Categories");
        }
        if(!FormBase.labelDeque.isEmpty()){
                label.setText(FormBase.labelDeque.removeLast());
        }
    }


    private class SendAnswer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.e("Path", DataCenter.currentPath);
            String answerURL = sendAnswers(currentFormURL, jsonAnswer);
            String formName = Answers.getAnswerByURL(answerURL).getLocal_id();
            File file = new File(DataCenter.currentPath);
            String response = sendPhoto(answerURL, "name", file);
            PhotoObject photoObject = gson.fromJson(response, PhotoObject.class);
            Log.e("PhotoObject", photoObject.getUrl());
            Photos.updatePhoto(formName, photoObject.getUrl());
            Log.e("Photo", response);
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
                DataCenter.dateTextViewMap.get(dateId).setText(showDate(arg1, arg2+1, arg3));
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
            final Button buttonCategory = new Button(MainActivity.this);
            buttonCategory.setText("Uncategorized");
            buttonCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    formsView = (FormsView) findViewById(R.id.formsView);
                    LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
                    Log.e("Category: ", "Uncategorized");
                    List<Form> form = Form.getAllFormsByCategory("Uncategorized");
                    Log.e("Size: ", ""+Form.countTable());
                    linearLayoutForms.removeAllViews();
                    for (final Form f : form) {
                        final Button buttonForm = new Button(MainActivity.this);
                        buttonForm.setText(f.getName());
                        buttonForm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentFormURL=f.getUrl();
                                final String formNameString = buttonForm.getText().toString();
                                formView = (FormView)findViewById(R.id.formView);
                                final LinearLayout retrieveForms = (LinearLayout) formView.findViewById(R.id.retrieveForms);
                                Button addButton = (Button) formView.findViewById(R.id.addButton);
                                Button retrieveButton = (Button) formView.findViewById(R.id.retrieve);
                                retrieveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("Retrieve Clicked", "Clicked");
                                        Toast.makeText(MainActivity.this, "Downloading answers for approval...", Toast.LENGTH_LONG);
                                        new GetAnswers(retrieveForms, buttonForm.getText().toString()).execute();
                                    }
                                });
                                previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                                previousForms.removeAllViews();
                                previousCategory = f.getCategory();
                                for(Answers answers : Answers.getAllForms(f.getCategory())){
                                    final Button previousFormsButton = new Button(MainActivity.this);
                                    previousFormsButton.setText(answers.getLocal_id());
                                    previousFormsButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("Click", "Click");
                                            Button button = (Button)v;
                                            Answers fo = Answers.getFormByLocal_ID(button.getText().toString());
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            Log.e("Prev Content", fo.getContent());
                                            createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                                            formView.setVisibility(View.GONE);
                                            FormBase.viewDeque.addLast(formView);
                                            questionsLayout.setVisibility(View.VISIBLE);
                                            label.setText(previousFormsButton.getText().toString());
                                            labelString = buttonForm.getText().toString();
                                            FormBase.labelDeque.addLast(buttonForm.getText().toString());
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
                                                        FormBase.viewDeque.addLast(formView);
                                                        questionsLayout.setVisibility(View.VISIBLE);
                                                        label.setText(value);
                                                        FormBase.labelDeque.addLast(buttonForm.getText().toString());
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
                                FormBase.viewDeque.addLast(formsView);
                                formView.setVisibility(View.VISIBLE);
                                label.setText(buttonForm.getText().toString());
                                FormBase.labelDeque.addLast(buttonCategory.getText().toString());
                                current = formView;
                            }
                        });
                        linearLayoutForms.addView(buttonForm);
                    }
                    categoryView.setVisibility(View.GONE);
                    FormBase.viewDeque.addLast(categoryView);
                    back.setVisibility(View.VISIBLE);
                    formsView.setVisibility(View.VISIBLE);
                    label.setText("Uncategorized");
                    FormBase.labelDeque.addLast("Category");
                    current = formsView;
                }
            });
            layoutCategories.addView(buttonCategory);
        }
        for (final Category category : categories) {

            final Button buttonCategory = new Button(MainActivity.this);
            buttonCategory.setText(category.getName());
            DataCenter.buttonMap.put(buttonCategory, category.getName());
            buttonCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    formsView = (FormsView) findViewById(R.id.formsView);
                    LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
                    Log.e("Category: ", Category.getCategoryName(DataCenter.buttonMap.get(v)).getUrl());
                    Button b = (Button)v;
                    List<Form> form = Form.getAllFormsByCategory(b.getText().toString());
                    Log.e("Size: ", ""+Form.countTable());
                    linearLayoutForms.removeAllViews();
                    for (final Form f : form) {
                        final Button buttonForm = new Button(MainActivity.this);
                        buttonForm.setText(f.getName());
                        buttonForm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentFormURL=f.getUrl();
                                final String formNameString = buttonForm.getText().toString();
                                formView = (FormView)findViewById(R.id.formView);
                                final LinearLayout retrieveForms = (LinearLayout) formView.findViewById(R.id.retrieveForms);
                                Button addButton = (Button) formView.findViewById(R.id.addButton);
                                Button retrieveButton = (Button) formView.findViewById(R.id.retrieve);
                                retrieveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("Retrieve Clicked", "Clicked");
                                        new GetAnswers(retrieveForms, buttonForm.getText().toString()).execute();
                                    }
                                });
                                previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                                previousForms.removeAllViews();
                                previousCategory = f.getCategory();
                                for(final Answers answers : Answers.getAllForms(f.getCategory())){
                                    final Button previousFormsButton = new Button(MainActivity.this);
                                    Log.e("Button", answers.getLocal_id());
                                    previousFormsButton.setText(answers.getLocal_id());
                                    previousFormsButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("Previous Form", "Click");
                                            Button b = (Button)v;
                                            Answers fo = Answers.getFormByLocal_ID(b.getText().toString());
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            Log.e("Prev Content", fo.getContent());
                                            createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                                            formView.setVisibility(View.GONE);
                                            FormBase.viewDeque.addLast(formView);
                                            questionsLayout.setVisibility(View.VISIBLE);
                                            label.setText(previousFormsButton.getText().toString());
                                            labelString = buttonForm.getText().toString();
                                            FormBase.labelDeque.addLast(buttonForm.getText().toString());
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
                                                        FormBase.viewDeque.addLast(formView);
                                                        questionsLayout.setVisibility(View.VISIBLE);
                                                        label.setText(value);
                                                        FormBase.labelDeque.addLast(buttonForm.getText().toString());
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
                                FormBase.viewDeque.addLast(formsView);
                                formView.setVisibility(View.VISIBLE);
                                label.setText(buttonForm.getText().toString());
                                FormBase.labelDeque.addLast(buttonCategory.getText().toString());
                                current = formView;
                            }
                        });
                        linearLayoutForms.addView(buttonForm);
                    }
                    categoryView.setVisibility(View.GONE);
                    FormBase.viewDeque.addLast(categoryView);
                    back.setVisibility(View.VISIBLE);
                    formsView.setVisibility(View.VISIBLE);
                    label.setText(buttonCategory.getText().toString());
                    FormBase.labelDeque.addLast("Category");
                    current = formsView;
                }
            });
            layoutCategories.addView(buttonCategory);
        }
        categoryView.setVisibility(View.VISIBLE);
    }

    public void updatePreviousButton(LinearLayout previous){
        if(!previous.equals(null)){
            previous.removeAllViews();
            for(Answers answers : Answers.getAllForms(previousCategory)){
                final Button previousFormsButton = new Button(MainActivity.this);
                previousFormsButton.setText(answers.getLocal_id());
                previousFormsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Click", "Click");
                        Button button = (Button)v;
                        Answers fo = Answers.getFormByLocal_ID(button.getText().toString());
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                        createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo);
                        formView.setVisibility(View.GONE);
                        label.setText(previousFormsButton.getText().toString());
                        FormBase.viewDeque.addLast(formView);
                        FormBase.labelDeque.addLast(labelString);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public Bitmap getBitmap(){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;
    }

    public String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return format1.format(date);
    }

    public String sendPhoto(String answerURL, String name, File file ){
        try
        {
            Random fRandom = new Random();
            String boundary = "--------------------------- --" + fRandom.toString();


            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(DataCenter.GLOBAL_URL+"photos/");
            post.addHeader("Authorization", "Token " + accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            post.addHeader("Content-Type", "multipart/form-data; boundary="+boundary);
            post.addHeader("Accept", "application/json");
            post.addHeader("Accept-Encoding","gzip, deflate");


            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setBoundary(boundary);
            //entityBuilder.setMode(HttpMultipartMode.);
            entityBuilder.addTextBody("answer", answerURL);
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("name", name);
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("url_height", "1");
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("url_width", "1");
            //JSONObject json=new JSONObject();
            //json.put("answer", answerURL);
            //json.put("name", name);
            //json.put("url_height", "1");
            //json.put("url_width", "1");

            //String data = gson.toJson(json);

            //entityBuilder.addTextBody("data",data);
            entityBuilder.setBoundary(boundary);
            if(file != null)
            {

                entityBuilder.addBinaryBody("photo", file, ContentType.create("image/jpg"),name);
                //ContentBody cbFile = new FileBody(file, "image/jpeg");
                //entityBuilder.addPart("photo",cbFile);
                entityBuilder.setBoundary(boundary);
            }

            HttpEntity entity = entityBuilder.build();

            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            HttpEntity httpEntity = response.getEntity();


            InputStream instream = httpEntity.getContent();
            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, HTTP.UTF_8));
            File text = new File("/sdcard/text.html");
            String line = null;
            FileOutputStream stream = new FileOutputStream(text);
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    stream.write(line.getBytes());
                }

            } finally {
                stream.close();
                instream.close();
                reader.close();
            }
            result = buffer.toString();
            //Log.e("result", result);
            return result;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public QuestionGroupView createRepeaterQuestion(List childList,final LinearLayout questionsLayoutRepeater, String questionNameString){
        final QuestionGroupView questions = new QuestionGroupView(MainActivity.this);
        questions.setId(DataCenter.generateViewId());
        DataCenter.repeaterIdList.add(questions.getId());
        DataCenter.repeaterQuestionNameList.add(questionNameString);
        DataCenter.questionListMap.put(questionNameString, childList);
        TextView questionName = (TextView)questions.findViewById(R.id.questionName);
        questionName.setVisibility(View.GONE);
        TextView questionTypeTextView = (TextView) questions.findViewById(R.id.questionType);
        questionTypeTextView.setVisibility(View.GONE);
        final ScrollView scrollViewRepeater = (ScrollView)questions.findViewById(R.id.scrollViewRepeater);
        scrollViewRepeater.setVisibility(View.VISIBLE);
        final LinearLayout repeaterQuestions = (LinearLayout)questions.findViewById(R.id.repeaterQuestions);
        repeaterQuestions.setVisibility(View.VISIBLE);
        ImageButton removeInner = (ImageButton)questions.findViewById(R.id.remove_new);
        removeInner.setVisibility(View.VISIBLE);
        removeInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionsLayoutRepeater.removeView(questions);
            }
        });
        for(int x = 0; x<childList.size(); x++){
            final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
            if(child.get("typeName").equals(questionGroup)){
                final QuestionGroupView questionGroupChild = createQuestionGroupViewTop(child, original, questionsLayout);
                ImageButton addInner = (ImageButton)questionGroupChild.findViewById(R.id.add_new);
                if(Boolean.parseBoolean(child.get("isRepeating").toString()) == true){
                    addInner.setVisibility(View.VISIBLE);
                }
                repeaterQuestions.addView(questionGroupChild);
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
                                DataCenter.editTextMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                DataCenter.toggleButtonMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                DataCenter.checkBoxMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                DataCenter.radioGroupMap.remove(String.valueOf(newQuestionGroupView.getId()));
                                DataCenter.dateTextViewMap.remove(String.valueOf(newQuestionGroupView.getId()));
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
                repeaterQuestions.addView(createQuestionDateField(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(imageField)){
                repeaterQuestions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(switchQuestion)){
                repeaterQuestions.addView(createQuestionSwitchView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(numberField)){
                repeaterQuestions.addView(createQuestionNumberFieldView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(multipleChoice)){
                List choices = (List)child.get("choices");
                repeaterQuestions.addView(createQuestionMultipleChoiceView(child.get("typeName").toString(), child.get("name").toString(), choices, true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(basicTextField)){
                repeaterQuestions.addView(createQuestionBasicTextField(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId())));
            }
            else if(child.get("typeName").equals(checkList)){
                List choices = (List)child.get("choices");
                repeaterQuestions.addView(createQuestionCheckListView(child.get("typeName").toString(), child.get("name").toString(), choices, true, String.valueOf(questions.getId())));
            }


        }
        return questions;
    }

    protected String getAnswers()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"answers/?format=json");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("get Answers token", accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " +accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
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
            response = "{\"jsonAnswerList\":"+response + "}";
            JsonAnswerWrapper jsonAnswerWrapper = gson.fromJson(response, JsonAnswerWrapper.class);
            AnswersForApproval.deleteData();
            AnswersForApproval.insertData(jsonAnswerWrapper.getJsonAnswerList());
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

    private class GetAnswers extends AsyncTask<String, Void, String> {
        LinearLayout retrieveViews;
        String labelString;

        private GetAnswers(LinearLayout retrieveViews, String labelString) {
            this.retrieveViews = retrieveViews;
            this.labelString = labelString;
        }

        @Override
        protected String doInBackground(String... params) {
                getAnswers();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            retrieveViews.removeAllViews();
            List<AnswersForApproval> answersForApprovalList = AnswersForApproval.getAllAnswersForApproval();
            for(AnswersForApproval answersForApproval : answersForApprovalList){
                Button button = new Button(MainActivity.this);
                button.setText(answersForApproval.getUrl());
                retrieveViews.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button b = (Button)v;
                        AnswersForApproval afa = AnswersForApproval.getAnswersForApprovalByURL(b.getText().toString());
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(afa.getAnswer(), com.dilimanlabs.formbase.objects.Form.class);
                        Log.e("Prev Content", afa.getAnswer());
                        createPreviousFormForApproval(form, questionsLayout, b.getText().toString());
                        formView.setVisibility(View.GONE);
                        FormBase.viewDeque.addLast(formView);
                        questionsLayout.setVisibility(View.VISIBLE);
                        label.setText(b.getText().toString());
                        FormBase.labelDeque.addLast(labelString);
                        current = questionsLayout;
                    }
                });
            }
            retrieveViews.setVisibility(View.VISIBLE);
            Log.e("Answers For Approval Count", ""+AnswersForApproval.countTable());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    protected String sendApproveAnswer(String url)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL newUrl = null;
        String response = null;
        AnswersForApproval answersForApproval = AnswersForApproval.getAnswersForApprovalByURL(url);
        JsonAnswer jfa = new JsonAnswer();
        jfa.setCreated_by(answersForApproval.getCreated_by());
        jfa.setFormbase(answersForApproval.getFormbase());
        jfa.setState("Approved");
        jfa.setAnswer(answersForApproval.getAnswer());
        jfa.setModified_by(answersForApproval.getModified_by());
        jfa.setDate_created(answersForApproval.getDate_created());
        jfa.setDate_modified(answersForApproval.getDate_modified());
        jfa.setUrl(answersForApproval.getUrl());
        jfa.setFulfilled(answersForApproval.getFulfilled());
        String data = gson.toJson(jfa);
        try
        {
            newUrl = new URL(url);
            connection = (HttpURLConnection) newUrl.openConnection();
            Log.e("get Answers token", accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Token " +accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
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

    private class SendApproveAnswer extends AsyncTask<String, Void, String> {
        String url;

        private SendApproveAnswer(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            sendApproveAnswer(url);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
