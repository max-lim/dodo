package org.dodo.rpc.serialize;


import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class UserBean implements Serializable {
    private long id;
    private String name;
    private int sex;
    private int age;
    private String email;
    private String phone;
    private int score;
//    private Date createAt;
    private Object other;
//    private List<UserItemBean> items;
//    private Map<Integer, UserItemBean> itemsMap = new HashMap<>();
//    private BigDecimal big;
//    private Exception ex = new IOException("test io throw");

    public UserBean() {
//        this.items = new ArrayList<>();
    }

    public UserBean(long id, String name, String email, String phone, int score, List<UserItemBean> items) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.score = score;
//        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

//    public List<UserItemBean> getItems() {
//        return items;
//    }
//
//    public void setItems(List<UserItemBean> items) {
//        this.items = items;
//    }
//
//    public void addItem(UserItemBean userItemBean) {
//        items.add(userItemBean);
//    }

//    public Date getCreateAt() {
//        return createAt;
//    }
//
//    public void setCreateAt(Date createAt) {
//        this.createAt = createAt;
//    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }

//    public Map<Integer, UserItemBean> getItemsMap() {
//        return itemsMap;
//    }
//
//    public void setItemsMap(Map<Integer, UserItemBean> itemsMap) {
//        this.itemsMap = itemsMap;
//    }
//
//    public BigDecimal getBig() {
//        return big;
//    }
//
//    public void setBig(BigDecimal big) {
//        this.big = big;
//    }
//
//    public Exception getEx() {
//        return ex;
//    }
//
//    public void setEx(Exception ex) {
//        this.ex = ex;
//    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", score=" + score +
//                ", createAt=" + createAt +
                ", other=" + other +
//                ", items=" + items +
//                ", itemsMap=" + itemsMap +
//                ", big=" + big +
//                ", ex=" + ex +
                '}';
    }
}
