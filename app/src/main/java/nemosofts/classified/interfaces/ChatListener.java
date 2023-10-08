package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemChat;

public interface ChatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemChat> arrayList);
}
