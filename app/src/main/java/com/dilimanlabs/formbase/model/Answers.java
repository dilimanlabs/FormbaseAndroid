package com.dilimanlabs.formbase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by user on 4/8/2015.
 */
@Table(name = "Forms")
public class Answers extends Model {

    @Column(name = "URL")
    public String url;

    @Column(name = "Created_By")
    public String created_by;

    @Column(name = "Category")
    public String category;

    @Column(name = "Content")
    public String content;

    @Column(name = "Name")
    public String name;

    @Column(name = "SubName")
    public String subName;

    @Column(name = "State")
    public String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Answers getFormBySubName(String name){
        return new Select().from(Answers.class).where("SubName = ?", name).executeSingle();
    }

    public static void updateAnswer(Answers answers, String jsonAnswer){
        answers.setState("submitted");
        answers.setContent(jsonAnswer);
        answers.save();
    }

    public synchronized static void insertAnswer(Form fo, String formName, String content){
        Answers answers = new Answers();
        answers.setCategory(fo.getCategory());
        answers.setName(fo.getName());
        answers.setSubName(formName);
        answers.setContent(content);
        answers.setState("draft");
        answers.setCreated_by(fo.getCreated_by());
        answers.setUrl(fo.getUrl());
        answers.save();
    }

    public static List<Answers> getAllForms(String category){
        return new Select().from(Answers.class).where("Category = ?", category).execute();
    }
}
