package nemosofts.classified.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private final EncryptData encryptData;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String TAG_FIRST_OPEN = "firstopen", TAG_IS_LOGGED = "islogged", TAG_UID = "uid", TAG_USERNAME = "name",
            TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_GENDER = "gender", TAG_REMEMBER = "rem" ,
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", TAG_LOGIN_TYPE = "loginType",
            TAG_AUTH_ID = "auth_id", TAG_IMAGES = "profile";
    private static final String TAG_OTP_STATUS = "otp_status", TAG_MEMBER = "member", TAG_REG_ON = "registered_on";

    public SharedPref(Context ctx) {
        encryptData = new EncryptData(ctx);
        sharedPreferences = ctx.getSharedPreferences("setting_app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean(TAG_FIRST_OPEN, true);
    }
    public void setIsFirst(Boolean flag) {
        editor.putBoolean(TAG_FIRST_OPEN, flag);
        editor.apply();
    }

    public boolean isLogged() {
        return sharedPreferences.getBoolean(TAG_IS_LOGGED, false);
    }
    public void setIsLogged(Boolean isLogged) {
        editor.putBoolean(TAG_IS_LOGGED, isLogged);
        editor.apply();
    }

    public void setLoginDetails(String id, String name, String mobile, String email, String gender, String profilePic, String authID, Boolean isRemember, String password, String loginType,
                                String otp_status, String member, String registered_on) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, encryptData.encrypt(id));
        editor.putString(TAG_USERNAME, encryptData.encrypt(name));
        editor.putString(TAG_MOBILE, encryptData.encrypt(mobile));
        editor.putString(TAG_EMAIL, encryptData.encrypt(email));
        editor.putString(TAG_GENDER, encryptData.encrypt(gender));
        editor.putString(TAG_PASSWORD, encryptData.encrypt(password));
        editor.putString(TAG_LOGIN_TYPE, encryptData.encrypt(loginType));
        editor.putString(TAG_AUTH_ID, encryptData.encrypt(authID));
        if (profilePic != null) {
            editor.putString(TAG_IMAGES, encryptData.encrypt(profilePic.replace(" ", "%20")));
        }
        editor.putString(TAG_OTP_STATUS, otp_status);
        editor.putString(TAG_MEMBER, member);
        editor.putString(TAG_REG_ON, registered_on);
        editor.apply();
    }

    public void setOtpStatus(String otp_status) {
        editor.putString(TAG_OTP_STATUS, otp_status);
        editor.apply();
    }

    public String getOtpStatus() {
        return sharedPreferences.getString(TAG_OTP_STATUS, "0");
    }

    public String getMember() {
        return sharedPreferences.getString(TAG_MEMBER, "0");
    }

    public void setRemember(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public String getUserId() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_UID, ""));
    }

    public void setUserName(String userName) {
        editor.putString(TAG_USERNAME, encryptData.encrypt(userName));
        editor.apply();
    }

    public String getUserName() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_USERNAME, ""));
    }

    public void setEmail(String email) {
        editor.putString(TAG_EMAIL, encryptData.encrypt(email));
        editor.apply();
    }

    public String getEmail() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public void setUserMobile(String mobile) {
        editor.putString(TAG_MOBILE, encryptData.encrypt(mobile));
        editor.apply();
    }

    public String getUserMobile() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_MOBILE, ""));
    }

    public String getPassword() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean getIsAutoLogin() { return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false); }
    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public String getLoginType() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,""));
    }

    public String getAuthID() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_AUTH_ID,""));
    }

    public String getProfileImages() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_IMAGES,""));
    }
    public void setProfileImages(String profilePic) {
        if (profilePic != null) {
            editor.putString(TAG_IMAGES, encryptData.encrypt(profilePic.replace(" ", "%20")));
            editor.apply();
        }
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }
    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public Boolean getGrid() {
        return sharedPreferences.getBoolean("grid_lay", true);
    }
    public void setGrid(Boolean state) {
        editor.putBoolean("grid_lay", state);
        editor.apply();
    }

    public Boolean getGridSimilar() {
        return sharedPreferences.getBoolean("grid_similar", false);
    }
    public void setGridSimilar(Boolean state) {
        editor.putBoolean("grid_similar", state);
        editor.apply();
    }

    public int getTextSize() {
        return sharedPreferences.getInt("text_size", 2);
    }
    public void setTextSize(int state) {
        editor.putInt("text_size", state);
        editor.apply();
    }
}