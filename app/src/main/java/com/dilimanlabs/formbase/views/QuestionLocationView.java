package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 6/4/2015.
 */
public class QuestionLocationView extends CardView {

    private TextView questionName;
    private TextView latitude;
    private TextView longitude;
    private Button getLocation;
    private EditText nameOfEstablishment;
    private EditText address;
    private EditText contact;
    private EditText operatingHours;

    public QuestionLocationView(Context context) {
        super(context);
        init();
    }

    public QuestionLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionLocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_question_location, this);
        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        getLocation = (Button)findViewById(R.id.getLocation);
        questionName = (TextView)findViewById(R.id.questionName);
        nameOfEstablishment = (EditText)findViewById(R.id.name_of_establishment);
        address = (EditText)findViewById(R.id.address);
        contact = (EditText)findViewById(R.id.contact);
        operatingHours = (EditText)findViewById(R.id.operating_hours);
    }

    public TextView getLatitude() {
        return latitude;
    }

    public void setLatitude(TextView latitude) {
        this.latitude = latitude;
    }

    public TextView getLongitude() {
        return longitude;
    }

    public void setLongitude(TextView longitude) {
        this.longitude = longitude;
    }

    public Button getGetLocation() {
        return getLocation;
    }

    public void setGetLocation(Button getLocation) {
        this.getLocation = getLocation;
    }

    public TextView getQuestionName() {
        return questionName;
    }

    public void setQuestionName(TextView questionName) {
        this.questionName = questionName;
    }

    public EditText getNameOfEstablishment() {
        return nameOfEstablishment;
    }

    public void setNameOfEstablishment(EditText nameOfEstablishment) {
        this.nameOfEstablishment = nameOfEstablishment;
    }

    public EditText getAddress() {
        return address;
    }

    public void setAddress(EditText address) {
        this.address = address;
    }

    public EditText getContact() {
        return contact;
    }

    public void setContact(EditText contact) {
        this.contact = contact;
    }

    public EditText getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(EditText operatingHours) {
        this.operatingHours = operatingHours;
    }
}
