package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 5/18/2015.
 */
public class AnswersView extends CardView {

    private TextView submitted_by;
    private TextView state;
    private TextView date;

    public AnswersView(Context context) {
        super(context);
        init();
    }

    public AnswersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnswersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_answers, this);
        submitted_by = (TextView) findViewById(R.id.submitted_by);
        state = (TextView) findViewById(R.id.state);
        date = (TextView) findViewById(R.id.date);
    }

    public TextView getSubmitted_by() {
        return submitted_by;
    }

    public TextView getState() {
        return state;
    }

    public TextView getDate() {
        return date;
    }
}
