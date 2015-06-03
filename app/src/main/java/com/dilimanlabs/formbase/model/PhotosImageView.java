package com.dilimanlabs.formbase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by user on 6/3/2015.
 */
@Table(name = "PhotosImageView")
public class PhotosImageView extends Model {

    @Column(name="Image_Id")
    private String imageId;
    @Column(name = "Photo_Path")
    private String photo_path;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public static void insertPhoto(String imageId, String photo_path){
        PhotosImageView photosImageView = new PhotosImageView();
        photosImageView.setImageId(imageId);
        photosImageView.setPhoto_path(photo_path);
        photosImageView.save();
    }

    public static String getPhotoPath(String imageId){
       PhotosImageView photosImageView =  new Select().from(PhotosImageView.class).where("Image_Id = ?", imageId).executeSingle();
        return photosImageView.getPhoto_path();
    }
}
