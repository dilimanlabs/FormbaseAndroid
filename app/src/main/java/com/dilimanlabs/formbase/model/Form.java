package com.dilimanlabs.formbase.model;

import android.util.Log;

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

    public static Form getJsonString (String formName){
        return new Select()
                .from(Form.class)
                .where("FormName = ?", formName)
                .executeSingle();
    }

    public static synchronized Form insertData(String url, String created_by, String category, String content, String name){
        Form form = new Form();
        form.setUrl(url);
        form.setCreated_by(created_by);
        form.setCategory(category);
        Log.e("Category", category);
        form.setContent(content);
        form.setName(name);
        form.save();
        return form;
    }

    public static List<Form> getAllForms(){
        return new Select().from(Form.class).execute();
    }

    public static List<Form> getAllFormsByCategory(String category){
        return new Select().from(Form.class).where("Category = ?", category).execute();
    }

    public static List<Form> getAllFormsUncategorized(){
        return new Select().from(Form.class).where("Category = null").execute();
    }

    public static Form getFormByName(String name){
        return new Select().from(Form.class).where("Name = ?", name).executeSingle();
    }

    public static int countTable(){
        return new Select().from(Form.class).execute().size();
    }

    public static void deleteData(){
        new Delete().from(Form.class).execute();
    }
}
