package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemData;

public interface PostUserListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemData> arrayList);
}
