package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemPostDatabase;

public interface DatabaseListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemPostDatabase> itemPosts);
}
