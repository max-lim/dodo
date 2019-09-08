package org.dodo.benchmark.service;

import java.util.Date;

public class UserItemBean {
    private int userId;
    private int itemId;
    private int num;
    private Date createAt;
    private Date modifiedAt;

    public UserItemBean() {
    }

    public UserItemBean(int userId, int itemId, int num, Date createAt, Date modifiedAt) {
        this.userId = userId;
        this.itemId = itemId;
        this.num = num;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return "UserItemBean{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                ", num=" + num +
                ", createAt=" + createAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}