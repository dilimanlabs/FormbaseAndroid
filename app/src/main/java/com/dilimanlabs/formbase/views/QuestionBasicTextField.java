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
public class QuestionBasicTextField extends CardView {
    private TextView mQuestionType;
    private TextView mQuestionName;
    private LinearLayout mQuestionAnswer;
    private static final int ID = 1;


    public QuestionBasicTextField(Context context){
        super(context);
        init();
    }

    public QuestionBasicTextField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionBasicTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_question_textfield, this);
        mQuestionType = (TextView) findViewById(R.id.questionType);
        mQuestionName = (TextView) findViewById(R.id.questionName);
        mQuestionAnswer = (LinearLayout) findViewById(R.id.questionAnswer);
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object object) {
        if ((object instanceof QuestionBasicTextField)){
            QuestionBasicTextField questionBasicTextField = (QuestionBasicTextField) object;
            if (questionBasicTextField.getID() == this.getID()){
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
