package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemSubCategory;

public interface FilterListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemCity> arrayListCity, ArrayList<ItemCategory> arrayListCategory, ArrayList<ItemSubCategory> arrayListSubCategory);
}
