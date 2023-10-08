package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemPostHome;

public interface HomeListener {
    void onStart();
    void onEnd(String success, String message, ArrayList<ItemPostHome> arrayListPost);
}
