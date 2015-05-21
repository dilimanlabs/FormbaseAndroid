package com.dilimanlabs.formbase.objects;

/**
 * Created by user on 5/15/2015.
 */
public class Project {
    String url;
    String name;
    String organizational_group;
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
}
