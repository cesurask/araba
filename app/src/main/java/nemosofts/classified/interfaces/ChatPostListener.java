package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemChatPost;

public interface ChatPostListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemChatPost> arrayList);
}
