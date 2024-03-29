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
public class QuestionMultipleChoiceView extends CardView {
    private TextView mQuestionName;
    private LinearLayout mQuestionChoices;
    private final int ID = 5;


    public QuestionMultipleChoiceView(Context context){
        super(context);
        init();
    }

    public QuestionMultipleChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionMultipleChoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_question_multiplechoice, this);
        mQuestionName = (TextView) findViewById(R.id.questionName);
        mQuestionChoices = (LinearLayout) findViewById(R.id.questionChoices);
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object object) {
        if ((object instanceof QuestionMultipleChoiceView)){
            QuestionMultipleChoiceView question = (QuestionMultipleChoiceView) object;
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
