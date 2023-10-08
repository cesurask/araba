package nemosofts.classified.interfaces;


public interface StripeTokenListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message , String token);
}