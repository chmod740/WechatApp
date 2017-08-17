package me.hupeng.app.wechat.chat.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HUPENG on 2017/8/10.
 */
@Table(name = "t_contact")
public class Contact {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "sex")
    private boolean sex;

    @Column(name = "userId")
    private int userId;

    @Column(name = "username")
    private String username;

    @Column(name = "nickName")
    private String nickName;

    @Column(name = "userPhoto")
    private String userPhoto;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
