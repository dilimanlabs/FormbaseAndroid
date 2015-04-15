package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 4/8/2015.
 */
public class LoginView extends ScrollView {

    private EditText username;
    private EditText password;
    private Button login;
    private Context context;

    public LoginView(Context context){
        super(context);
        this.context = context;
        init();
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_login, this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
    }
}
