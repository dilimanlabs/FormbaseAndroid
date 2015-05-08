package com.dilimanlabs.formbase.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Created by user on 4/27/2015.
 */
@Table(name = "Photos")
public class Photos extends Model {

    @Column(name = "Answer_Id")
    String answer_id;
    @Column(name = "Path")
    String path;
    @Column(name = "Answer_URL")
    String photo_url;

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public synchronized  static void updatePhoto(String answer_id, String url){
        Log.e("Photo", "Photo Updated");
        Photos photos = new Select().from(Photos.class).where("Answer_Id = ?", answer_id).executeSingle();
        photos.setPhoto_url(url);
        photos.save();
    }

    public synchronized static void insertPhoto(String answer_id, String path){
        Log.e("Photo", "Photo Inserted");
        Photos photos = new Photos();
        photos.setAnswer_id(answer_id);
        photos.setPath(path);
        photos.setPhoto_url(null);
        photos.save();
    }
    public static String getPhotoPath(String answer_id){
        Photos photos = new Select().from(Photos.class).where("Answer_Id = ?", answer_id).executeSingle();
        return photos.getPath();
    }

    public static void deleteData(){
        new Delete().from(Photos.class).execute();
    }
}
