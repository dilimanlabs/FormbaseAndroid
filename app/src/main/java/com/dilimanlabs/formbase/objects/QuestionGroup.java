package com.dilimanlabs.formbase.objects;

import java.util.List;

/**
 * Created by user on 2/24/2015.
 */
public class QuestionGroup {

    String type;
    String typeName;
    String name;
    List<Object> childList;
    List<QuestionRepeater> questionRepeaterList;
    double level;
    double order;
    boolean isRepeating;
    String answer;
    public QuestionGroup(String type, String typeName, String name, List<Object> childList, double level, double order, boolean isRepeating, String answer){
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.childList = childList;
        this.level = level;
        this.order = order;
        this.isRepeating = isRepeating;
        this.answer = answer;
    }

    public QuestionGroup(){
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getChildList() {
        return childList;
    }

    public void setChildList(List<Object> childList) {
        this.childList = childList;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean isRepeating) {
        this.isRepeating = isRepeating;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<QuestionRepeater> getQuestionGroupRepeater() {
        return questionRepeaterList;
    }

    public void setQuestionGroupRepeaterList(List<QuestionRepeater> questionRepeaterList) {
        this.questionRepeaterList = questionRepeaterList;
    }

    @Override
    public String toString()
    {
        return getTypeName();
    }
}
