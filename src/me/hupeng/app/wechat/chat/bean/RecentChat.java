package me.hupeng.app.wechat.chat.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by HUPENG on 2017/8/10.
 */
@Table(name = "t_recent_chat")
public class RecentChat{
    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "content")
    private String content;

    @Column(name = "userId")
    private int userId;

    private String username;

    private String userPhoto;


    @Column(name = "unreadMessageCount")
    private int unreadMessageCount;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }
}
