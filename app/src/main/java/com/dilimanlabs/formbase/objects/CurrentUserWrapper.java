package com.dilimanlabs.formbase.objects;

import java.util.List;

/**
 * Created by user on 5/8/2015.
 */
public class CurrentUserWrapper {
    List<UserInfo> userInfoList;

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
