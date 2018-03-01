package com.example.administrator.songshuv2.bean;


import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class Music extends BmobObject {

    private String musicName;
    private BmobFile musicFile;

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicFile(BmobFile musicFile) {
        this.musicFile = musicFile;
    }

    public BmobFile getMusicFile() {
        return musicFile;
    }
}
