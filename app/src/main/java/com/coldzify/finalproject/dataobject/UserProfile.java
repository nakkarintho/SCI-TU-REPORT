package com.coldzify.finalproject.dataobject;

public class UserProfile {
    private String uid,firstname,lastname,email,picture,role;

    private String userType;
    public UserProfile(){}
    public UserProfile(String uid, String first_name,String last_name, String email, String picture,String userType,String role) {
        this.uid = uid;
        this.firstname = first_name;
        this.lastname = last_name;
        this.userType = userType;
        this.email = email;
        this.picture = picture;
        this.role = role;

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

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}
