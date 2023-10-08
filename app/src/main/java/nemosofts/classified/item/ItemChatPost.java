package nemosofts.classified.item;

public class ItemChatPost {

    private final String id;
    private final String post_id;
    private final String post_title;

    private final String chat_user_id;
    private final String chat_user_image;
    private final String chat_user_name;

    private final String post_user_id;
    private final String post_user_image;
    private final String post_user_name;

    public ItemChatPost(String id, String post_id, String post_title, String chat_user_id, String chat_user_image, String chat_user_name, String post_user_id, String post_user_image, String post_user_name) {
        this.id = id;
        this.post_id = post_id;
        this.post_title = post_title;
        this.chat_user_id = chat_user_id;
        this.chat_user_image = chat_user_image;
        this.chat_user_name = chat_user_name;
        this.post_user_id = post_user_id;
        this.post_user_image = post_user_image;
        this.post_user_name = post_user_name;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return post_id;
    }

    public String getPostTitle() {
        return post_title;
    }

    public String getChatUserId() {
        return chat_user_id;
    }

    public String getChatUserName() {
        return chat_user_name;
    }

    public String getChatUserImage() {
        return chat_user_image;
    }

    public String getPostUserId() {
        return post_user_id;
    }

    public String getPostUserName() {
        return post_user_name;
    }

    public String getPostUserImage() {
        return post_user_image;
    }
}
