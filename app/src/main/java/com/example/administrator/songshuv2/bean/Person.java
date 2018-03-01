package com.example.administrator.songshuv2.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class Person extends BmobObject {
    private String userName;
    private int userResults;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserResults(int userResults) {
        this.userResults = userResults;
    }

    public int getUserResults() {
        return userResults;
    }

}
