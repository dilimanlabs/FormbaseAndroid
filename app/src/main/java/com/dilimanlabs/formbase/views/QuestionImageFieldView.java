package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 3/2/2015.
 */
public class QuestionImageFieldView extends CardView {
    private TextView mQuestionName;
    private final int ID = 4;


    public QuestionImageFieldView(Context context){
        super(context);
        init();
    }

    public QuestionImageFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionImageFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_image_field, this);
        mQuestionName = (TextView) findViewById(R.id.questionName);
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object object) {
        if ((object instanceof QuestionImageFieldView)){
            QuestionImageFieldView question = (QuestionImageFieldView) object;
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
