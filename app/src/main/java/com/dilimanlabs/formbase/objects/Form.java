package com.dilimanlabs.formbase.objects;

import java.util.List;

/**
 * Created by user on 2/24/2015.
 */
public class Form {

    String name;
    String description;
    String type;
    double level;
    List<Object> childList;
    List<Object> questionGroupRepeaterListInner;
    List<Object> questionGroupRepeaterListOuter;

    public Form(){
        super();
    }

    public Form(String formName, String description, String type, double level, List<Object> childList, List<Object> questionGroupRepeaterListInner){
        this.name = formName;
        this.description = description;
        this.type = type;
        this.level = level;
        this.childList = childList;
        this.questionGroupRepeaterListInner = questionGroupRepeaterListInner;
    }

    public String getFormName() {
        return name;
    }

    public void setFormName(String name) {
        this.name = name;
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

    public List<Object> getQuestionGroupRepeaterListInner() {
        return questionGroupRepeaterListInner;
    }

    public void setQuestionGroupRepeaterListInner(List<Object> questionGroupRepeaterListInner) {
        this.questionGroupRepeaterListInner = questionGroupRepeaterListInner;
    }

    public List<Object> getQuestionGroupRepeaterListOuter() {
        return questionGroupRepeaterListOuter;
    }

    public void setQuestionGroupRepeaterListOuter(List<Object> questionGroupRepeaterListOuter) {
        this.questionGroupRepeaterListOuter = questionGroupRepeaterListOuter;
    }

    //    @Override
//    public String toString()
//    {
//        return getFormName();
//    }


}
