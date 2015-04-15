package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 4/13/2015.
 */
public class FormsView extends LinearLayout {
    LinearLayout linearLayoutForms;
    public FormsView(Context context) {
        super(context);
        init();
    }

    public FormsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_forms, this);
        linearLayoutForms = (LinearLayout) findViewById(R.id.formsView);
    }
}
