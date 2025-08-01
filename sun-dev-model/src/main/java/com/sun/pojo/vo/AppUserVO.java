package com.sun.pojo.vo;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AppUserVO {
    private String id;
    private String nickname;
    private String face;
    private Integer activeStatus;
    private Integer myFollowCounts;

    public Integer getMyFansCounts() {
        return myFansCounts;
    }

    public void setMyFansCounts(Integer myFansCounts) {
        this.myFansCounts = myFansCounts;
    }

    private Integer myFansCounts;

    public Integer getMyFollowCounts() {
        return myFollowCounts;
    }

    public void setMyFollowCounts(Integer myFollowCounts) {
        this.myFollowCounts = myFollowCounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public String toString() {
        return "AppUserVO{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", face='" + face + '\'' +
                ", activeStatus=" + activeStatus +
                ", myFollowCounts=" + myFollowCounts +
                ", myFansCounts=" + myFansCounts +
                '}';
    }
}