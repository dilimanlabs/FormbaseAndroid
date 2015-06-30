package com.dilimanlabs.formbase.objects;

/**
 * Created by user on 3/25/2015.
 */
public class JsonAnswer {

    String url;
    String date_created;
    String date_modified;
    String answer;
    String state;
    String name;
    String created_by;
    String formbase;
    String modified_by;
    String fulfilled;
    String editing;
    String submission_bin;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormbase() {
        return formbase;
    }

    public void setFormbase(String formbase) {
        this.formbase = formbase;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
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

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getSubmission_bin() {
        return submission_bin;
    }

    public void setSubmission_bin(String submission_bin) {
        this.submission_bin = submission_bin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
