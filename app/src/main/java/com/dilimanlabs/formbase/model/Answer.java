package com.dilimanlabs.formbase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by user on 4/8/2015.
 */
@Table(name = "Answer")
public class Answer extends Model {
    @Column(name = "FormName")
    public String name;

    @Column(name = "FormJSON")
    public String json;

    @Column(name = "Category")
    public Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
