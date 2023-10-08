package nemosofts.classified.interfaces;

public interface LoginListener {
    void onStart();
    void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String user_gender, String user_phone, String profile_img,
               String otp_status, String member, String registered_on);
}