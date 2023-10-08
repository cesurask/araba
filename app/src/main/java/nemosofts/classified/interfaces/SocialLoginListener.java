package nemosofts.classified.interfaces;

public interface SocialLoginListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message, String user_id, String user_name, String email, String auth_id,
               String otp_status, String member, String registered_on);
}