package nemosofts.classified.interfaces;

public interface BankDetailsListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message, String name, String account_number, String details);
}