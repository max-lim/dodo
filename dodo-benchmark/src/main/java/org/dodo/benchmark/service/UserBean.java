package org.dodo.benchmark.service;

import java.util.ArrayList;
import java.util.List;

public class UserBean {
    private long id;
    private String name;
    private int sex;
    private int age;
    private String email;
    private String phone;
    private int score;
    private List<UserItemBean> items;

    public UserBean() {
        this.items = new ArrayList<>();
    }

    public UserBean(long id, String name, String email, String phone, int score, List<UserItemBean> items) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.score = score;
        this.items = items;
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

    public List<UserItemBean> getItems() {
        return items;
    }

    public void setItems(List<UserItemBean> items) {
        this.items = items;
    }

    public void addItem(UserItemBean userItemBean) {
        items.add(userItemBean);
    }

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
                ", items=" + items +
                '}';
    }
}
