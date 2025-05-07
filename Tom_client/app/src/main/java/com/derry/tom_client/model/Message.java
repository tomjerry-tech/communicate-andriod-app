package com.derry.tom_client.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private Long id;
    private String content;
    private String sender;
    private Date createTime;
    private MessageType type;

    // 消息类型枚举
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    // 无参构造函数
    public Message() {
    }

    // 带参数的构造函数
    public Message(String content, String sender, MessageType type) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.createTime = new Date();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}