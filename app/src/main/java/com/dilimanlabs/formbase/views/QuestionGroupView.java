package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 3/2/2015.
 */
public class QuestionGroupView extends CardView {
    private TextView mQuestionType;
    private TextView mQuestionName;
    private LinearLayout mLinearLayout;
    private final int ID = 3;

    public QuestionGroupView(Context context){
        super(context);
        init();
    }

    public QuestionGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_group_question, this);
        mQuestionType = (TextView) findViewById(R.id.questionType);
        mQuestionName = (TextView) findViewById(R.id.questionName);
        mLinearLayout = (LinearLayout) findViewById(R.id.questions);
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object object) {
        if ((object instanceof QuestionGroupView)){
            QuestionGroupView question = (QuestionGroupView) object;
            if (question.getID() == this.getID()){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }

    }
}
