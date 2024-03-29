package com.dilimanlabs.formbase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.activeandroid.ActiveAndroid;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.dilimanlabs.formbase.adapater.AnswerListViewAdapter;
import com.dilimanlabs.formbase.authentication.AccountGeneral;
import com.dilimanlabs.formbase.model.Answers;
import com.dilimanlabs.formbase.model.AnswersForApproval;
import com.dilimanlabs.formbase.model.Category;
import com.dilimanlabs.formbase.model.CurrentUser;
import com.dilimanlabs.formbase.model.Form;
import com.dilimanlabs.formbase.model.Photos;
import com.dilimanlabs.formbase.model.PhotosImageView;
import com.dilimanlabs.formbase.model.Projects;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.Choices;
import com.dilimanlabs.formbase.objects.Email;
import com.dilimanlabs.formbase.objects.FormObjectWrapper;
import com.dilimanlabs.formbase.objects.JsonAnswer;
import com.dilimanlabs.formbase.objects.JsonAnswerWrapper;
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
import com.dilimanlabs.formbase.views.AnswersView;
import com.dilimanlabs.formbase.views.CategoryCardView;
import com.dilimanlabs.formbase.views.CategoryView;
import com.dilimanlabs.formbase.views.FormView;
import com.dilimanlabs.formbase.views.FormsView;
import com.dilimanlabs.formbase.views.InnerQuestionView;
import com.dilimanlabs.formbase.views.LoginView;
import com.dilimanlabs.formbase.views.PreviousView;
import com.dilimanlabs.formbase.views.QuestionBasicTextField;
import com.dilimanlabs.formbase.views.QuestionCheckListView;
import com.dilimanlabs.formbase.views.QuestionDateFieldView;
import com.dilimanlabs.formbase.views.QuestionGroupView;
import com.dilimanlabs.formbase.views.QuestionImageFieldView;
import com.dilimanlabs.formbase.views.QuestionLocationView;
import com.dilimanlabs.formbase.views.QuestionMultipleChoiceView;
import com.dilimanlabs.formbase.views.QuestionNumberFieldView;
import com.dilimanlabs.formbase.views.QuestionSwitchView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Gson gson;
    String result;
    private Button currentButton;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout projects;
    private String jsonAnswer;
    private String currentFormURL;
    private Context context;
    private LoginView loginView;
    private DrawerLayout mDrawerLayout;
    private TextView path;
    private String formNameString;
    String mCurrentPhotoPath;
    private LinearLayout previousForms;
    private AnswerListViewAdapter answerListViewAdapter;
    private static int current_page = 1;
    private int ival = 1;
    private int loadLimit = 10;
    private String labelString;
    private CategoryView categoryView;
    private CategoryWrapper categoryWrapper;
    private FormObjectWrapper formObjectWrapper;
    private FormsView formsView;
    private FormView formView;
    private LinearLayout formsViewContainer;
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
    private TextView username;
    private TextView email;
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
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mainLayout;
    private Button logout;
    private boolean pause;
    private ImageView download;
    private ImageView sendEmail;
    private AlertDialogWrapper.Builder alertDialogWrapper;
    private ImageView imageView;
    private MapFragment mapFragment;
    private Thread.UncaughtExceptionHandler defaultUEH;
    private ImageView upload;
    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("On Create", "On Create");
        formBase = (FormBase)getApplicationContext();
        init();
        if(CurrentUser.countTable() > 0){
            email.setText(CurrentUser.getCurrentUser().getEmail());
            username.setText(CurrentUser.getCurrentUser().getUsername());
            Log.e("Projects", ""+Projects.countTable());
        }
        try{
            retrieveAllData(questionsLayout, innerQuestionsLayout, original);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }

    public void init(){
        formsViewContainer = (LinearLayout) findViewById(R.id.formsViewContainer);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        currentButton = new Button(this);
        mLayoutManager = new LinearLayoutManager(this);
        username = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,  mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        path = (TextView)questionsLayout.findViewById(R.id.attachment);
        innerQuestionsLayout = (LinearLayout) findViewById(R.id.innerQuestionsLayout);
        original = (LinearLayout) findViewById(R.id.innerQuestionsCopyLayout);
        back = (ImageView) toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                if(null != upload){
                    upload.setVisibility(View.GONE);
                }
                if(capture != null){
                    capture.setVisibility(View.GONE);
                }
                Log.e("Before size", ""+FormBase.viewDeque.size());
                if(!FormBase.viewDeque.isEmpty()){
                    LinearLayout remove = FormBase.viewDeque.removeLast();
                    Log.e("After size", ""+FormBase.viewDeque.size());
                    if(previousForms != null){
                        updatePreviousButton(previousForms);
                    }
                    if(remove instanceof FormsView){
                        Log.e("Im in Instance", "Im in instance");
                        formsViewContainer.removeAllViews();
                        formsViewContainer.addView(remove);
                        backView(current, formsViewContainer);
                    }
                    else{
                        Log.e("Not instance", "Not instance");
                        backView(current, remove);
                    }
                }
            }
        });
        download = (ImageView)toolbar.findViewById(R.id.download);
        sendEmail = (ImageView)toolbar.findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                input.setHint("recepient@example.com;another_recepient@example.com;");
                new AlertDialogWrapper.Builder(MainActivity.this)
                        .setTitle("Recipients")
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new SendEmail(input.getText().toString(), DataCenter.ANSWER_URL).execute();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Download");
                new DownloadFile(DataCenter.GLOBAL_URL+"download/answers/"+DataCenter.DOWNLOAD_ID+"/?filetype=pdf").execute();
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
        projects = (LinearLayout) findViewById(R.id.projects);
        projects.removeAllViews();
        _unCaughtExceptionHandler = UEH.UEHInit(defaultUEH);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createSubmissionBins();
    }

    public void initAccounts(){
        accountManager = AccountManager.get(this);
        accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if(accounts.length == 0){
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
        }

    }

    public void logout(){
        DataCenter.isLogout = true;
        new AlertDialogWrapper.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        accountManager = AccountManager.get(MainActivity.this);
                        accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                        accountManager.removeAccount(accounts[0], null, null);
                        ActiveAndroid.getDatabase().close();
                        FormBase.clearAllStaticData();
                        DataCenter.clearAllStaticVariables();
                        final Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                DataCenter.isLogout = false;
            }
        }).show();
    }


    @Override
    public void onPause(){
        Log.e("Pause", "pause");
        super.onPause();
        if(!DataCenter.isLogout){
            if(questionsLayout.getVisibility() == View.VISIBLE || FormBase.isCaptured == true){
                Log.e("On Pause", "QuestionsLayout");
                FormBase.isSaved = true;
                FormBase.globalQuestion = questionsLayout;
                FormBase.currentPath = mCurrentPhotoPath;
            }
            DataCenter.questionsLayout = questionsLayout;
            DataCenter.currentPath = mCurrentPhotoPath;
            FormBase.currentButtonText = currentButton.getText().toString();
            pause = true;
        }
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
        initAccounts();
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
                    if(!FormBase.isQuestionImage){
                        Button attachment = new Button(this);
                        attachment.setText("Attachment: "+FormBase.currentPath);
                        questionsLayout.addView(attachment);
                    }
                    else{
                        FormBase.isQuestionImage = false;
                    }

                }
                questionsLayout.setVisibility(View.VISIBLE);
            }
            else if(DataCenter.isLogin ==  true){
                Log.e("Login", "Login");
                init();
                if(CurrentUser.countTable() > 0){
                    email.setText(CurrentUser.getCurrentUser().getEmail());
                    username.setText(CurrentUser.getCurrentUser().getUsername());
                    Log.e("Projects", ""+Projects.countTable());
                }
                try{
                    retrieveAllData(questionsLayout, innerQuestionsLayout, original);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                DataCenter.isLogin = false;
            }
        if(!(categoryView.getVisibility() == View.VISIBLE)){
            Log.e("DrawerToggle", "DrawerToggle");
            mDrawerToggle.setDrawerIndicatorEnabled(false);
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
                String questionGroupName = map.get("name").toString();
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
                        repeatedInner.put(DataCenter.repeaterQuestionNameList.get(r),insertRepeaterAnswersForOuter(DataCenter.questionListMap.get(DataCenter.repeaterQuestionNameList.get(r).toString()), DataCenter.repeaterIdList.get(r).toString(),r));
                    }
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
                                    questionDateField.setType(children.get("type").toString());
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
                                    Log.e("Answer", ""+DataCenter.toggleButtonMap.get(children.get("name").toString()).getText().toString());
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
                                    questionMultipleChoice.setElName(child.get("elName").toString());
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
                                    Spinner spinner = DataCenter.spinnerMap.get(children.get("name").toString());
                                    if(choices.size() >= 10){
                                        questionMultipleChoice.setAnswer(String.valueOf(spinner.getSelectedItem()));
                                    }
                                    else{
                                        questionMultipleChoice.setAnswer(radioButton.getText().toString());
                                    }
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
                            questionDateField.setType(child.get("type").toString());
                            questionDateField.setName(child.get("name").toString());
                            questionDateField.setLevel(Double.parseDouble(child.get("level").toString()));
                            questionDateField.setOrder(Double.parseDouble(child.get("order").toString()));
                            questionDateField.setElName(child.get("elName").toString());
                            questionDateField.setAnswer(DataCenter.dateTextViewMap.get(questionGroupName+child.get("name").toString()).getText().toString());
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
                            if(!DataCenter.imageTextViewMap.containsKey(questionGroupName+child.get("name"))){
                                questionImageField.setAnswer("No attachment");
                            }else{
                                questionImageField.setAnswer(DataCenter.imageTextViewMap.get(questionGroupName+child.get("name").toString()).getText().toString());
                            }
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
                            questionSwitch.setAnswer(DataCenter.toggleButtonMap.get(questionGroupName+child.get("name").toString()).getText().toString());
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
                            questionNumberField.setMinimum(Double.parseDouble(child.get("minimum").toString()));
                            questionNumberField.setMaximum(Double.parseDouble(child.get("maximum").toString()));
                            questionNumberField.setAnswer(DataCenter.editTextMap.get(questionGroupName+child.get("name").toString()).getText().toString());
                            objectListInner.add(questionNumberField);

                        }
                        else if(child.get("typeName").equals(multipleChoice)){
                            Log.e("Question Group", "Question Group");
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
                            int radioButtonID = DataCenter.radioGroupMap.get(questionGroupName+child.get("name").toString()).getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(questionGroupName+child.get("name").toString()).findViewById(radioButtonID);
                            Spinner spinner = DataCenter.spinnerMap.get(questionGroupName+child.get("name").toString());
                            if(choices.size() >= 10){
                                questionMultipleChoice.setAnswer(String.valueOf(spinner.getSelectedItem()));
                            }
                            else{
                                questionMultipleChoice.setAnswer(radioButton.getText().toString());
                            }
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
                            Log.e("Answer", DataCenter.editTextMap.get(questionGroupName+child.get("name").toString()).getText().toString());
                            questionTextField.setAnswer(DataCenter.editTextMap.get(questionGroupName+child.get("name").toString()).getText().toString());
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
                                CheckBox checkBox = DataCenter.checkBoxMap.get(questionGroupName+choice.get("name").toString()+child.get("name").toString());
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

                questionGroup.setLevel(Double.parseDouble(map.get("level").toString()));
                questionGroup.setOrder(Double.parseDouble(map.get("order").toString()));
                questionGroup.setRepeating(Boolean.parseBoolean(map.get("isRepeating").toString()));
                if(Boolean.parseBoolean(map.get("isRepeating").toString())){
                    questionGroup.setChildList(createChildList(DataCenter.questionListMap.get(map.get("name").toString())));
                }
                else{
                    questionGroup.setChildList(objectListInner);
                }
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
                if(DataCenter.imageTextViewMap.containsKey(map.get("name").toString())){
                    questionImageField.setAnswer(DataCenter.imageTextViewMap.get(map.get("name").toString()).getText().toString());
                    PhotosImageView.insertPhoto(map.get("name").toString(), DataCenter.imageTextViewMap.get(map.get("name").toString()).getText().toString());
                }
                else{
                    questionImageField.setAnswer("No attachment");
                    PhotosImageView.insertPhoto(map.get("name").toString(), "No attachment");
                }
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
//                Log.e("Ranged", ""+Boolean.parseBoolean(map.get("ranged").toString()));
//                questionNumberField.setRanged(Boolean.parseBoolean(map.get("ranged").toString()));
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

                if(choices.size() >= 10){
                    Spinner spinner = DataCenter.spinnerMap.get(map.get("name").toString());
                    questionMultipleChoice.setAnswer(String.valueOf(spinner.getSelectedItem()));
                }
                else{
                    int radioButtonID = DataCenter.radioGroupMap.get(map.get("name").toString()).getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)DataCenter.radioGroupMap.get(map.get("name").toString()).findViewById(radioButtonID);
                    questionMultipleChoice.setAnswer(radioButton.getText().toString());
                }
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
        submit.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
        submit.setTextColor(context.getResources().getColor(R.color.white));
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
        submit.setTextColor(context.getResources().getColor(R.color.white));
        submit.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
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

    public QuestionSwitchView createQuestionSwitchView(String typeName, String questionName, boolean isRepeater, String repeaterId, boolean isQuestionGroup, String questionGroupName){
        QuestionSwitchView questionSwitchView = new QuestionSwitchView(this);
        TextView name = (TextView)questionSwitchView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choices = (LinearLayout)questionSwitchView.findViewById(R.id.questionChoices);
        final ToggleButton toggleButton = new ToggleButton(this);
        toggleButton.setId(DataCenter.generateViewId());
        if(isRepeater){
            DataCenter.repeaterToggleButton.put(repeaterId +questionName, toggleButton);
        }
        else if(isQuestionGroup){
            DataCenter.toggleButtonMap.put(questionGroupName +questionName, toggleButton);
        }
        else{
            DataCenter.toggleButtonMap.put(questionName, toggleButton);
        }
        toggleButton.setTextOn("Yes");
        toggleButton.setTextColor(context.getResources().getColor(R.color.white));
        toggleButton.setTextSize(20);
        toggleButton.setTextOff("No");
        toggleButton.setChecked(false);
        toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton.getText().toString().equals("True")){
                    Log.e("No Requirement", "True");
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red_light));
                }
                else{
                    Log.e("No Requirement", "False");
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
                }
            }
        });
        choices.addView(toggleButton);
        return questionSwitchView;
    }

    public QuestionSwitchView createQuestionSwitchViewWithRequirement(String typeName, String questionName, boolean isRepeater, String repeaterId, boolean isQuestionGroup, final List<Object> requirement_true, final List<Object> requirement_false){
        QuestionSwitchView questionSwitchView = new QuestionSwitchView(this);
        TextView name = (TextView)questionSwitchView.findViewById(R.id.questionName);
        final LinearLayout requirement = (LinearLayout)questionSwitchView.findViewById(R.id.requirement);
        name.setText(questionName);
        LinearLayout choices = (LinearLayout)questionSwitchView.findViewById(R.id.questionChoices);
        final ToggleButton toggleButton = new ToggleButton(this);
        toggleButton.setId(DataCenter.generateViewId());
        if(isRepeater){
            DataCenter.repeaterToggleButton.put(repeaterId +questionName, toggleButton);
        }
        else{
            DataCenter.toggleButtonMap.put(questionName, toggleButton);
        }
        toggleButton.setTextOn("True");
        toggleButton.setTextColor(context.getResources().getColor(R.color.white));
        toggleButton.setTextSize(20);
        toggleButton.setTextOff("False");
        toggleButton.setChecked(false);
        toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton.getText().toString().equals("True")){
                    Log.e("True", "True");
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red_light));
                    requirement.removeAllViews();
                    for(int i=0; i<requirement_true.size(); i++){
                        final LinkedTreeMap<String, Object> requirementChild = (LinkedTreeMap<String, Object>) requirement_true.get(i);
                        if(requirementChild.get("typeName").equals(dateField)){
                            QuestionDateFieldView questionDateFieldView = createQuestionDateField(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionDateFieldView);
                            requirement.addView(questionDateFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(imageField)){
                            QuestionImageFieldView questionImageFieldView = createQuestionImageFieldView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionImageFieldView);
                            requirement.addView(questionImageFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(switchQuestion)){
                            QuestionSwitchView questionSwitchView  = createQuestionSwitchView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionSwitchView);
                            requirement.addView(questionSwitchView);
                        }
                        else if(requirementChild.get("typeName").equals(numberField)){
                            QuestionNumberFieldView questionNumberFieldView = createQuestionNumberFieldView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionNumberFieldView);
                            requirement.addView(questionNumberFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(multipleChoice)){
                            List choices = (List)requirementChild.get("choices");
                            QuestionMultipleChoiceView questionMultipleChoiceView = createQuestionMultipleChoiceView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), choices, false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionMultipleChoiceView);
                            requirement.addView(questionMultipleChoiceView);
                        }
                        else if(requirementChild.get("typeName").equals(basicTextField)){
                            QuestionBasicTextField questionBasicTextField = createQuestionBasicTextField(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionBasicTextField);
                            requirement.addView(questionBasicTextField);
                        }
                        else if(requirementChild.get("typeName").equals(checkList)){
                            List choices = (List)requirementChild.get("choices");
                            QuestionCheckListView questionCheckListView = createQuestionCheckListView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), choices, false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionCheckListView);
                            requirement.addView(questionCheckListView);
                        }
                    }
                    requirement.setVisibility(View.VISIBLE);
                }
                else{
                    Log.e("False", "False");
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
                    requirement.removeAllViews();
                    for(int i=0; i<requirement_false.size(); i++){
                        final LinkedTreeMap<String, Object> requirementChild = (LinkedTreeMap<String, Object>) requirement_false.get(i);
                        if(requirementChild.get("typeName").equals(dateField)){
                            QuestionDateFieldView questionDateFieldView = createQuestionDateField(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionDateFieldView);
                            requirement.addView(questionDateFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(imageField)){
                            QuestionImageFieldView questionImageFieldView = createQuestionImageFieldView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionImageFieldView);
                            requirement.addView(questionImageFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(switchQuestion)){
                            QuestionSwitchView questionSwitchView  = createQuestionSwitchView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionSwitchView);
                            requirement.addView(questionSwitchView);
                        }
                        else if(requirementChild.get("typeName").equals(numberField)){
                            QuestionNumberFieldView questionNumberFieldView = createQuestionNumberFieldView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionNumberFieldView);
                            requirement.addView(questionNumberFieldView);
                        }
                        else if(requirementChild.get("typeName").equals(multipleChoice)){
                            List choices = (List)requirementChild.get("choices");
                            QuestionMultipleChoiceView questionMultipleChoiceView = createQuestionMultipleChoiceView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), choices, false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionMultipleChoiceView);
                            requirement.addView(questionMultipleChoiceView);
                        }
                        else if(requirementChild.get("typeName").equals(basicTextField)){
                            QuestionBasicTextField questionBasicTextField = createQuestionBasicTextField(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionBasicTextField);
                            requirement.addView(questionBasicTextField);
                        }
                        else if(requirementChild.get("typeName").equals(checkList)){
                            List choices = (List)requirementChild.get("choices");
                            QuestionCheckListView questionCheckListView = createQuestionCheckListView(requirementChild.get("typeName").toString(), requirementChild.get("name").toString(), choices, false, null, true, "");
                            FormBase.stringCardViewHashMap.put(requirementChild.get("level").toString()+"_"+requirementChild.get("order").toString(), questionCheckListView);
                            requirement.addView(questionCheckListView);
                        }
                    }
                    requirement.setVisibility(View.VISIBLE);
                }
            }
        });
        choices.addView(toggleButton);
        return questionSwitchView;
    }

    public QuestionSwitchView createQuestionSwitchViewWithAnswer(String typeName, String questionName, String answer){
        QuestionSwitchView questionSwitchView = new QuestionSwitchView(this);
        TextView name = (TextView)questionSwitchView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choices = (LinearLayout)questionSwitchView.findViewById(R.id.questionChoices);
        final ToggleButton toggleButton = new ToggleButton(this);
        toggleButton.setTextOn("True");
        toggleButton.setTextColor(context.getResources().getColor(R.color.white));
        toggleButton.setTextSize(20);
        toggleButton.setTextOff("False");
        toggleButton.setChecked(Boolean.parseBoolean(answer));
        toggleButton.setEnabled(false);
        toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton.getText().toString().equals("True")){
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red_light));
                }
                else{
                    toggleButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
                }
            }
        });
        choices.addView(toggleButton);
        return questionSwitchView;
    }

    @SuppressWarnings("deprecation")
    public QuestionDateFieldView createQuestionDateField(String typeName, final String questionName, boolean isRepeater, final String repeaterId, boolean isQuestionGroup, final String questionGroupName){
        QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
        questionDateField.setId(DataCenter.generateViewId());
        TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
        name.setText(questionName);
        final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
        dateTextView.setId(DataCenter.generateViewId());
        if(isRepeater){
            Log.e("Inserted Repeater", "Date");
            Log.e("Repeater Id Date", repeaterId);
            DataCenter.repeaterDateTextView.put(repeaterId +questionName, dateTextView);
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCenter.isQuestionGroup = false;
                    DataCenter.isDatePickRepeater = true;
                    dateId = repeaterId +questionName;
                    DataCenter.datePickRepeaterID = repeaterId+questionName;
                    Log.e("ID", DataCenter.datePickRepeaterID);
                    showDialog(999);
                }
            });
        }
        else if(isQuestionGroup){
            DataCenter.dateTextViewMap.put(questionGroupName+questionName, dateTextView);
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCenter.isQuestionGroup = true;
                    DataCenter.isDatePickRepeater= false;
                    dateId = questionGroupName+questionName;
                    showDialog(999);
                }
            });
        }
        else{
            DataCenter.dateTextViewMap.put(questionName, dateTextView);
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCenter.isQuestionGroup = false;
                    DataCenter.isDatePickRepeater= false;
                    dateId = questionName;
                    showDialog(999);
                }
            });
        }
        Log.e("Content", ""+questionName);
        return questionDateField;
    }

    @SuppressWarnings("deprecation")
    public QuestionDateFieldView createQuestionDateFieldWithAnswer(String typeName, final String questionName, String answer){
        QuestionDateFieldView questionDateField = new QuestionDateFieldView(this);
        TextView name = (TextView)questionDateField.findViewById(R.id.questionName);
        name.setText(questionName);
        final TextView dateTextView = (TextView)questionDateField.findViewById(R.id.date);
        dateTextView.setText(answer);
        DataCenter.dateTextViewMap.put(questionName, dateTextView);
        Log.e("Content", ""+questionName);
        return questionDateField;
    }

    public QuestionNumberFieldView createQuestionNumberFieldView(String typeName, String questionName, boolean isRepeater, String repeaterId, boolean isQuestionGroup, String questionGroupName){
        QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(this);
        questionNumberFieldView.setId(DataCenter.generateViewId());
        TextView name = (TextView)questionNumberFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        answer.setId(DataCenter.generateViewId());
        answer.setHint("Type here");
        answer.setInputType(InputType.TYPE_CLASS_NUMBER);
        answer.setLines(1);
        questionAnswer.addView(answer);
        questionAnswer.setVisibility(View.VISIBLE);
        if(isRepeater){
            DataCenter.repeaterNumberTextEditText.put(repeaterId + questionName, answer);
        }
        else if(isQuestionGroup){
            DataCenter.editTextMap.put(questionGroupName+questionName, answer);
        }
        else{
            DataCenter.editTextMap.put(questionName, answer);
        }
        return questionNumberFieldView;
    }

    public QuestionNumberFieldView createQuestionNumberFieldViewWithAnswer(String typeName, String questionName, String numberAnswer){
        QuestionNumberFieldView questionNumberFieldView = new QuestionNumberFieldView(this);
        TextView name = (TextView)questionNumberFieldView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionNumberFieldView.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        answer.setHint("Type here");
        answer.setInputType(InputType.TYPE_CLASS_NUMBER);
        answer.setLines(1);
        answer.setText(numberAnswer);
        answer.setEnabled(false);
        questionAnswer.addView(answer);
        questionAnswer.setVisibility(View.VISIBLE);
        DataCenter.editTextMap.put(questionName, answer);
        return questionNumberFieldView;
    }

    public QuestionBasicTextField createQuestionBasicTextField(String typeName, String questionName, boolean isRepeater, String repeaterId, boolean isQuestionGroup, String questionGroupName){
        QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(this);
        questionBasicTextField.setId(DataCenter.generateViewId());
        TextView name = (TextView)questionBasicTextField.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        answer.setId(DataCenter.generateViewId());
        answer.setHint("Type here");
        questionAnswer.addView(answer);
        questionAnswer.setVisibility(View.VISIBLE);
        if(isRepeater){
            Log.e("Repeater Id TextField", repeaterId);
            DataCenter.repeaterBasicTextEditText.put(repeaterId +questionName, answer);
        }
        else if(isQuestionGroup){
            DataCenter.editTextMap.put(questionGroupName+questionName, answer);
        }
        else{
            DataCenter.editTextMap.put(questionName, answer);
        }
        return questionBasicTextField;
    }

    public QuestionBasicTextField createQuestionBasicTextFieldWithAnswer(String typeName, String questionName, String textFieldAnswer){
        QuestionBasicTextField questionBasicTextField = new QuestionBasicTextField(this);
        TextView name = (TextView)questionBasicTextField.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionAnswer = (LinearLayout)questionBasicTextField.findViewById(R.id.questionAnswer);
        EditText answer = new EditText(this);
        questionAnswer.addView(answer);
        answer.setText(textFieldAnswer);
        answer.setEnabled(false);
        questionAnswer.setVisibility(View.VISIBLE);
        DataCenter.editTextMap.put(questionName, answer);
        return questionBasicTextField;
    }

    public QuestionImageFieldView createQuestionImageFieldView(String typeName, final String questionName, final boolean isRepeater, final String repeaterId, final boolean isQuestionGroup, final String questionGroupName){
        QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(this);
        TextView name = (TextView)questionImageFieldView.findViewById(R.id.questionName);
        final LinearLayout imageLayout = (LinearLayout)questionImageFieldView.findViewById(R.id.image);
        ImageView cameraButton = (ImageView)questionImageFieldView.findViewById(R.id.cameraButton);
        name.setText(questionName);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLayout.removeAllViews();
                dispatchTakePictureIntent();
                DataCenter.saved = true;
                DataCenter.questionsLayout = questionsLayout;
                DataCenter.currentPath = mCurrentPhotoPath;
                FormBase.isQuestionImage = true;
                FormBase.isCaptured = true;
                galleryAddPic();
                final TextView textView = new TextView(MainActivity.this);
                if(isRepeater){
                    Log.e("Repeater", "isRepeater");
                    if(DataCenter.repeaterImageTextViewMap.containsKey(repeaterId+questionName)){
                        DataCenter.repeaterImageTextViewMap.remove(repeaterId+questionName);
                        DataCenter.repeaterImageTextViewMap.put(repeaterId+questionName, textView);
                    }
                    else{
                        DataCenter.repeaterImageTextViewMap.put(repeaterId+questionName, textView);
                    }
                }
                else if(isQuestionGroup){
                    if(DataCenter.imageTextViewMap.containsKey(questionGroupName+questionName)){
                        DataCenter.imageTextViewMap.remove(questionGroupName+questionName);
                        DataCenter.imageTextViewMap.put(questionGroupName+questionName, textView);
                    }
                    else{
                        DataCenter.imageTextViewMap.put(questionGroupName+questionName, textView);
                    }
                }
                else{
                    if(DataCenter.imageTextViewMap.containsKey(questionName)){
                        DataCenter.imageTextViewMap.remove(questionName);
                        DataCenter.imageTextViewMap.put(questionName, textView);
                    }
                    else{
                        DataCenter.imageTextViewMap.put(questionName, textView);
                    }
                }
                textView.setText(DataCenter.currentPath);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogWrapper = new AlertDialogWrapper.Builder(MainActivity.this);
                        imageView = new ImageView(MainActivity.this);
                        imageView.setImageBitmap(decodeSampledBitmapFromFile(textView.getText().toString(), 500, 500));
                        alertDialogWrapper.setView(null).setMessage(null);
                        alertDialogWrapper.setTitle("Image")
                                .setView(imageView)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        dialog.cancel();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        }).show();
                    }
                });
                imageLayout.addView(textView);
                imageLayout.setVisibility(View.VISIBLE);
            }
        });
        return questionImageFieldView;
    }

    public QuestionImageFieldView createQuestionImageFieldViewWithAnswer(String typeName, final String questionName, String answer){
        QuestionImageFieldView questionImageFieldView = new QuestionImageFieldView(this);
        TextView name = (TextView)questionImageFieldView.findViewById(R.id.questionName);
        final LinearLayout imageLayout = (LinearLayout)questionImageFieldView.findViewById(R.id.image);
        ImageView cameraButton = (ImageView)questionImageFieldView.findViewById(R.id.cameraButton);
        name.setText(questionName);
        cameraButton.setVisibility(View.GONE);
        final TextView textView = new TextView(MainActivity.this);
//        DataCenter.getImageTextViewMapPrevious.put(questionName, textView);
        DataCenter.imageTextViewList.add(textView);
        textView.setText(answer);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogWrapper = new AlertDialogWrapper.Builder(MainActivity.this);
                imageView = new ImageView(MainActivity.this);
                if(DataCenter.isDraft){
                    imageView.setImageBitmap(decodeSampledBitmapFromFile(textView.getText().toString(), 500, 500));
                }
                else{
                    Picasso.with(MainActivity.this).load(DataCenter.GLOBAL_URL+"photos/attachments/photo/"+textView.getText().toString().split("/")[textView.getText().toString().split("/").length - 1]).resize(500,500).placeholder(R.drawable.progress_animation).into(imageView);
                }
                alertDialogWrapper.setView(null).setMessage(null);
                alertDialogWrapper.setTitle("Image")
                        .setView(imageView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }).show();
            }
        });
        imageLayout.addView(textView);
        imageLayout.setVisibility(View.VISIBLE);
        return questionImageFieldView;
    }

    public QuestionMultipleChoiceView createQuestionMultipleChoiceView(String typeName, String questionName, List choices, boolean isRepeater, String repeaterId, boolean isQuestionGroup, String questionGroupName){
        QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(this);
        questionMultipleChoiceView.setId(DataCenter.generateViewId());
        TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choicesLayout = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setId(DataCenter.generateViewId());
        Spinner spinner = new Spinner(this);
        spinner.setId(DataCenter.generateViewId());
        List<String> choiceList = new ArrayList<>();
        if(isRepeater){
            if(choices.size() >= 10){
                DataCenter.repeaterSpinner.put(repeaterId+questionName, spinner);
            }
            else{
                DataCenter.repeaterRadioGroup.put(repeaterId + questionName, radioGroup);
                DataCenter.radioGroupMap.put(repeaterId + questionName, radioGroup);
            }
            Log.e("RadioGroup Id: ", ""+repeaterId);
            Log.e("RadioGroup Contains", ""+DataCenter.radioGroupMap.containsKey(repeaterId));
            Log.e("RadioGroup Map Size:", ""+DataCenter.radioGroupMap.size());
        }
        else if(isQuestionGroup){
            if(choices.size() >= 10){
                DataCenter.spinnerMap.put(questionGroupName+questionName, spinner);
            }
            else{
                DataCenter.radioGroupMap.put(questionGroupName+questionName, radioGroup);
            }
        }
        else{
            if(choices.size() >= 10){
                DataCenter.spinnerMap.put(questionName, spinner);
            }
            else{
                DataCenter.radioGroupMap.put(questionName, radioGroup);
            }
        }
        for(int x = 0; x<choices.size(); x++){
            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(x);
            choiceList.add(choice.get("name").toString());
            RadioButton radioButton = new RadioButton(this);
            radioButton.setFocusable(true);
            radioButton.setFocusableInTouchMode(true);
            radioButton.setText(choice.get("name").toString());
            radioGroup.addView(radioButton);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, choiceList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        spinner.setAdapter(dataAdapter);
        if(choices.size() >= 10){
            Log.e("Spinner", "Spinner");
            choicesLayout.addView(spinner);
        }
        else{
            Log.e("Radio", "Radio");
            choicesLayout.addView(radioGroup);
        }
        return questionMultipleChoiceView;
    }

    public QuestionMultipleChoiceView createQuestionMultipleChoiceViewWithAnswer(String typeName, String questionName, List choices, String answer){
        QuestionMultipleChoiceView questionMultipleChoiceView = new QuestionMultipleChoiceView(this);
        List<RadioButton> radioButtonList = new ArrayList<>();
        TextView name = (TextView) questionMultipleChoiceView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout choicesLayout = (LinearLayout) questionMultipleChoiceView.findViewById(R.id.questionChoices);
        RadioGroup radioGroup = new RadioGroup(this);
        Spinner spinner = new Spinner(this);
        List<String> choiceList = new ArrayList<>();
        DataCenter.spinnerMap.put(questionName, spinner);
        DataCenter.radioGroupMap.put(questionName, radioGroup);
        for(int x = 0; x<choices.size(); x++){
            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(x);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setEnabled(false);
            radioButton.setId(x);
            radioButton.setText(choice.get("name").toString());
            choiceList.add(choice.get("name").toString());
            radioButtonList.add(radioButton);
            radioGroup.addView(radioButton);
        }
        for(RadioButton radioButton : radioButtonList){
            if(radioButton.getText().toString().equals(answer)){
                radioButton.setChecked(true);
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, choiceList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getPosition(answer));
        if(choices.size() >= 10){
            Log.e("Spinner", "Spinner");
            choicesLayout.addView(spinner);
        }
        else{
            Log.e("Radio", "Radio");
            choicesLayout.addView(radioGroup);
        }
        return questionMultipleChoiceView;
    }

    public QuestionCheckListView createQuestionCheckListView(String typeName, String questionName, List choices, boolean isRepeater, String repeaterId, boolean isQuestionGroup, String questionGroupName){
        QuestionCheckListView questionCheckListView = new QuestionCheckListView(this);
        questionCheckListView.setId(DataCenter.generateViewId());
        TextView name = (TextView) questionCheckListView.findViewById(R.id.questionName);
        name.setText(questionName);
        LinearLayout questionChoices = (LinearLayout)questionCheckListView.findViewById(R.id.questionChoices);
        for(int x = 0; x<choices.size(); x++){
            CheckBox choiceCheckBox = new CheckBox(this);
            choiceCheckBox.setId(DataCenter.generateViewId());
            LinkedTreeMap<String, Object> choice = (LinkedTreeMap<String, Object>) choices.get(x);
            Log.e("CheckList", (x+1)+" "+choice.get("name"));
            if(isRepeater){
                DataCenter.repeaterCheckBox.put(choice.get("name").toString() + repeaterId + questionName, choiceCheckBox);
            }
            else if(isQuestionGroup){
                DataCenter.checkBoxMap.put(questionGroupName+choice.get("name").toString()+questionName, choiceCheckBox);
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
            choiceCheckBox.setEnabled(false);
            questionChoices.addView(choiceCheckBox);
        }
        return questionCheckListView;
    }

    public QuestionLocationView createQuestionLocationView(String questionName){
        final QuestionLocationView questionLocationView = new QuestionLocationView(MainActivity.this);
        Log.e("Question Name", questionName);
        Log.e("TextView", questionLocationView.getQuestionName().toString());
        questionLocationView.getQuestionName().setText(questionName);
        questionLocationView.getGetLocation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.VISIBLE);
                DataCenter.latitude = questionLocationView.getLatitude();
                DataCenter.longitude = questionLocationView.getLongitude();
            }
        });
        return questionLocationView;
    }

    public void createAllQuestions(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original, final String json, final Form fo, final String formName, final String formNameString){
        DataCenter.repeaterQuestionNameList.clear();
        DataCenter.repeaterIdList.clear();
        DataCenter.radioGroupMap.clear();
        capture.setVisibility(View.VISIBLE);
        capture.setVisibility(View.GONE);
        questionsLayout.removeAllViews();
        for(int i =0; i<form.getChildList().size(); i++){
            final LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                FloatingActionButton add = (FloatingActionButton)questionGroupView.findViewById(R.id.add_new);
                final ImageView expand = (ImageView)questionGroupView.findViewById(R.id.expand);
                final List childList = (List)map.get("childList");
                TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final String questionGroupName = map.get("name").toString();
                questionName.setText(map.get("name").toString());
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                questions.setId(DataCenter.generateViewId());
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(questions.getVisibility() == View.VISIBLE){
                            expand.setImageResource(R.drawable.ic_action_expand);
                            questions.setVisibility(View.GONE);
                        }
                        else{
                            expand.setImageResource(R.drawable.ic_action_collapse);
                            questions.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    add.setVisibility(View.VISIBLE);
                    expand.setVisibility(View.GONE);
                    questions.setVisibility(View.VISIBLE);
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
                    Log.e("Repeater", "False");
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    for(int x = 0; x<childList.size(); x++){
                        final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                         if(child.get("typeName").equals(questionGroup)){
                                final QuestionGroupView questionGroupChild = new QuestionGroupView(this);
                                questions.addView(questionGroupChild);
                        }
                        else if(child.get("typeName").equals(dateField)){
                            QuestionDateFieldView questionDateFieldView = createQuestionDateField(child.get("typeName").toString(), child.get("name").toString(), false, null, true, questionGroupName);
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionDateFieldView);
                            questions.addView(questionDateFieldView);
                        }
                        else if(child.get("typeName").equals(imageField)){
                            QuestionImageFieldView questionImageFieldView = createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString(), false, null, true, questionGroupName);
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionImageFieldView);
                            questions.addView(questionImageFieldView);
                        }
                        else if(child.get("typeName").equals(switchQuestion)){
                             QuestionSwitchView questionSwitchView = null;
//                             if(null != child.get("requirement")){
//                                 if(Boolean.parseBoolean(child.get("requirement").toString())){
//                                     questionSwitchView = createQuestionSwitchViewWithRequirement(child.get("typeName").toString(), child.get("name").toString(), false, null, true, (List) child.get("requirement_true"), (List) child.get("requirement_false"));
//                                 }
//                                 else{
                                     questionSwitchView = createQuestionSwitchView(child.get("typeName").toString(), child.get("name").toString(), false, null, true, questionGroupName);
//                                 }
//                             }
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionSwitchView);
                            questions.addView(questionSwitchView);
                        }
                        else if(child.get("typeName").equals(numberField)){
                            QuestionNumberFieldView questionNumberFieldView = createQuestionNumberFieldView(child.get("typeName").toString(), child.get("name").toString(), false, null, true, questionGroupName);
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionNumberFieldView);
                            questions.addView(questionNumberFieldView);
                        }
                        else if(child.get("typeName").equals(multipleChoice)){
                            List choices = (List)child.get("choices");
                            QuestionMultipleChoiceView questionMultipleChoiceView = createQuestionMultipleChoiceView(child.get("typeName").toString(), child.get("name").toString(), choices, false, null, true, questionGroupName);
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionMultipleChoiceView);
                            questions.addView(questionMultipleChoiceView);
                        }
                        else if(child.get("typeName").equals(basicTextField)){
                            QuestionBasicTextField questionBasicTextField = createQuestionBasicTextField(child.get("typeName").toString(), child.get("name").toString(), false, null, true, questionGroupName);
                            FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionBasicTextField);
                            questions.addView(questionBasicTextField);
                        }
                        else if(child.get("typeName").equals(checkList)){
                            List choices = (List)child.get("choices");
                            QuestionCheckListView questionCheckListView = createQuestionCheckListView(child.get("typeName").toString(), child.get("name").toString(), choices, false, null, true, questionGroupName);
                             FormBase.stringCardViewHashMap.put(child.get("level").toString()+"_"+child.get("order").toString(), questionCheckListView);
                            questions.addView(questionCheckListView);
                        }


                    }

                }
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionGroupView);
                questionsLayout.addView(questionGroupView);

            }
            else if(map.get("typeName").equals(dateField)){
                QuestionDateFieldView questionDateFieldView = createQuestionDateField(map.get("typeName").toString(), map.get("name").toString(), false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionDateFieldView);
                questionsLayout.addView(questionDateFieldView);
            }
            else if(map.get("typeName").equals(imageField)){
                QuestionImageFieldView questionImageFieldView = createQuestionImageFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionImageFieldView);
                questionsLayout.addView(questionImageFieldView);
            }
            else if(map.get("typeName").equals(switchQuestion)){
                QuestionSwitchView questionSwitchView = null;
//                if(null != map.get("requirement")){
//                    if(Boolean.parseBoolean(map.get("requirement").toString())){
//                        questionSwitchView = createQuestionSwitchViewWithRequirement(map.get("typeName").toString(), map.get("name").toString(), false, null, true, (List) map.get("requirement_true"), (List) map.get("requirement_false"));
//                    }
//                    else{
                        questionSwitchView = createQuestionSwitchView(map.get("typeName").toString(), map.get("name").toString(), false, null, true, "");
//                    }
//                }
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionSwitchView);
                questionsLayout.addView(questionSwitchView);
            }
            else if(map.get("typeName").equals(numberField)){
                QuestionNumberFieldView questionNumberFieldView = createQuestionNumberFieldView(map.get("typeName").toString(), map.get("name").toString(), false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionNumberFieldView);
                questionsLayout.addView(questionNumberFieldView);
            }
            else if(map.get("typeName").equals(multipleChoice)){
                List choices = (List)map.get("choices");
                QuestionMultipleChoiceView questionMultipleChoiceView = createQuestionMultipleChoiceView(map.get("typeName").toString(), map.get("name").toString(), choices, false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionMultipleChoiceView);
                questionsLayout.addView(questionMultipleChoiceView);
            }
            else if(map.get("typeName").equals(basicTextField)){
                QuestionBasicTextField questionBasicTextField = createQuestionBasicTextField(map.get("typeName").toString(), map.get("name").toString(), false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionBasicTextField);
                questionsLayout.addView(questionBasicTextField);
            }
            else if(map.get("typeName").equals(checkList)){
                List choices = (List)map.get("choices");
                QuestionCheckListView questionCheckListView = createQuestionCheckListView(map.get("typeName").toString(), map.get("name").toString(), choices, false, null, false, "");
                FormBase.stringCardViewHashMap.put(map.get("level").toString()+"_"+map.get("order").toString(), questionCheckListView);
                questionsLayout.addView(questionCheckListView);

            }
            else if(map.get("typeName").equals("qLocation")){
                questionsLayout.addView(createQuestionLocationView(map.get("name").toString()));
            }
        }
        Button submit = new Button(this);
        submit.setText("Save");
        submit.setTextColor(context.getResources().getColor(R.color.white));
        submit.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
        submit.setLayoutParams(layoutParams);
        questionsLayout.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Im in", "Now");
                    if(isAllRadioButtonChecked(DataCenter.radioGroupMap)){
                        Log.e("Json", json);
                        com.dilimanlabs.formbase.objects.Form newForm = createJSONFile(json);
                        jsonAnswer = gson.toJson(newForm);
                        Log.e("JsonAnswer", jsonAnswer);
                        Log.e("Form", gson.toJson(newForm));
                        if(Answers.insertAnswer(fo, formName, jsonAnswer) && Photos.insertPhoto(formName, mCurrentPhotoPath)){
                            Log.e("Previous", "Previous");
                            updateButtonAfterSubmission(previousForms, formNameString);
                            Toast.makeText(MainActivity.this, "Form saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                        backView(current, FormBase.viewDeque.removeLast());
                        capture.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Radiobutton cannot be empty.", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        Log.e("CardView Size: ", ""+FormBase.stringCardViewHashMap.size());
    }


    public void createPreviousForm(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, LinearLayout original, final String json, final Answers answer, final String formNameString, final String name){
        DataCenter.isDraft = true;
        questionsLayout.removeAllViews();
        DataCenter.getImageTextViewMapPrevious.clear();
        DataCenter.imageTextViewList.clear();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                final TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                ImageView expand = (ImageView) questionGroupView.findViewById(R.id.expand);
                expand.setVisibility(View.GONE);
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("questionRepeaterList");
                    for(int x = 0; x<childList.size(); x++){
                        final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                        List children = (List)child.get("repeaterQuestion");
                        Log.e("Repeater Question", child.get("repeaterQuestion").toString());
                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams((LinearLayout.LayoutParams.MATCH_PARENT),(LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout repeaterLayout = new LinearLayout(MainActivity.this);
                        repeaterLayout.setOrientation(LinearLayout.VERTICAL);
                        repeaterLayout.setLayoutParams(linearLayoutParams);
                        TextView numberTextView = new TextView(MainActivity.this);
                        numberTextView.setTextSize(20);
                        numberTextView.setText(""+(x+1));
                        repeaterLayout.addView(numberTextView);
                        for(int y = 0; y<children.size(); y++){

                            final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) children.get(y);
                            if(c.get("typeName").equals(questionGroup)){
                                repeaterLayout.addView(createQuestionGroupViewTop(child, original, questionsLayout));
                            }
                            else if(c.get("typeName").equals(dateField)){
                                repeaterLayout.addView(createQuestionDateFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(imageField)){
                                repeaterLayout.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(switchQuestion)){
                                repeaterLayout.addView(createQuestionSwitchViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(numberField)){
                                repeaterLayout.addView(createQuestionNumberFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(multipleChoice)){
                                List choices = (List)c.get("choices");
                                repeaterLayout.addView(createQuestionMultipleChoiceViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(basicTextField)){
                                repeaterLayout.addView(createQuestionBasicTextFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(checkList)){
                                List choices = (List)c.get("choices");
                                List answers = (List)c.get("answers");
                                repeaterLayout.addView(createQuestionCheckListViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, answers));
                            }

                        }
                        questions.addView(repeaterLayout);

                    }
                }
                else{
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("childList");
                    for(int y = 0; y<childList.size(); y++){
                        final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) childList.get(y);
                        if(c.get("typeName").equals(questionGroup)){
                            questions.addView(createQuestionGroupViewTop(c, original, questionsLayout));
                        }
                        else if(c.get("typeName").equals(dateField)){
                            questions.addView(createQuestionDateFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                        }
                        else if(c.get("typeName").equals(imageField)){
                            questions.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                        }
                        else if(c.get("typeName").equals(switchQuestion)){
                            Log.e("typeName",""+c.get("typeName").toString());
                            Log.e("Name",""+c.get("name").toString());
                            Log.e("Answer",""+c.get("answer").toString());
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
                questionsLayout.addView(createQuestionImageFieldViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(),map.get("answer").toString()));
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
            submit.setText("Submit for Approval");
            submit.setTextColor(context.getResources().getColor(R.color.white));
            submit.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
            submit.setLayoutParams(layoutParams);
            questionsLayout.addView(submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Im in", "Draft");

                        Log.e("Json", json);
                        jsonAnswer = json;
                        DataCenter.answers = answer;
                        if(isNetworkAvailable()){
                            new SendAnswer(formNameString, name).execute();
                            Toast.makeText(MainActivity.this, "Form submitted successfully.", Toast.LENGTH_SHORT).show();
                            backView(current, FormBase.viewDeque.removeLast());
                            capture.setVisibility(View.GONE);
                            upload.setVisibility(View.VISIBLE);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please connect the device to the internet before submitting your answer.", Toast.LENGTH_LONG).show();
                        }

            }
            });

        }else{
            Button submitted = new Button(this);
            submitted.setText("Submitted");
            submitted.setTextColor(context.getResources().getColor(R.color.white));
            submitted.setBackgroundColor(context.getResources().getColor(R.color.yokohama_red));
            submitted.setLayoutParams(layoutParams);
            submitted.setClickable(false);
            questionsLayout.addView(submitted);
        }

    }

    public void createPreviousFormForApproval(com.dilimanlabs.formbase.objects.Form form, final LinearLayout questionsLayout, final String url, final String editing, String status, final FormView formView){
        DataCenter.isDraft = false;
        upload.setVisibility(View.GONE);
        questionsLayout.removeAllViews();
        DataCenter.imageTextViewList.clear();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                ImageView expand = (ImageView) questionGroupView.findViewById(R.id.expand);
                expand.setVisibility(View.GONE);
                final TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("questionRepeaterList");
                    for(int x = 0; x<childList.size(); x++){
                        final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                        List children = (List)child.get("repeaterQuestion");
                        Log.e("Repeater Question", child.get("repeaterQuestion").toString());
                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams((LinearLayout.LayoutParams.MATCH_PARENT),(LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout repeaterLayout = new LinearLayout(MainActivity.this);
                        repeaterLayout.setOrientation(LinearLayout.VERTICAL);
                        repeaterLayout.setLayoutParams(linearLayoutParams);
                        TextView numberTextView = new TextView(MainActivity.this);
                        numberTextView.setTextSize(20);
                        numberTextView.setText(""+(x+1));
                        repeaterLayout.addView(numberTextView);
                        for(int y = 0; y<children.size(); y++){

                            final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) children.get(y);
                            if(c.get("typeName").equals(questionGroup)){
                                repeaterLayout.addView(createQuestionGroupViewTop(child, original, questionsLayout));
                            }
                            else if(c.get("typeName").equals(dateField)){
                                repeaterLayout.addView(createQuestionDateFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(imageField)){
                                repeaterLayout.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(switchQuestion)){
                                repeaterLayout.addView(createQuestionSwitchViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(numberField)){
                                repeaterLayout.addView(createQuestionNumberFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(multipleChoice)){
                                List choices = (List)c.get("choices");
                                repeaterLayout.addView(createQuestionMultipleChoiceViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(basicTextField)){
                                repeaterLayout.addView(createQuestionBasicTextFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                            else if(c.get("typeName").equals(checkList)){
                                List choices = (List)c.get("choices");
                                List answers = (List)c.get("answers");
                                repeaterLayout.addView(createQuestionCheckListViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), choices, answers));
                            }

                        }
                        questions.addView(repeaterLayout);

                    }
                }
                else{
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("childList");
                    for(int y = 0; y<childList.size(); y++){
                        final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) childList.get(y);
                        if(c.get("typeName").equals(questionGroup)){
                            questions.addView(createQuestionGroupViewTop(c, original, questionsLayout));
                        }
                        else if(c.get("typeName").equals(dateField)){
                            questions.addView(createQuestionDateFieldWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                        }
                        else if(c.get("typeName").equals(imageField)){
                            questions.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                        }
                        else if(c.get("typeName").equals(switchQuestion)){
                            Log.e("typeName",""+c.get("typeName").toString());
                            Log.e("Name",""+c.get("name").toString());
                            Log.e("Answer",""+c.get("answer").toString());
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
                questionsLayout.addView(createQuestionImageFieldViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(),map.get("answer").toString()));
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
        if(!status.equals(DataCenter.STATUS)){
            sendEmail.setVisibility(View.GONE);
        }
        if(Boolean.parseBoolean(editing) && !status.equals(DataCenter.STATUS)){
            Button approve = new Button(this);
            approve.setText("Approve");
            approve.setBackgroundColor(context.getResources().getColor(R.color.yokohama_dark));
            approve.setLayoutParams(layoutParams);
            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Clicked", "Clicked");
                    if(isNetworkAvailable()){
                        new SendApproveAnswer(url, questionsLayout, formView).execute();
                        upload.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please connect the device to the internet to send approvals.", Toast.LENGTH_LONG).show();
                    }

                }
            });
            questionsLayout.addView(approve);
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
                questionImageField.setAnswer(DataCenter.imageTextViewMap.get(id).getText().toString());
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
                Spinner spinner = DataCenter.spinnerMap.get(id);
                if(choices.size() >= 10){
                    questionMultipleChoice.setAnswer(String.valueOf(spinner.getSelectedItem()));
                }
                else{
                    questionMultipleChoice.setAnswer(radioButton.getText().toString());
                }

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

    public List<Object> insertRepeaterAnswersForOuter(List childrenList, String id, int r ){
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
                if(!DataCenter.repeaterImageTextViewMap.containsKey(id+children.get("name"))){
                    Log.e("Image answer", "No attachment");
                    questionImageField.setAnswer("No attachment");
                }
                else{
                    Log.e("Image answer", DataCenter.repeaterImageTextViewMap.get(id+children.get("name")).getText().toString());
                    questionImageField.setAnswer(DataCenter.repeaterImageTextViewMap.get(id+children.get("name")).getText().toString());
                }
                questionGroupRepeaterList.add(questionImageField);
            }
            else if(children.get("typeName").equals(dateField)){
                QuestionDateField questionDateField = new QuestionDateField();
                questionDateField.setTypeName(children.get("typeName").toString());
                questionDateField.setName(children.get("name").toString());
                questionDateField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionDateField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionDateField.setElName(children.get("elName").toString());
                questionDateField.setAnswer(DataCenter.repeaterDateTextView.get(id+children.get("name")).getText().toString());
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
                questionSwitch.setAnswer(DataCenter.repeaterToggleButton.get(id+children.get("name")).getText().toString());
                questionGroupRepeaterList.add(questionSwitch);

            } else if (children.get("typeName").equals(numberField)) {
                QuestionNumberField questionNumberField = new QuestionNumberField();
                questionNumberField.setType(children.get("type").toString());
                questionNumberField.setTypeName(children.get("typeName").toString());
                questionNumberField.setName(children.get("name").toString());
                questionNumberField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionNumberField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionNumberField.setElName(children.get("elName").toString());
                questionNumberField.setMinimum(Double.parseDouble(children.get("minimum").toString()));
                questionNumberField.setMaximum(Double.parseDouble(children.get("maximum").toString()));
                questionNumberField.setAnswer(DataCenter.repeaterNumberTextEditText.get(id+children.get("name")).getText().toString());
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
                if(choices.size() >= 10){
                    Spinner spinner = DataCenter.repeaterSpinner.get(id+children.get("name"));
                    questionMultipleChoice.setAnswer(String.valueOf(spinner.getSelectedItem()));
                }
                else{
                    int radioButtonID = DataCenter.repeaterRadioGroup.get(id+children.get("name")).getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)DataCenter.repeaterRadioGroup.get(id+children.get("name")).findViewById(radioButtonID);
                    questionMultipleChoice.setAnswer(radioButton.getText().toString());
                }
                questionGroupRepeaterList.add(questionMultipleChoice);

            } else if (children.get("typeName").equals(basicTextField)) {
                QuestionTextField questionTextField = new QuestionTextField();
                questionTextField.setType(children.get("type").toString());
                questionTextField.setTypeName(children.get("typeName").toString());
                questionTextField.setName(children.get("name").toString());
                questionTextField.setLevel(Double.parseDouble(children.get("level").toString()));
                questionTextField.setOrder(Double.parseDouble(children.get("order").toString()));
                questionTextField.setElName(children.get("elName").toString());
                questionTextField.setAnswer(DataCenter.repeaterBasicTextEditText.get(id+children.get("name")).getText().toString());
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
                    CheckBox checkBox = DataCenter.checkBoxMap.get(choice.get("name").toString()+id+children.get("name").toString());
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



    protected String sendAnswers(String form, String answer, String submission_bin, String name)
    {
        HttpURLConnection connection;
        JsonAnswer ja = null;
        InputStream inputStream;
        InputStreamReader isr;
        OutputStreamWriter request = null;
        JsonAnswer json = new JsonAnswer();
        json.setAnswer(answer);
        json.setFormbase(form);
        json.setName(name);
        Log.e("Bin", submission_bin);
        json.setSubmission_bin(submission_bin);
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
            else if(connection.getResponseCode() == 500){
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
            Toast.makeText(MainActivity.this, "Answer sumbission failed please check your network connection", Toast.LENGTH_LONG).show();
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
        Log.e("Current", ""+(current.getVisibility() == View.GONE));
        previous.setVisibility(View.VISIBLE);
        Log.e("Previous", ""+(previous.getVisibility() == View.VISIBLE));
        if(FormBase.viewDeque.isEmpty()){
            back.setVisibility(View.GONE);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        mapFragment.getView().setVisibility(View.GONE);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
                int height = mainLayout.getHeight();
                Projection projection = googleMap.getProjection();
                Point markerScreenPosition = projection.toScreenLocation(latLng);
                Point pointHalfScreenAbove = new Point(markerScreenPosition.x,
                        markerScreenPosition.y - ((int)(height / -5)));
                LatLng aboveMarkerLatLng = projection
                        .fromScreenLocation(pointHalfScreenAbove);
                CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
                googleMap.animateCamera(center);
                new AlertDialogWrapper.Builder(MainActivity.this)
                        .setTitle("Get this Location?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DataCenter.latitude.setText(String.valueOf(latLng.latitude));
                                DataCenter.longitude.setText(String.valueOf(latLng.longitude));
                                mapFragment.getView().setVisibility(View.GONE);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
            }
        });
    }

    private class SendAnswer extends AsyncTask<String, Void, String> {
        String formNameString;
        String name;
        ProgressDialog dialog;

        public SendAnswer(String formNameString, String name){
            this.formNameString = formNameString;
            this.name = name;
        }
        @Override
        protected String doInBackground(String... params) {
            String answerURL = sendAnswers(currentFormURL, jsonAnswer, FormBase.SUBMISSION_BIN, name);
            if(!DataCenter.imageTextViewMap.isEmpty()){
//                for (Map.Entry<String, TextView> entry : DataCenter.getImageTextViewMapPrevious.entrySet())
//                {
//                    File file = new File(entry.getValue().getText().toString());
//                    String response = sendPhoto(answerURL, answerURL.split("/")[answerURL.split("/").length -1]+"_"+entry.getValue().getText().toString(), file);
//                    Log.e("Response Photo", response);
//                }
                for(TextView textView : DataCenter.imageTextViewList){
                    File file = new File(textView.getText().toString());
                    String response = sendPhoto(answerURL, answerURL.split("/")[answerURL.split("/").length -1]+"_"+textView.getText().toString(), file);
                    Log.e("Response Photo", response);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new GetAnswers(formView, formNameString).execute();
            updateButtonAfterSubmission(previousForms, formNameString);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Sending answer...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public boolean isAllFieldsValid(Map<String, EditText> editTextMap){
        for (Map.Entry<String, EditText> entry : editTextMap.entrySet())
        {
            if(!isInputValid(entry.getValue().getText().toString())){
                entry.getValue().requestFocus();
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

                if(DataCenter.isDatePickRepeater){
                    Log.e("Date Id", dateId);
                    Log.e("Date", showDate(arg1, arg2+1, arg3));
                    Log.e("ID", DataCenter.datePickRepeaterID);
                    Log.e("DateText is in Map?", ""+DataCenter.dateTextViewMap.containsKey(DataCenter.datePickRepeaterID));
                    DataCenter.repeaterDateTextView.get(DataCenter.datePickRepeaterID).setText(showDate(arg1, arg2+1, arg3));
                }
                else{
                    Log.e("Date Id", dateId);
                    DataCenter.dateTextViewMap.get(dateId).setText(showDate(arg1, arg2+1, arg3));
                }
            }
        }
    };

    private String showDate(int year, int month, int day) {
        return new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).toString();
    }


    public void retrieveAllData(final LinearLayout questionsLayout, final LinearLayout innerQuestionsLayout, final LinearLayout original){
        Log.e("Category Size", "" + Category.countTable());
        loginView.setVisibility(View.GONE);
        LinearLayout layoutCategories = (LinearLayout) categoryView.findViewById(R.id.layoutCategories);
        layoutCategories.removeAllViews();
        List<Category> categories = Category.getAllCategories();
        //check if there exists a form without category
        if(!Form.getAllFormsByCategory("Uncategorized").isEmpty()){
            CategoryCardView categoryCardView = new CategoryCardView(MainActivity.this);
            categoryCardView.getCategoryName().setText("Uncategorized");
            categoryCardView.getFormName().setText(Form.getAllFormsByCategory("Uncategorized").size()+" "+"Forms");
            categoryCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDrawerToggle.isDrawerIndicatorEnabled()){
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                    }
                    final FormsView formsView = new FormsView(MainActivity.this);
                    LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
                    LinearLayout subcategoryForms = (LinearLayout) formsView.findViewById(R.id.subcategoryForms);
                    CategoryCardView b = (CategoryCardView)v;
                    List<Form> form = Form.getAllFormsByCategory(b.getCategoryName().getText().toString());
                    subcategoryForms.removeAllViews();
                    Log.e("Size: ", ""+Form.countTable());
                    linearLayoutForms.removeAllViews();
                    int count = 0;
                    for (final Form f : form) {
                        count++;
                        final Button buttonForm = new Button(MainActivity.this);
                        if(count % 2 == 1){
                            buttonForm.setBackgroundColor(context.getResources().getColor(R.color.yokohama_button_light));
                        }
                        else{
                            buttonForm.setBackgroundColor(context.getResources().getColor(R.color.yokohama_button_dark));
                        }

                        buttonForm.setTextColor(context.getResources().getColor(R.color.white));
                        buttonForm.setText(f.getName());
                        buttonForm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentFormURL = f.getUrl();
                                formNameString = buttonForm.getText().toString();
                                FormBase.FORM = Form.getFormByName(formNameString).getUrl().split("/")[Form.getFormByName(formNameString).getUrl().split("/").length-1];
                                formView = (FormView) findViewById(R.id.formView);
                                final TextView formLabel = (TextView) formView.findViewById(R.id.formLabel);
                                formLabel.setText(formNameString);
                                final List<Answers> answersList = Answers.getFormsByFormName(formNameString);
                                upload = (ImageView) toolbar.findViewById(R.id.upload);
                                upload.setVisibility(View.VISIBLE);
                                upload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        displayDraftAnswers(answersList, formNameString);
                                    }
                                });
                                final LinearLayout retrieveForms = (LinearLayout) formView.findViewById(R.id.retrieveForms);
                                final FloatingActionButton addButton = (FloatingActionButton) formView.findViewById(R.id.addButton);
                                TextView note = (TextView) formView.findViewById(R.id.note);
                                addButton.setLayoutParams(layoutParams);
                                TextView retrieveFormLabel = (TextView) formView.findViewById(R.id.retrieveFormLabel);
                                previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                                previousForms.removeAllViews();
                                previousCategory = f.getCategory();
                                for (Answers answers : answersList) {
                                    final PreviousView previousView = new PreviousView(MainActivity.this);
                                    previousView.getName().setText(answers.getLocal_id());
                                    previousView.getState().setText(answers.getState());
                                    previousView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("Click", "Click");
                                            upload.setVisibility(View.GONE);
                                            PreviousView prev = (PreviousView) v;
                                            String name = prev.getName().getText().toString();
                                            Answers fo = Answers.getFormByLocal_ID(name);
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            Log.e("Prev Content", fo.getContent());
                                            createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo, formNameString, name);
                                            formView.setVisibility(View.GONE);
                                            FormBase.viewDeque.addLast(formView);
                                            questionsLayout.setVisibility(View.VISIBLE);
                                            labelString = formNameString;
                                            current = questionsLayout;
                                        }
                                    });
                                    previousForms.addView(previousView);
                                    previousForms.setVisibility(View.VISIBLE);
                                }
                                addButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText input = new EditText(MainActivity.this);
                                        input.setHint("Form name");
                                        new AlertDialogWrapper.Builder(MainActivity.this)
                                                .setTitle("Enter form name")
                                                .setView(input)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        upload.setVisibility(View.GONE);
                                                        String value = input.getText().toString();
                                                        Log.e("FormName String", formNameString);
                                                        Form fo = Form.getFormByName(formNameString);
                                                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                                        createAllQuestions(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo, value, formNameString);
                                                        formView.setVisibility(View.GONE);
                                                        FormBase.viewDeque.addLast(formView);
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
                                Log.e("BIN", FormBase.BIN);
                                if (FormBase.BIN.equals("")) {
                                    if(!Boolean.parseBoolean(CurrentUser.getCurrentUser().getIs_manager())){
                                        final RadioGroup projectRadioGroup = new RadioGroup(MainActivity.this);
                                        for(Projects p : Projects.getAllProjects()){
                                            RadioButton radioButton = new RadioButton(MainActivity.this);
                                            radioButton.setText(p.getName());
                                            projectRadioGroup.addView(radioButton);
                                        }
                                        new AlertDialogWrapper.Builder(MainActivity.this)
                                                .setCancelable(false)
                                                .setTitle("Please select a submission bin")
                                                .setView(projectRadioGroup)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        RadioButton radioButton = (RadioButton)projectRadioGroup.findViewById(projectRadioGroup.getCheckedRadioButtonId());
                                                        String url = Projects.getProjectURLByName(radioButton.getText().toString());
                                                        FormBase.BIN = url.split("/")[url.split("/").length-1];
                                                        FormBase.SUBMISSION_BIN = DataCenter.GLOBAL_URL+"bins/"+FormBase.BIN+"/";
                                                        Log.e("URL retrieve", url);
                                                        Log.e("Contains", ""+FormBase.buttonMap.containsKey(url));
                                                        Button b = FormBase.buttonMap.get(url);
                                                        b.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black_lighter));
                                                        currentButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                                                        currentButton = b;
                                                        if(isNetworkAvailable()){
                                                            new GetAnswers(formView, formNameString).execute();
                                                            addButton.setVisibility(View.VISIBLE);
                                                        }
                                                        else{
                                                            Toast.makeText(MainActivity.this, "Please connect to the internet to retrieve answers", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                backView(current, FormBase.viewDeque.removeLast());
                                            }
                                        }).show();
                                    }
                                } else if (!Boolean.parseBoolean(Form.getFormByName(formNameString).getStarting())) {
                                    addButton.setVisibility(View.GONE);
                                    new GetAnswers(formView, formNameString).execute();
                                } else {
                                    addButton.setVisibility(View.VISIBLE);
                                    new GetAnswers(formView, formNameString).execute();
                                    note.setVisibility(View.GONE);
                                    retrieveForms.setVisibility(View.GONE);
                                    retrieveFormLabel.setText("Submitted Forms");
                                }
                                formsViewContainer.setVisibility(View.GONE);
                                FormBase.viewDeque.addLast(formsViewContainer);
                                formView.setVisibility(View.VISIBLE);
                                current = formView;
                            }
                        });
                        linearLayoutForms.addView(buttonForm);
                    }
                    categoryView.setVisibility(View.GONE);
                    FormBase.viewDeque.addLast(categoryView);
                    back.setVisibility(View.VISIBLE);
                    formsViewContainer.removeAllViews();
                    formsViewContainer.addView(formsView);
                    formsViewContainer.setVisibility(View.VISIBLE);
                    current = formsViewContainer;
                }
            });
                layoutCategories.addView(categoryCardView);
        }
        for (final Category category : categories) {
            final CategoryCardView categoryCardView = new CategoryCardView(MainActivity.this);
            categoryCardView.getCategoryName().setText(category.getName());
            categoryCardView.getFormName().setText(Form.getAllFormsByCategory(category.getName()).size()+" "+"Forms");
            categoryCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDrawerToggle.isDrawerIndicatorEnabled()){
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                    }
                    current = createFormsView(v, categoryCardView, formsViewContainer);
                    FormBase.viewDeque.addLast(categoryView);
                }
            });
            Log.e("Parent", "Parent"+category.getParent());
            if(category.getParent().equals("")){
                layoutCategories.addView(categoryCardView);
            }
        }
        categoryView.setVisibility(View.VISIBLE);
    }

    public LinearLayout createFormsView(final View v, final CategoryCardView categoryCardView, final LinearLayout formsViewContainer){
        final FormsView formsView = new FormsView(MainActivity.this);
        LinearLayout linearLayoutForms = (LinearLayout) formsView.findViewById(R.id.layoutForms);
        LinearLayout subcategoryForms = (LinearLayout) formsView.findViewById(R.id.subcategoryForms);
        CategoryCardView b = (CategoryCardView)v;
        List<Form> form = Form.getAllFormsByCategory(b.getCategoryName().getText().toString());
        Log.e("Button", b.getCategoryName().getText().toString());
        Category cat = Category.getURLByCategoryName(b.getCategoryName().getText().toString());
        Log.e("Category Parent", cat.getUrl());
        List<Category> child_categories = Category.getAllCategoryByURL(cat.getUrl());
        subcategoryForms.removeAllViews();
        for(final Category c : child_categories){
            final CategoryCardView categoryCardView1 = new CategoryCardView(MainActivity.this);
            categoryCardView1.getCategoryName().setText(c.getName());
            categoryCardView1.getFormName().setText(Form.getAllFormsByCategory(c.getName()).size() + " " + "Forms");
            categoryCardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDrawerToggle.isDrawerIndicatorEnabled()){
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                    }
                    Log.e("Instance", ""+(formsViewContainer.getChildAt(0) instanceof FormsView));
                    Log.e("Before Size", ""+FormBase.viewDeque.size());
                    FormBase.viewDeque.addLast((FormsView)formsViewContainer.getChildAt(0));
                    Log.e("After Size", ""+FormBase.viewDeque.size());
                    current = createFormsView(v, categoryCardView1, formsViewContainer);
                }
            });
            subcategoryForms.addView(categoryCardView1);
        }
        Log.e("Size: ", ""+Form.countTable());
        linearLayoutForms.removeAllViews();
        int count = 0;
        for (final Form f : form) {
            count++;
            final Button buttonForm = new Button(MainActivity.this);
            if(count % 2 == 1){
                Log.e("Count", ""+count);
                Log.e("Black", "Black");
                buttonForm.setBackgroundColor(context.getResources().getColor(R.color.yokohama_button_light));
            }
            else{
                Log.e("Count", ""+count);
                Log.e("Black", "Not Black");
                buttonForm.setBackgroundColor(context.getResources().getColor(R.color.yokohama_button_dark));
            }
            buttonForm.setText(f.getName());
            buttonForm.setTextColor(context.getResources().getColor(R.color.white));
            buttonForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFormURL=f.getUrl();
                    final String formNameString = buttonForm.getText().toString();
                    FormBase.FORM = Form.getFormByName(formNameString).getUrl().split("/")[Form.getFormByName(formNameString).getUrl().split("/").length-1];
                    Log.e("Form", FormBase.FORM);
                    formView = (FormView)findViewById(R.id.formView);
                    final List<Answers> answersList = Answers.getFormsByFormName(formNameString);
                    upload = (ImageView) toolbar.findViewById(R.id.upload);
                    upload.setVisibility(View.VISIBLE);
                    upload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDraftAnswers(answersList, formNameString);
                        }
                    });
                    final TextView formLabel = (TextView)formView.findViewById(R.id.formLabel);
                    formLabel.setText(formNameString);
                    TextView retrieveFormLabel = (TextView)formView.findViewById(R.id.retrieveFormLabel);
                    final LinearLayout retrieveForms = (LinearLayout) formView.findViewById(R.id.retrieveForms);
                    final FloatingActionButton addButton = (FloatingActionButton) formView.findViewById(R.id.addButton);
                    TextView note = (TextView) formView.findViewById(R.id.note);
                    previousForms = (LinearLayout) formView.findViewById(R.id.previousForms);
                    previousForms.removeAllViews();
                    previousCategory = f.getCategory();
                    for(Answers answers : answersList){
                        Log.e("Im in", "PreviousView");
                        final PreviousView previousView = new PreviousView(MainActivity.this);
                        Log.e("Local Id", answers.getLocal_id());
                        previousView.getName().setText(answers.getLocal_id());
                        previousView.getState().setText(answers.getState());
                        previousView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("Click", "Click");
                                upload.setVisibility(View.GONE);
                                PreviousView prev = (PreviousView)v;
                                String name = prev.getName().getText().toString();
                                Answers fo = Answers.getFormByLocal_ID(name);
                                com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                Log.e("Prev Content", fo.getContent());
                                createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo, formNameString, name);
                                formView.setVisibility(View.GONE);
                                FormBase.viewDeque.addLast(formView);
                                questionsLayout.setVisibility(View.VISIBLE);
                                labelString = formNameString;
                                current = questionsLayout;
                            }
                        });
                        previousForms.addView(previousView);
                        previousForms.setVisibility(View.VISIBLE);
                    }
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText input = new EditText(MainActivity.this);
                            input.setHint("Form name");
                            new AlertDialogWrapper.Builder(MainActivity.this)
                                    .setTitle("Enter form name")
                                    .setView(input)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            upload.setVisibility(View.GONE);
                                            String value = input.getText().toString();
                                            Form fo = Form.getFormByName(formNameString);
                                            Log.e("FormName String", formNameString);
                                            com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                                            createAllQuestions(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(),fo,value, formNameString);
                                            formView.setVisibility(View.GONE);
                                            FormBase.viewDeque.addLast(formView);
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
                    Log.e("Starting", ""+Boolean.parseBoolean(Form.getFormByName(formNameString).getStarting()));
                    if(FormBase.BIN.equals("")){
                            final RadioGroup projectRadioGroup = new RadioGroup(MainActivity.this);
                            for(Projects p : Projects.getAllProjects()){
                                RadioButton radioButton = new RadioButton(MainActivity.this);
                                radioButton.setText(p.getName());
                                projectRadioGroup.addView(radioButton);
                            }
                            new AlertDialogWrapper.Builder(MainActivity.this)
                                    .setCancelable(false)
                                    .setTitle("Please select a submission bin")
                                    .setView(projectRadioGroup)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            RadioButton radioButton = (RadioButton)projectRadioGroup.findViewById(projectRadioGroup.getCheckedRadioButtonId());
                                            String url = Projects.getProjectURLByName(radioButton.getText().toString());
                                            FormBase.BIN = url.split("/")[url.split("/").length-1];
                                            FormBase.SUBMISSION_BIN = DataCenter.GLOBAL_URL+"bins/"+FormBase.BIN+"/";
                                            Log.e("URL retrieve", url);
                                            Log.e("Contains", ""+FormBase.buttonMap.containsKey(url));
                                            Button b = FormBase.buttonMap.get(url);
                                            b.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black_lighter));
                                            currentButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                                            currentButton = b;
                                            if(isNetworkAvailable()){
                                                new GetAnswers(formView, formNameString).execute();
                                                if(Boolean.parseBoolean(CurrentUser.getCurrentUser().getIs_manager())){
                                                    addButton.setVisibility(View.GONE);
                                                }
                                                else{
                                                    addButton.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Please connect to the internet to retrieve answers", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    backView(current, FormBase.viewDeque.removeLast());
                                }
                            }).show();
                    }
                    else if(!Boolean.parseBoolean(Form.getFormByName(formNameString).getStarting())){
                        Log.e("Starting", "Starting");
                        new GetAnswers(formView, formNameString).execute();
                        addButton.setVisibility(View.GONE);
                    }
                    else{
                        addButton.setVisibility(View.VISIBLE);
                        note.setVisibility(View.GONE);
                        retrieveForms.setVisibility(View.GONE);
                        retrieveFormLabel.setText("Submitted Forms");
                        new GetAnswers(formView, formNameString).execute();
                    }
                    formsViewContainer.setVisibility(View.GONE);
                    FormBase.viewDeque.addLast(formsViewContainer);
                    formView.setVisibility(View.VISIBLE);
                    current = formView;
                }
            });
            linearLayoutForms.addView(buttonForm);
        }
        categoryView.setVisibility(View.GONE);
        if(Category.getParentCategoryByName(categoryCardView.getCategoryName().getText().toString()).equals("")){
        }
        back.setVisibility(View.VISIBLE);
        formsViewContainer.removeAllViews();
        formsViewContainer.addView(formsView);
        formsViewContainer.setVisibility(View.VISIBLE);
        return  formsViewContainer;
    }
    public void updatePreviousButton(LinearLayout previous){
        if(!previous.equals(null) && formNameString != null){
            Log.e("Updating the Buttons", "Updating the buttons");
            previous.removeAllViews();
            for(Answers answers : Answers.getFormsByFormName(formNameString)){
                final PreviousView previousView = new PreviousView(MainActivity.this);
                previousView.getName().setText(answers.getLocal_id());
                previousView.getState().setText(answers.getState());
                previousView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Click", "Click");
                        PreviousView prev = (PreviousView)v;
                        String name = prev.getName().getText().toString();
                        Answers fo = Answers.getFormByLocal_ID(name);
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                        Log.e("Prev Content", fo.getContent());
                        createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo, formNameString, name);
                        formView.setVisibility(View.GONE);
                        FormBase.viewDeque.addLast(formView);
                        questionsLayout.setVisibility(View.VISIBLE);
                        labelString = formNameString;
                        current = questionsLayout;
                    }
                });
                previousForms.addView(previousView);
                previousForms.setVisibility(View.VISIBLE);
            }
        }

    }

    public void updateButtonAfterSubmission(LinearLayout previous, final String formNameString){
        if(!previous.equals(null)){
            Log.e("Updating the Buttons", "Updating the buttons");
            previous.removeAllViews();
            for(Answers answers : Answers.getFormsByFormName(formNameString)){
                final PreviousView previousView = new PreviousView(MainActivity.this);
                previousView.getName().setText(answers.getLocal_id());
                previousView.getState().setText(answers.getState());
                previousView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Click", "Click");
                        PreviousView prev = (PreviousView)v;
                        String name = prev.getName().getText().toString();
                        Answers fo = Answers.getFormByLocal_ID(name);
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(fo.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                        Log.e("Prev Content", fo.getContent());
                        createPreviousForm(form, questionsLayout, innerQuestionsLayout, original, fo.getContent(), fo, formNameString, name);
                        formView.setVisibility(View.GONE);
                        FormBase.viewDeque.addLast(formView);
                        questionsLayout.setVisibility(View.VISIBLE);
                        labelString = formNameString;
                        current = questionsLayout;
                    }
                });
                previousForms.addView(previousView);
                previousForms.setVisibility(View.VISIBLE);
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
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/FormBase/Pictures");
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
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
            entityBuilder.addTextBody("answer", answerURL);
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("name", name);
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("url_height", "1");
            entityBuilder.setBoundary(boundary);
            entityBuilder.addTextBody("url_width", "1");
            entityBuilder.setBoundary(boundary);
            if(file != null)
            {
                entityBuilder.addBinaryBody("photo", file, ContentType.create("image/jpg"),name);
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
            Log.e("result", result);
            return result;

        }
        catch(Exception e)
        {
            //Toast.makeText(MainActivity.this, "Photo submission failed please check your network connection", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        Log.e("Result", result);
        return result;
    }

    public QuestionGroupView createRepeaterQuestion(List childList,final LinearLayout questionsLayoutRepeater, String questionNameString){
        final QuestionGroupView questions = new QuestionGroupView(MainActivity.this);
        ImageView expand = (ImageView) questions.findViewById(R.id.expand);
        expand.setVisibility(View.GONE);
        questions.setId(DataCenter.generateViewId());
        DataCenter.repeaterIdList.add(questions.getId());
        DataCenter.repeaterQuestionNameList.add(questionNameString);
        DataCenter.questionListMap.put(questionNameString, childList);
        TextView questionName = (TextView)questions.findViewById(R.id.questionName);
        questionName.setVisibility(View.GONE);
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
                FloatingActionButton addInner = (FloatingActionButton)questionGroupChild.findViewById(R.id.add_new);
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
                repeaterQuestions.addView(createQuestionDateField(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(imageField)){
                repeaterQuestions.addView(createQuestionImageFieldView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(switchQuestion)){
                repeaterQuestions.addView(createQuestionSwitchView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(numberField)){
                repeaterQuestions.addView(createQuestionNumberFieldView(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(multipleChoice)){
                Log.e("Repeater", "Multiple Choice");
                List choices = (List)child.get("choices");
                repeaterQuestions.addView(createQuestionMultipleChoiceView(child.get("typeName").toString(), child.get("name").toString(), choices, true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(basicTextField)){
                repeaterQuestions.addView(createQuestionBasicTextField(child.get("typeName").toString(), child.get("name").toString(), true, String.valueOf(questions.getId()), false, ""));
            }
            else if(child.get("typeName").equals(checkList)){
                List choices = (List)child.get("choices");
                repeaterQuestions.addView(createQuestionCheckListView(child.get("typeName").toString(), child.get("name").toString(), choices, true, String.valueOf(questions.getId()), false, ""));
            }


        }
        return questions;
    }

    protected String getAnswersTrue()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            if(FormBase.BIN == null || FormBase.BIN.equals("")){
                url = new URL(DataCenter.GLOBAL_URL+"answers/?bin=ALL&fb="+FormBase.FORM+"&editing=True");
                Log.e("Answer URL", DataCenter.GLOBAL_URL+"answers/?bin=ALL&fb="+FormBase.FORM+"&editing=True");
            }
            else{
                url = new URL(DataCenter.GLOBAL_URL+"answers/?bin="+FormBase.BIN+"&fb="+FormBase.FORM+"&editing=True");
                Log.e("Answer URL", DataCenter.GLOBAL_URL+"answers/?bin="+FormBase.BIN+"&fb="+FormBase.FORM+"&editing=True");
            }
            connection = (HttpURLConnection) url.openConnection();
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
            AnswersForApproval.insertData(jsonAnswerWrapper.getJsonAnswerList(), "true");
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

    protected String getAnswersFalse()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            if(FormBase.BIN == null || FormBase.BIN.equals("")){
                url = new URL(DataCenter.GLOBAL_URL+"answers/?bin=ALL&fb="+FormBase.FORM+"&editing=False");
                Log.e("Answer URL", DataCenter.GLOBAL_URL+"answers/?bin=ALL&fb="+FormBase.FORM+"&editing=False");
            }
            else{
                url = new URL(DataCenter.GLOBAL_URL+"answers/?bin="+FormBase.BIN+"&fb="+FormBase.FORM+"&editing=False");
                Log.e("Answer URL", DataCenter.GLOBAL_URL+"answers/?bin="+FormBase.BIN+"&fb="+FormBase.FORM+"&editing=False");
            }
            connection = (HttpURLConnection) url.openConnection();
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
            AnswersForApproval.insertData(jsonAnswerWrapper.getJsonAnswerList(), "false");
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
        FormView formView;
        ProgressDialog dialog;
        String labelString;

        public GetAnswers(FormView formView, String labelString){
            this.formView = formView;
            this.labelString = labelString;
        }
        @Override
        protected String doInBackground(String... params) {
            Log.e("Hello", "Getting answers");
            getAnswersTrue();
            getAnswersFalse();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            LinearLayout retrieveForms = (LinearLayout)formView.findViewById(R.id.retrieveForms);
            retrieveForms.removeAllViews();
            for(final AnswersForApproval answersForApproval : AnswersForApproval.getAllAnswersForApproval()){
                final AnswersView answersView = new AnswersView(MainActivity.this);
                answersView.getName().setText(answersForApproval.getName());
                answersView.getSubmitted_by().setText(answersForApproval.getCreated_by());
                answersView.getState().setText(answersForApproval.getState());
                String date = answersForApproval.getDate_created();
                answersView.getDate().setText(date);
                answersView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnswersView a = (AnswersView) v;
                        String status = answersForApproval.getState().split("\\d")[answersForApproval.getState().split("\\d").length -1].trim();
                        Log.e("status", status);
                        final AnswersForApproval afa = AnswersForApproval.getAnswersForApprovalByCreatedBy(a.getSubmitted_by().getText().toString(), a.getDate().getText().toString());
                        Log.e("Prev Content", afa.getAnswer());
                        Log.e("URL", afa.getUrl());
                        DataCenter.DOWNLOAD_ID = afa.getUrl().split("/")[afa.getUrl().split("/").length-1];
                        DataCenter.ANSWER_URL = afa.getUrl();
                        download.setVisibility(View.VISIBLE);
                        sendEmail.setVisibility(View.VISIBLE);
                        com.dilimanlabs.formbase.objects.Form form = gson.fromJson(afa.getAnswer(), com.dilimanlabs.formbase.objects.Form.class);
                        createPreviousFormForApproval(form, questionsLayout, afa.getUrl(), afa.getEditing(), status, formView);
                        formView.setVisibility(View.GONE);
                        FormBase.viewDeque.addLast(formView);
                        questionsLayout.setVisibility(View.VISIBLE);
                        Log.e("Visibility", ""+questionsLayout.getVisibility());
                        current = questionsLayout;
                    }
                });
                retrieveForms.addView(answersView);
            }
            retrieveForms.setVisibility(View.VISIBLE);
            Log.e("Number", ""+AnswersForApproval.getAllAnswersForApproval().size());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Retrieving data...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
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
            Log.e("URL", url);
            Log.e("get Answers token", accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Token " +accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("PUT");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(data);
            request.flush();
            request.close();
            Log.e("Error", "E");
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
        LinearLayout current;
        FormView formView;
        ProgressDialog dialog;

        private SendApproveAnswer(String url, LinearLayout current, FormView formView) {
            this.url = url;
            this.current = current;
            this.formView = formView;
        }

        @Override
        protected String doInBackground(String... params) {
            sendApproveAnswer(url);
            getAnswersTrue();
            getAnswersFalse();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            backView(current, FormBase.viewDeque.removeLast());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Sending answer...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public void createSubmissionBins(){
        projects.removeAllViews();
        for(Projects p : Projects.getAllProjects()){
            final Button allProjects = new Button(this);
            if(p.getName().equals("All")){
                allProjects.setText(p.getName());
                allProjects.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                allProjects.setLayoutParams(layoutParams);
                allProjects.setTextColor(context.getResources().getColor(R.color.white));
                allProjects.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null != upload){
                            upload.setVisibility(View.GONE);
                        }
                        mDrawerToggle.setDrawerIndicatorEnabled(true);
                        download.setVisibility(View.GONE);
                        sendEmail.setVisibility(View.GONE);
                        FormBase.BIN = "";
                        FormBase.SUBMISSION_BIN = DataCenter.GLOBAL_URL+"bins/"+FormBase.BIN+"/";
                        backToCategoryView();
                        label.setText(allProjects.getText().toString());
                        Log.e("URL is", FormBase.BIN);
                        currentButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                        allProjects.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black_lighter));
                        currentButton = allProjects;
                        if(formView != null && formView.getVisibility() != View.GONE){
                            Log.e("All", "Getting answer");
                            if(isNetworkAvailable()){
                                new GetAnswers(formView, label.getText().toString()).execute();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please connect to the internet to retrieve answers", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
                projects.addView(allProjects);
            }
            else{
                Button button = new Button(this);
                FormBase.buttonStringMap.put(button, p.getUrl());
                Log.e("URL", p.getUrl());
                FormBase.buttonMap.put(p.getUrl(), button);
                FormBase.buttonList.add(button);
                button.setLayoutParams(layoutParams);
                button.setText(p.getName());
                button.setTextColor(context.getResources().getColor(R.color.white));
//                if(p.getName().equals(FormBase.currentButtonText)){
//                    Log.e("Equals", "Equals");
//                    button.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black_lighter));
//                    allProjects.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
//                    currentButton = button;
//                }
//                else{
//                    button.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
//                }
                button.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null != upload){
                            upload.setVisibility(View.GONE);
                        }
                        mDrawerToggle.setDrawerIndicatorEnabled(true);
                        download.setVisibility(View.GONE);
                        sendEmail.setVisibility(View.GONE);
                        Button b = (Button)v;
                        Log.e("Label", ""+b.getText().toString());
                        label.setText(b.getText().toString());
                        FormBase.BIN = FormBase.buttonStringMap.get(b).split("/")[FormBase.buttonStringMap.get(b).split("/").length-1];
                        FormBase.SUBMISSION_BIN = DataCenter.GLOBAL_URL+"bins/"+FormBase.BIN+"/";
                        backToCategoryView();
                        Log.e("URL is", FormBase.BIN);
                        if(!currentButton.equals(b)){
                            Log.e("Not Equal", "Not Equal");
                            b.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black_lighter));
                            currentButton.setBackgroundColor(context.getResources().getColor(R.color.yokohama_black));
                        }
                        currentButton = b;
                        if(formView != null && formView.getVisibility() != View.GONE){
                            Log.e("Project", "Getting answer");
                            if(isNetworkAvailable()){
                                new GetAnswers(formView, label.getText().toString()).execute();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please connect to the internet to retrieve answers", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                projects.addView(button);
            }
        }
    }

    public void backToCategoryView(){
        FormBase.viewDeque.clear();
        retrieveAllData(questionsLayout, innerQuestionsLayout, original);
        if(null != formView && formView.getVisibility() == View.VISIBLE){
            formView.setVisibility(View.GONE);
        }
        mDrawerLayout.closeDrawers();
        back.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static void downloadFile(String url) {
        try {
            URL u = new URL(url);
            URLConnection urlConnection = u.openConnection();
            String [] content = urlConnection.getHeaderField("Content-Disposition").split("=");
            String fileName = content[content.length-1];
            Log.e("filename", fileName);
            StringBuilder sb = new StringBuilder(fileName);
            String name = sb.toString();
            Log.e("Name", name);
            u.getFile();
            InputStream is = u.openStream();
            DataInputStream dis = new DataInputStream(is);
            Log.e("dis", dis.toString());
            byte[] buffer = new byte[4096];
            int length;
            File directory = new File(Environment.getExternalStorageDirectory() +"/FormBase/Documents");
            if(!directory.exists()){
                directory.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() +"/FormBase/Documents/"  + name));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        private String url;

        public DownloadFile(String url){
            this.url = url;
        }
        @Override
        protected String doInBackground(String... params) {
            downloadFile(url);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Downloaded successfully downloaded check the FormBase folder on your drive to see the file.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Downloading data...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public Bitmap decodeSampledBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    protected String sendEmail(String recipients, String answer)
    {
        HttpURLConnection connection;
        JsonAnswer ja = null;
        InputStream inputStream;
        InputStreamReader isr;
        OutputStreamWriter request = null;
        Email email = new Email();
        email.setRecipients(recipients);
        email.setAnswer(answer);
        String data = gson.toJson(email);
        Log.e("Json: ", data);
        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"sent_emails/");
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
            Log.e("request code", "" + connection.getResponseCode());
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
            else if(connection.getResponseCode() == 500){
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

    private class SendEmail extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        private String recipients;
        private String answer;

        public SendEmail(String recipients, String answer){
            this.recipients = recipients;
            this.answer = answer;
        }
        @Override
        protected String doInBackground(String... params) {
            sendEmail(recipients, answer);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Email sent successfully.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Sending email...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public boolean isAllRadioButtonChecked(Map<String, RadioGroup> map){
        for (Map.Entry<String, RadioGroup> entry : map.entrySet())
        {
            if (entry.getValue().getCheckedRadioButtonId() == -1)
            {
                entry.getValue().requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    public void displayDraftAnswers(List<Answers> answersList, final String formNameString){
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.removeAllViews();
        CheckBox selectAll = new CheckBox(MainActivity.this);
        selectAll.setText("Select All");
        linearLayout.addView(selectAll);
        final Map<String, CheckBox> stringCheckBoxMap = new HashMap<>();
        Log.e("Size", ""+answersList.size());
        for(Answers answers : answersList){
            CheckBox checkBox = new CheckBox(MainActivity.this);
            checkBox.setText(answers.getLocal_id());
            stringCheckBoxMap.put(answers.getLocal_id(), checkBox);
            linearLayout.addView(checkBox);
        }
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkAllForms(stringCheckBoxMap);
                }
                else{
                    unCheckAllForms(stringCheckBoxMap);
                }
            }
        });
        new AlertDialogWrapper.Builder(MainActivity.this)
                .setTitle("Select forms to be submitted")
                .setView(linearLayout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(isNetworkAvailable()){
                            new SendMultipleAnswer(formNameString, stringCheckBoxMap).execute();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please connect the device to the internet before submitting answers.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public void checkAllForms(Map<String, CheckBox> stringCheckBoxMap){
         for (Map.Entry<String, CheckBox> entry : stringCheckBoxMap.entrySet())
                {
                    entry.getValue().setChecked(true);
                }
    }

    public void unCheckAllForms(Map<String, CheckBox> stringCheckBoxMap){
        for (Map.Entry<String, CheckBox> entry : stringCheckBoxMap.entrySet())
        {
            entry.getValue().setChecked(false);
        }
    }

    protected String sendMultipleAnswers(String form, String answer, String submission_bin, String name, Answers answers)
    {
        HttpURLConnection connection;
        JsonAnswer ja = null;
        InputStream inputStream;
        InputStreamReader isr;
        OutputStreamWriter request = null;
        JsonAnswer json = new JsonAnswer();
        json.setAnswer(answer);
        json.setFormbase(form);
        json.setName(name);
        Log.e("Bin", submission_bin);
        json.setSubmission_bin(submission_bin);
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
            else if(connection.getResponseCode() == 500){
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
                Answers.updateAnswer(answers, answer, ja.getUrl());
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
            Toast.makeText(MainActivity.this, "Answer sumbission failed please check your network connection", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e("Error", e.toString());

        }
        if(ja == null){
            return "";
        } else{
            return ja.getUrl();
        }

    }

    private class SendMultipleAnswer extends AsyncTask<String, Void, String> {
        String formNameString;
        Map<String, CheckBox> checkBoxMap;
        ProgressDialog dialog;

        public SendMultipleAnswer(String formNameString, Map<String, CheckBox> checkBoxMap){
            this.formNameString = formNameString;
            this.checkBoxMap = checkBoxMap;
        }
        @Override
        protected String doInBackground(String... params) {
            for (Map.Entry<String, CheckBox> entry : checkBoxMap.entrySet()) {
                if (entry.getValue().isChecked()) {
                    Answers answers = Answers.getFormByLocalId(entry.getKey());
                    com.dilimanlabs.formbase.objects.Form form = gson.fromJson(answers.getContent(), com.dilimanlabs.formbase.objects.Form.class);
                    insertPhotos(form);
                    String answerURL = sendMultipleAnswers(currentFormURL, answers.getContent(), FormBase.SUBMISSION_BIN, entry.getKey(), answers);
                    if(!DataCenter.imageTextViewMap.isEmpty()){
                        for(TextView textView : DataCenter.imageTextViewList){
                            File file = new File(textView.getText().toString());
                            String response = sendPhoto(answerURL, answerURL.split("/")[answerURL.split("/").length -1]+"_"+textView.getText().toString(), file);
                            Log.e("Response Photo", response);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new GetAnswers(formView, formNameString).execute();
            updateButtonAfterSubmission(previousForms, formNameString);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Sending answer...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void insertPhotos(com.dilimanlabs.formbase.objects.Form form){
        DataCenter.getImageTextViewMapPrevious.clear();
        DataCenter.imageTextViewList.clear();
        for(int i =0; i<form.getChildList().size(); i++){
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) form.getChildList().get(i);
            if(map.get("typeName").equals(questionGroup)){
                questionGroupView = new QuestionGroupView(this);
                final TextView questionName = (TextView)questionGroupView.findViewById(R.id.questionName);
                final LinearLayout questions = (LinearLayout)questionGroupView.findViewById(R.id.questions);
                ImageView expand = (ImageView) questionGroupView.findViewById(R.id.expand);
                expand.setVisibility(View.GONE);
                if(Boolean.parseBoolean(map.get("isRepeating").toString()) == true){
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("questionRepeaterList");
                    for(int x = 0; x<childList.size(); x++){
                        final LinkedTreeMap<String, Object> child = (LinkedTreeMap<String, Object>) childList.get(x);
                        List children = (List)child.get("repeaterQuestion");
                        Log.e("Repeater Question", child.get("repeaterQuestion").toString());
                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams((LinearLayout.LayoutParams.MATCH_PARENT),(LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout repeaterLayout = new LinearLayout(MainActivity.this);
                        repeaterLayout.setOrientation(LinearLayout.VERTICAL);
                        repeaterLayout.setLayoutParams(linearLayoutParams);
                        for(int y = 0; y<children.size(); y++){
                            final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) children.get(y);
                            if(c.get("typeName").equals(imageField)){
                                repeaterLayout.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                            }
                        }
                        questions.addView(repeaterLayout);
                    }
                }
                else{
                    questions.setVisibility(View.VISIBLE);
                    questionName.setText(map.get("name").toString());
                    Log.e("Question Type: ", (i + 1) + " " + map.get("typeName"));
                    Log.e("Name: ", " "+map.get("name"));
                    List childList = (List)map.get("childList");
                    for(int y = 0; y<childList.size(); y++){
                        final LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) childList.get(y);
                        if(c.get("typeName").equals(imageField)){
                            questions.addView(createQuestionImageFieldViewWithAnswer(c.get("typeName").toString(), c.get("name").toString(), c.get("answer").toString()));
                        }
                    }
                }
            }

            else if(map.get("typeName").equals(imageField)){
                createQuestionImageFieldViewWithAnswer(map.get("typeName").toString(), map.get("name").toString(), map.get("answer").toString());
            }
        }
    }

}
