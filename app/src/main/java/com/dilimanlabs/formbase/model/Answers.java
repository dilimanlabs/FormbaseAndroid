package com.dilimanlabs.formbase.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by user on 4/8/2015.
 */
@Table(name = "Answers")
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

    @Column(name = "Local_ID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String local_id;

    @Column(name = "State")
    public String state;

    @Column(name = "Formbase")
    public String formbase;

    @Column(name = "Editing")
    public String editing;

    @Column(name = "Submission_Bin")
    public String submission_bin;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
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

    public String getFormbase() {
        return formbase;
    }

    public void setFormbase(String formbase) {
        this.formbase = formbase;
    }

    public String getEditing() {
        return editing;
    }

    public void setEditing(String editing) {
        this.editing = editing;
    }

    public String getSubmission_bin() {
        return submission_bin;
    }

    public void setSubmission_bin(String submission_bin) {
        this.submission_bin = submission_bin;
    }

    public static Answers getFormByLocal_ID(String name){
        Answers answers = new Select().from(Answers.class).where("Local_ID = ?", name).executeSingle();
        ActiveAndroid.getDatabase().close();
        return answers;
    }

    public static String getAnswerByURL(String url){
        Answers answers = new Select().from(Answers.class).where("URL =?", url).executeSingle();
        ActiveAndroid.getDatabase().close();
        return answers.getLocal_id();
    }

    public static void updateAnswer(Answers answers, String jsonAnswer, String answer_url){
        answers.setState("submitted");
        answers.setContent(jsonAnswer);
        answers.setUrl(answer_url);
        answers.save();
        ActiveAndroid.getDatabase().close();
    }

    public synchronized static boolean insertAnswer(Form fo, String formName, String content){
        Answers answers = new Answers();
        answers.setCategory(fo.getCategory());
        answers.setName(fo.getName());
        answers.setLocal_id(formName);
        answers.setContent(content);
        answers.setFormbase(fo.getUrl());
        answers.setState("draft");
        answers.setCreated_by(fo.getCreated_by());
        answers.setUrl(null);
        answers.save();
        ActiveAndroid.getDatabase().close();
        return true;
    }


    public static List<Answers> getAllForms(String category){
        List<Answers> answersList = new Select().from(Answers.class).where("Category = ?", category).execute();
        ActiveAndroid.getDatabase().close();
        return answersList;
    }

    public static List<Answers> getFormsByFormName(String name){
        List<Answers> answersList = new Select().from(Answers.class).where("Name = ? AND State = ?", name, "draft").execute();
        ActiveAndroid.getDatabase().close();
        return answersList;
    }

    public static void deleteData(){
        new Delete().from(Answers.class).execute();
        ActiveAndroid.getDatabase().close();
    }
}
