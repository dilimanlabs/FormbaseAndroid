package com.dilimanlabs.formbase.model;

import android.widget.LinearLayout;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by user on 4/29/2015.
 */
@Table(name = "PauseAndResume")
public class PauseAndResume extends Model {
    @Column(name = "LinearLayout")
    private LinearLayout linearLayout;
    @Column(name = "CurrentPath")
    private String currentPath;
    @Column(name = "IsSaved")
    private boolean isSaved;

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public static void insertData(){
        PauseAndResume pauseAndResume = new PauseAndResume();
        pauseAndResume.setCurrentPath(null);
        pauseAndResume.setLinearLayout(null);
        pauseAndResume.setSaved(false);
        pauseAndResume.save();
    }

    public static List<PauseAndResume> getPauseAndResume(){
        return new Select().from(PauseAndResume.class).execute();
    }

    public static int countTable(){
        return new Select().from(PauseAndResume.class).execute().size();
    }
}
