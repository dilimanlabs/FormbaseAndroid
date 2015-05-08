package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 3/23/2015.
 */
public class QuestionDateFieldView extends CardView {
    private TextView mQuestionName;
    private TextView mDate;
    private int year;
    private int month;
    private int day;
    private Context context;
    private Date date;
    private static final int ID = 1;


    public QuestionDateFieldView(Context context){
        super(context);
        this.context = context;
        init();
    }

    public QuestionDateFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionDateFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_question_date_field, this);
        mQuestionName = (TextView) findViewById(R.id.questionName);
        mDate = (TextView) findViewById(R.id.date);
        mDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
    }



    private void showDate(int year, int month, int day) {
        mDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public int getID() {
        return ID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
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
