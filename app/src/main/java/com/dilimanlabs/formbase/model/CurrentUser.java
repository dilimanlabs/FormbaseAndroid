package com.dilimanlabs.formbase.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dilimanlabs.formbase.objects.UserInfo;

import java.util.List;

/**
 * Created by user on 5/8/2015.
 */
@Table(name = "CurrentUser")
public class CurrentUser extends Model {
    @Column(name = "URL")
    private String url;
    @Column(name = "Username")
    private String username;
    @Column(name = "Email")
    private String email;
    @Column(name = "Is_Staff")
    private String is_staff;
    @Column(name = "Is_Manager")
    private String is_manager;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIs_staff() {
        return is_staff;
    }

    public void setIs_staff(String is_staff) {
        this.is_staff = is_staff;
    }

    public String getIs_manager() {
        return is_manager;
    }

    public void setIs_manager(String is_manager) {
        this.is_manager = is_manager;
    }

    public static void insertCurrentUser(UserInfo userInfo){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUsername(userInfo.getUsername());
        currentUser.setUrl(userInfo.getUrl());
        currentUser.setEmail(userInfo.getEmail());
        currentUser.setIs_manager(userInfo.getIs_manager());
        currentUser.setIs_staff(userInfo.getIs_staff());
        currentUser.save();
        ActiveAndroid.getDatabase().close();
    }

    public static CurrentUser getCurrentUser(){
        List<CurrentUser> currentUserList = new Select().from(CurrentUser.class).execute();
        ActiveAndroid.getDatabase().close();
        return currentUserList.get(0);
    }

    public static void deleteData(){
        new Delete().from(CurrentUser.class).execute();
        ActiveAndroid.getDatabase().close();
    }

    public static int countTable(){
        int size = new Select().from(CurrentUser.class).execute().size();
        ActiveAndroid.getDatabase().close();
        return size;
    }
}
