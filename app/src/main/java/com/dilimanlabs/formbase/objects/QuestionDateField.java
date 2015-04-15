package com.dilimanlabs.formbase.objects;

/**
 * Created by user on 3/23/2015.
 */
public class QuestionDateField {
    String type;
    String typeName;
    String name;
    double level;
    double order;
    String elName;
    String answer;

    public QuestionDateField(String type, String typeName, String name, double level, double order, String elName, String answer) {
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.level = level;
        this.order = order;
        this.elName = elName;
        this.answer = answer;
    }

    public QuestionDateField(){
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

    public String getElName() {
        return elName;
    }

    public void setElName(String elName) {
        this.elName = elName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString()
    {
        return getTypeName();
    }
}
