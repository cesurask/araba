package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemData;
import nemosofts.classified.item.ItemGallery;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.item.ItemUser;

public interface DetailsListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemPostDatabase> postArrayList, ArrayList<ItemData> dataArrayList,
               ArrayList<ItemUser> userArrayList, ArrayList<ItemGallery> arrayListGallery, Boolean isBlockShop);
}
