package me.hupeng.app.wechat.chat.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;


/**
 * 详细的聊天记录表.
 */
@Table(name = "t_chat_detail")
public class ChatDetail {
    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "fromUserId")
    private int fromUserId;


    @Column(name = "toUserId")
    private int toUserId;


    /**
     * 消息类型
     * 0x01：文本消息
     * 0x02：图片消息
     * */
    @Column(name = "msgType")
    private int msgType;

    /**
     * 消息内容
     * */
    @Column(name = "content")
    private String content;

    @Column(name = "createTime")
    private Date createTime;

    /**
     * 信息的发送与接受状态
     * 0:发送成功
     * -1：发送中
     * -2：发送失败
     * */
    @Column(name = "status")
    private int status;

    /**
     * 信息的已读与未读状态
     * */
    @Column(name = "read")
    private boolean read;

    /**
     * 信息是发送还是接收
     * */
    @Column(name = "send")
    private boolean send;

    @Column(name = "ts")
    private long ts;

    public ChatDetail(int id, int fromUserId,int toUserId,  int msgType, String content, Date createTime, int status, boolean read, boolean send) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.msgType = msgType;
        this.content = content;
        this.createTime = createTime;
        this.status = status;
        this.read = read;
        this.send = send;
        this.ts = 0;
    }

    public ChatDetail(){

    }

    public ChatDetail(int fromUserId, int toUserId,  int msgType, String content, Date createTime, int status, boolean read, boolean send) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.msgType = msgType;
        this.content = content;
        this.createTime = createTime;
        this.status = status;
        this.read = read;
        this.send = send;
        this.ts = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }


    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
