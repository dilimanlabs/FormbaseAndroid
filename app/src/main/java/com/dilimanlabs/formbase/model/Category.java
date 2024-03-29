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
@Table(name = "Categories")
public class Category extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "URL")
    public String url;

    @Column(name = "Created_By")
    public String created_by;

    @Column(name = "Parent")
    public String parent;


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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Category> getAllCategories(){
        List<Category> categoryList = new Select().from(Category.class).execute();
        ActiveAndroid.getDatabase().close();
        return categoryList;
    }

    public static String getCategoryNameByURL(String url){
        Category category = new Select().from(Category.class).where("URL = ?", url).executeSingle();
        ActiveAndroid.getDatabase().close();
        return category.getName();
    }

    public static List<Category> getAllCategoryByURL(String parent){
        List<Category> categoryList = new Select().from(Category.class).where("Parent = ?",parent).execute();
        ActiveAndroid.getDatabase().close();
        return categoryList;
    }

    public static Category getURLByCategoryName(String name){
        Category category = new Select().from(Category.class).where("Name = ?", name).executeSingle();
        ActiveAndroid.getDatabase().close();
        return category;
    }

    public static String getParentCategoryByName(String name){
        Category category = new Select().from(Category.class).where("Name = ?", name).executeSingle();
        ActiveAndroid.getDatabase().close();
        return category.getParent();
    }

    public static int countTable(){
        int size = new Select().from(Category.class).execute().size();
        ActiveAndroid.getDatabase().close();
        return size;
    }

    public static void deleteData(){
        new Delete().from(Category.class).execute();
        ActiveAndroid.getDatabase().close();
    }


}
