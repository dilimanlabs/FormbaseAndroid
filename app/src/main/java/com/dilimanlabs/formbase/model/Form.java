package com.dilimanlabs.formbase.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by user on 4/6/2015.
 */
@Table(name = "Form")
public class Form extends Model {

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

    @Column(name = "Published")
    public String published;

    @Column(name = "Deleted")
    public String deleted;

    @Column(name = "Starting")
    public String starting;

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
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

    public String getStarting() {
        return starting;
    }

    public void setStarting(String starting) {
        this.starting = starting;
    }

    public static Form getJsonString (String formName){
        return new Select()
                .from(Form.class)
                .where("FormName = ?", formName)
                .executeSingle();
    }

    public static synchronized Form insertData(String url, String created_by, String category, String content, String name, String starting){
        Form form = new Form();
        form.setUrl(url);
        form.setCreated_by(created_by);
        form.setCategory(category);
        form.setContent(content);
        form.setName(name);
        form.setStarting(starting);
        form.save();
        return form;
    }

    public static List<Form> getAllForms(){
        List<Form> formList = new Select().from(Form.class).execute();
        ActiveAndroid.getDatabase().close();
        return formList;
    }

    public static List<Form> getAllFormsByCategory(String category){
        List<Form> formList = new Select().from(Form.class).where("Category = ?", category).execute();
        ActiveAndroid.getDatabase().close();
        return formList;
    }

    public static List<Form> getAllFormsUncategorized(){
        List<Form> formList = new Select().from(Form.class).where("Category = null").execute();
        ActiveAndroid.getDatabase().close();
        return formList;
    }

    public static Form getFormByName(String name){
        Form form = new Select().from(Form.class).where("Name = ?", name).executeSingle();
        ActiveAndroid.getDatabase().close();
        return form;
    }

    public static int countTable(){
        int size = new Select().from(Form.class).execute().size();
        ActiveAndroid.getDatabase().close();
        return size;
    }

    public static void deleteData(){
        new Delete().from(Form.class).execute();
        ActiveAndroid.getDatabase().close();
    }
}
