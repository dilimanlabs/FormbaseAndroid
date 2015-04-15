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
public class Forms extends Model {

    @Column(name = "Form")
    public Form form;

    @Column(name = "Category")
    public String category;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Form> forms() {
        return getMany(Form.class, "Forms");
    }

    public static void insertDataInForms(Form form, String category){
        Forms forms = new Forms();
        forms.setForm(form);
        forms.setCategory(category);
        forms.save();
    }

    public static List<Forms> getAllFormsByCategory(String category){
        return new Select().from(Forms.class).where("Category = ?", category).execute();
    }
}
