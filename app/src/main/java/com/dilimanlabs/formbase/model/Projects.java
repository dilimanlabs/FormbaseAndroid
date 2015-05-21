package com.dilimanlabs.formbase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by user on 5/15/2015.
 */
@Table(name = "Projects")
public class Projects extends Model {
    @Column(name="URL")
    String url;
    @Column(name="Name")
    String name;
    @Column(name="Organizational_Group")
    String organizational_group;
    @Column(name="Created_By")
    String created_by;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizational_group() {
        return organizational_group;
    }

    public void setOrganizational_group(String organizational_group) {
        this.organizational_group = organizational_group;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public static List<Projects> getAllProjects(){
        return new Select().from(Projects.class).execute();
    }

    public static int countTable(){
        return new Select().from(Projects.class).execute().size();
    }
}
