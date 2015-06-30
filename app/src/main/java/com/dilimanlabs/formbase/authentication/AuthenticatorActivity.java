package com.dilimanlabs.formbase.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.dilimanlabs.formbase.DataCenter;
import com.dilimanlabs.formbase.R;
import com.dilimanlabs.formbase.model.Answers;
import com.dilimanlabs.formbase.model.AnswersForApproval;
import com.dilimanlabs.formbase.model.Category;
import com.dilimanlabs.formbase.model.CurrentUser;
import com.dilimanlabs.formbase.model.Form;
import com.dilimanlabs.formbase.model.Photos;
import com.dilimanlabs.formbase.model.Projects;
import com.dilimanlabs.formbase.objects.CategoryObject;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.CurrentUserWrapper;
import com.dilimanlabs.formbase.objects.FormObject;
import com.dilimanlabs.formbase.objects.FormObjectWrapper;
import com.dilimanlabs.formbase.objects.Project;
import com.dilimanlabs.formbase.objects.ProjectsWrapper;
import com.dilimanlabs.formbase.objects.UserInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 4/23/2015.
 */
public class AuthenticatorActivity extends ActionBarActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;
    private Button login;
    private EditText username;
    private EditText password;
    private Gson gson;
    private Bundle mResultBundle = null;
    private String authtoken;
    private String categories;
    private boolean categories_retrieved;
    private CategoryWrapper categoryWrapper;
    private CurrentUserWrapper currentUserWrapper;
    private FormObjectWrapper formObjectWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new Gson();
        login = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        mAccountType = getIntent().getStringExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                clearData();
                submit();
            }
        });
    }

    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    public void submit() {

        if(isNetworkAvailable()){
            final String userName = username.getText().toString();
            final String userPass = password.getText().toString();
            if(isUsernameAndPasswordNotEmptyAndNull(userName, userPass)){
                new AsyncTask<String, Void, Intent>() {
                    ProgressDialog dialog;

                    @Override
                    protected Intent doInBackground(String... params) {
                        Bundle data = new Bundle();
                        try {
                            authtoken =  new FormbaseServerAuthenticate().userSignIn(userName, userPass, mAuthTokenType);
                            if(authtoken.equals("400")){
                                Toast.makeText(AuthenticatorActivity.this, "Username and password does not match", Toast.LENGTH_LONG).show();
                            }
                            else{
                                data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                                data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                                data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                                data.putString(PARAM_USER_PASS, userPass);
                            }
                        } catch (Exception e) {
                            Log.e("Im login token", ""+authtoken);
                            e.printStackTrace();
                            Log.e("Connection Status", "Cant connect to server");
                            data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                        }
                        if(authtoken != null && !authtoken.equals("400")){
                            getCurrentUserInfo();
                            getAllProjects();
                            getCategory();
                            getFormsTrue();
                            getFormsFalse();
                        }
                        Log.e("done acquiring","");
                        final Intent res = new Intent();
                        res.putExtras(data);
                        Log.e("returning","");
                        return res;
                    }

                    @Override
                    protected void onPostExecute(Intent intent) {
                        if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                            login.setEnabled(true);
                            Toast.makeText(AuthenticatorActivity.this, "Username and password does not match", Toast.LENGTH_LONG).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Log.e("finishing","");
                            ActiveAndroid.getDatabase().close();
                            finishLogin(intent);
                        }
                    }
                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        dialog = new ProgressDialog(AuthenticatorActivity.this);
                        dialog.setMessage("Logging in...");
                        dialog.setIndeterminate(false);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                }.execute();
            }
            else{
                login.setEnabled(true);
                Toast.makeText(AuthenticatorActivity.this, "Username or Password cannot be empty.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            login.setEnabled(true);
            Toast.makeText(AuthenticatorActivity.this, "Please connect the device to the internet to login.", Toast.LENGTH_LONG).show();
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        DataCenter.isLogin = true;
        finish();
    }

    protected String getFormsTrue()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"formbases/?starting=True");
            Log.e("URL", DataCenter.GLOBAL_URL+"formbases/?starting=True");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("get Forms token", authtoken);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " +authtoken.trim());
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
            Log.e("Form True Count", ""+formObjectWrapper.getFormObjectList().size());
            for(FormObject formObject : formObjectWrapper.getFormObjectList()){
                Log.e("Form Content", formObject.getContent());
                String cat =
                        formObject.getCategory();
                Log.e("Category", ""+cat);
                if (cat==null){
                    cat = "Uncategorized";
                }
                else{
                    cat = Category.getCategoryNameByURL(cat);
                }

                Form.insertData(formObject.getUrl(), formObject.getCreated_by(), cat, formObject.getContent(), formObject.getName(), "true");
            }
            Log.e("Response", response);
            isr.close();
            reader.close();
            connection.disconnect();
            ActiveAndroid.getDatabase().close();
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

    protected String getFormsFalse()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"formbases/?starting=False");
            Log.e("URL", DataCenter.GLOBAL_URL+"formbases/?starting=False");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("get Forms token", authtoken);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " +authtoken.trim());
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
            Log.e("Form False Count", ""+formObjectWrapper.getFormObjectList().size());
            for(FormObject formObject : formObjectWrapper.getFormObjectList()){
                Log.e("Form Content", formObject.getContent());
                String cat =
                        formObject.getCategory();
                Log.e("Category", ""+cat);
                if (cat==null){
                    cat = "Uncategorized";
                }
                else{
                    cat = Category.getCategoryNameByURL(cat);
                }

                Form.insertData(formObject.getUrl(), formObject.getCreated_by(), cat, formObject.getContent(), formObject.getName(), "false");
            }
            Log.e("Response", response);
            isr.close();
            reader.close();
            connection.disconnect();
            ActiveAndroid.getDatabase().close();
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


    protected String getCategory()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"categories/?format=json");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("token", authtoken);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + authtoken);
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
                if(null == categoryObject.getParent()){
                    category.setParent("");
                }
                else{
                    category.setParent(categoryObject.getParent());
                }
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

    public void clearData(){
        Photos.deleteData();
        Answers.deleteData();
        AnswersForApproval.deleteData();
        Form.deleteData();
        Category.deleteData();
        CurrentUser.deleteData();

    }

    protected String getCurrentUserInfo()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"users/?current=True");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("token", authtoken);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + authtoken);
            Log.e(" header", connection.getRequestProperties().get("Authorization").toString());
            Log.e("Current User", ""+connection.getResponseCode());
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            response = "{\"userInfoList\":"+response + "}";
            Log.e("Current User Response", response);
            currentUserWrapper = gson.fromJson(response, CurrentUserWrapper.class);
            Log.e("Current User Info", ""+currentUserWrapper.getUserInfoList().size());
            for(UserInfo userInfo : currentUserWrapper.getUserInfoList()){
                if(username.getText().toString().equals(userInfo.getUsername())){
                    CurrentUser.insertCurrentUser(userInfo);
                }
            }
            Log.e("appended", response);
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

    protected String getAllProjects()
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;

        try
        {
            url = new URL(DataCenter.GLOBAL_URL+"bins/?format=json");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("token", authtoken);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + authtoken);
            Log.e(" header", connection.getRequestProperties().get("Authorization").toString());
            Log.e("Projects", ""+connection.getResponseCode());
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            response = "{\"projectList\":"+response + "}";
            ProjectsWrapper projectsWrapper = gson.fromJson(response, ProjectsWrapper.class);
            Projects pro = new Projects();
            pro.setUrl("");
            pro.setName("All");
            pro.setCreated_by("");
            pro.setOrganizational_group("");
            pro.save();
            for(Project project : projectsWrapper.getProjectList()){
                Projects projects = new Projects();
                projects.setUrl(project.getUrl());
                projects.setName(project.getName());
                projects.setCreated_by(project.getCreated_by());
                projects.setOrganizational_group(project.getOrganizational_group());
                projects.save();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isUsernameAndPasswordNotEmptyAndNull(String username, String password){
        if(null != username && null != password && !username.equals("") && !password.equals("")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
