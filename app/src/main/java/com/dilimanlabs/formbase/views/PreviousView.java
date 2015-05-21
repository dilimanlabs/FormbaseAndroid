package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 5/19/2015.
 */
public class PreviousView extends CardView {

    private TextView name;
    private TextView state;

    public PreviousView(Context context) {
        super(context);
        init();
    }

    public PreviousView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviousView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_previous, this);
        name = (TextView) findViewById(R.id.name);
        state = (TextView) findViewById(R.id.state);
    }

    public TextView getName() {
        return name;
    }

    public TextView getState() {
        return state;
    }
}

