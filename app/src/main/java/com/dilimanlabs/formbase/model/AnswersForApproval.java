package com.dilimanlabs.formbase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dilimanlabs.formbase.objects.JsonAnswer;

import java.util.List;

/**
 * Created by user on 5/5/2015.
 */
@Table(name = "AnswersForApproval")
public class AnswersForApproval extends Model {
    @Column(name="URL")
    String url;
    @Column(name="Date_Created")
    String date_created;
    @Column(name="Date_Modified")
    String date_modified;
    @Column(name="Answer")
    String answer;
    @Column(name="State")
    String state;
    @Column(name="Created_By")
    String created_by;
    @Column(name="Formbase")
    String formbase;
    @Column(name="Modified_By")
    String modified_by;
    @Column(name="Fulfilled")
    String fulfilled;
    @Column(name="Editing")
    String editing;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getFormbase() {
        return formbase;
    }

    public void setFormbase(String formbase) {
        this.formbase = formbase;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(String fulfilled) {
        this.fulfilled = fulfilled;
    }

    public String getEditing() {
        return editing;
    }

    public void setEditing(String editing) {
        this.editing = editing;
    }

    public static void insertData(List<JsonAnswer> jsonAnswerList, String editing){
        for(JsonAnswer jsonAnswer : jsonAnswerList){
            AnswersForApproval answersForApproval = new AnswersForApproval();
            answersForApproval.setAnswer(jsonAnswer.getAnswer());
            answersForApproval.setCreated_by(jsonAnswer.getCreated_by());
            answersForApproval.setDate_created(jsonAnswer.getDate_created());
            answersForApproval.setDate_modified(jsonAnswer.getDate_modified());
            answersForApproval.setFormbase(jsonAnswer.getFormbase());
            answersForApproval.setState(jsonAnswer.getState());
            answersForApproval.setUrl(jsonAnswer.getUrl());
            answersForApproval.setFulfilled(jsonAnswer.getFulfilled());
            answersForApproval.setModified_by(jsonAnswer.getModified_by());
            answersForApproval.setEditing(editing);
            answersForApproval.save();
        }
    }

    public static int countTable(){
        return new Select().from(AnswersForApproval.class).execute().size();
    }

    public static void deleteData(){
        new Delete().from(AnswersForApproval.class).execute();
    }

    public static AnswersForApproval getAnswersForApprovalByCreatedBy(String created_by){
        return new Select().from(AnswersForApproval.class).where("Created_By = ?", created_by).executeSingle();
    }

    public static AnswersForApproval getAnswersForApprovalByURL(String url){
        return new Select().from(AnswersForApproval.class).where("URL = ?", url).executeSingle();
    }

    public static List<AnswersForApproval> getAllAnswersForApproval(){
        return new Select().from(AnswersForApproval.class).execute();
    }
}
