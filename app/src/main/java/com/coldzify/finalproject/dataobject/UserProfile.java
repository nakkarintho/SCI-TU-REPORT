package com.coldzify.finalproject.dataobject;

public class UserProfile {
    private String uid,firstname,lastname,email,picture,role,userType;

    public UserProfile(){}
    public UserProfile(String uid, String first_name,String last_name, String email, String picture,String role,String userType) {
        this.uid = uid;
        this.firstname = first_name;
        this.lastname = last_name;
        this.role = role;
        this.email = email;
        this.picture = picture;
        this.userType = userType;

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

    public String getPicture() {
        return picture;
    }
}
