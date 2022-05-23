package com.sanedu.fcrecognition.Model;

public class User {
    private String uId;
    private String username;
    private String phoneNumber;
    private String avatar;
    private long joinDate;
    private String gender;
    private int age;

    public User() {
    }

    public User(String uId, String username, String phoneNumber, String avatar, long joinDate) {
        this.uId = uId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.joinDate = joinDate;
    }

    public String getuId() {
        return uId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
