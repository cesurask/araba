package nemosofts.classified.interfaces;


import nemosofts.classified.item.ItemComment;

public interface PostCommentListener {
    void onStart();
    void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment);
}