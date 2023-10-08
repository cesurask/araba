package nemosofts.classified.interfaces;

import java.util.ArrayList;

import nemosofts.classified.item.ItemComment;

public interface CommentListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemComment> arrayListComment);
}