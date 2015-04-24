package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 4/16/2015.
 */
public class FormView extends LinearLayout {
    LinearLayout linearLayoutForm;
    public FormView(Context context) {
        super(context);
        init();
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_form, this);
        linearLayoutForm = (LinearLayout) findViewById(R.id.formView);
    }
}
