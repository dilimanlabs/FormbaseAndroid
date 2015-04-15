package com.dilimanlabs.formbase.objects;

import java.util.List;

/**
 * Created by user on 2/24/2015.
 */
public class Form {

    String formName;
    String description;
    String type;
    double level;
    List<Object> childList;
    List<Object> questionGroupRepeaterList;

    public Form(){
        super();
    }

    public Form(String formName, String description, String type, double level, List<Object> childList, List<Object> questionGroupRepeaterList){
        this.formName = formName;
        this.description = description;
        this.type = type;
        this.level = level;
        this.childList = childList;
        this.questionGroupRepeaterList = questionGroupRepeaterList;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public List<Object> getChildList() {
        return childList;
    }

    public void setChildList(List<Object> childList) {
        this.childList = childList;
    }

    public List<Object> getQuestionGroupRepeaterList() {
        return questionGroupRepeaterList;
    }

    public void setQuestionGroupRepeaterList(List<Object> questionGroupRepeaterList) {
        this.questionGroupRepeaterList = questionGroupRepeaterList;
    }

    //    @Override
//    public String toString()
//    {
//        return getFormName();
//    }


}
