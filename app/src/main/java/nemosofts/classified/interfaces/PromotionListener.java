package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemProActive;
import nemosofts.classified.item.ItemPromotions;

public interface PromotionListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemPromotions> promotionsArrayList, ArrayList<ItemProActive> activeArrayList);
}
