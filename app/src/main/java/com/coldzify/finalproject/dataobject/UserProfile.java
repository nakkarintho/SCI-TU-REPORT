package com.coldzify.finalproject.dataobject;

public class UserProfile {
    private String uid,firstname,lastname,email,birthday,picture;

    private String userType;
    public UserProfile(){}
    public UserProfile(String uid, String first_name,String last_name, String email, String birthday, String picture,String userType) {
        this.uid = uid;
        this.firstname = first_name;
        this.lastname = last_name;
        this.userType = userType;
        this.email = email;
        this.birthday = birthday;
        this.picture = picture;

    }

    public String getUid() {
        return uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }



    public String getBirthday() {
        return birthday;
    }

    public String getPicture() {
        return picture;
    }
}
