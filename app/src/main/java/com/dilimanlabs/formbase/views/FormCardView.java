package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 5/29/2015.
 */
public class FormCardView extends CardView {

    private TextView formName;

    public FormCardView(Context context) {
        super(context);
        init();
    }

    public FormCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_form_card, this);
        formName = (TextView) findViewById(R.id.formName);
    }

    public TextView getFormName() {
        return formName;
    }
}
