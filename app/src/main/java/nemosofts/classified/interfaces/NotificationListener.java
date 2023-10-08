package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemNotification;

public interface NotificationListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemNotification> notificationArrayList);
}