package nemosofts.classified.interfaces;

public interface PaymentSettingsListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message);
}