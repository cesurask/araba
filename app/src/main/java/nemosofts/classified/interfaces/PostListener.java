package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemData;

public interface PostListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemData> arrayListPost, ArrayList<ItemData> arrayListTop);
}
