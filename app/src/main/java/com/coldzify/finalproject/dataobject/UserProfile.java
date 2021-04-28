package com.coldzify.finalproject.dataobject;

public class UserProfile {
    private String uid,firstname,lastname,email,picture,role,userType,takecareType;

    public UserProfile(){}
    public UserProfile(String uid, String first_name,String last_name, String email, String picture,String role,String userType,String takecareType) {
        this.uid = uid;
        this.firstname = first_name;
        this.lastname = last_name;
        this.role = role;
        this.email = email;
        this.picture = picture;
        this.userType = userType;
        this.takecareType = takecareType;
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

    public String getrole() {
        return role;
    }

    public String getuserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    public String gettakecareType() {
        return takecareType;
    }

    public String getPicture() {
        return picture;
    }
}
