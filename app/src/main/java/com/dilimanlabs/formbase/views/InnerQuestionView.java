package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 3/17/2015.
 */
public class InnerQuestionView extends CardView {
    private LinearLayout innerQuestionsLayout;



    public InnerQuestionView(Context context){
        super(context);
        init();
    }

    public InnerQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InnerQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_inner_questions, this);
        innerQuestionsLayout = (LinearLayout) findViewById(R.id.innerQuestionsLayout);
    }
}
