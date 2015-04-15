package com.dilimanlabs.formbase.objects;

/**
 * Created by user on 2/24/2015.
 */
public class QuestionNumberField {
    String type;
    String typeName;
    String name;
    double level;
    double order;
    String elName;
    boolean ranged;
    double minimum;
    double maximum;
    String answer;

    public QuestionNumberField(String type, String typeName, String name, double level, double order, String elName, boolean ranged, double minimum, double maximum, String answer) {
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.level = level;
        this.order = order;
        this.elName = elName;
        this.ranged = ranged;
        this.minimum = minimum;
        this.maximum = maximum;
        this.answer = answer;
    }

    public QuestionNumberField(){
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

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isRanged() {
        return ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
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
