package nemosofts.classified.item;

import java.io.Serializable;

public class ItemUser implements Serializable {

    private  String id;
    private  String name;
    private  String email;
    private  String mobile;
    private final String gender;
    private final String authID;
    private final String loginType;
    private final String profile_images;
    private final String otp_status;
    private final String member;
    private final String registered_on;

    public ItemUser(String id, String name, String email, String mobile, String gender, String authID, String loginType, String profile_images, String otp_status, String member, String registered_on) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.authID = authID;
        this.loginType = loginType;
        this.profile_images = profile_images;

        this.otp_status = otp_status;
        this.member = member;
        this.registered_on = registered_on;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getGender() {
        return gender;
    }

    public String getAuthID() {
        return authID;
    }

    public String getLoginType() {
        return loginType;
    }

    public String getProfileImages() {
        return profile_images;
    }

    public String getVerified() {
        return otp_status;
    }

    public String getMember() {
        return member;
    }

    public String getRegisteredOn() {
        return registered_on;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}