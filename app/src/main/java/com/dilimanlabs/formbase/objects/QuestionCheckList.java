package com.dilimanlabs.formbase.objects;

import java.util.List;

/**
 * Created by user on 2/24/2015.
 */
public class QuestionCheckList {
    String type;
    String typeName;
    String name;
    double level;
    double order;
    String elName;
    List<Choices> choices;
    List<Answers> answers;

    public QuestionCheckList(String type, String typeName, String name, double level, double order, String elName, List<Choices> choices, List<Answers> answers) {
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.level = level;
        this.order = order;
        this.elName = elName;
        this.choices = choices;
        this.answers = answers;
    }

    public QuestionCheckList(){
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

    public List<Choices> getChoices() {
        return choices;
    }

    public void setChoices(List<Choices> choices) {
        this.choices = choices;
    }

    public List<Answers> getAnswer() {
        return answers;
    }

    public void setAnswer(List<Answers> answers) {
        this.answers = answers;
    }

    @Override
    public String toString()
    {
        return getTypeName();
    }
}
