package nemosofts.classified.item;

public class ItemChat {

    private final String id;
    private final String chat_id;
    private final String chat_user_id;
    private final Boolean is_image;
    private final String chat_text;
    private final String chat_on;
    private final String this_chat_use;

    public ItemChat(String id, String chat_id, String chat_user_id, Boolean is_image, String chat_text, String chat_on, String this_chat_use) {
        this.id = id;
        this.chat_id = chat_id;
        this.chat_user_id = chat_user_id;
        this.is_image = is_image;
        this.chat_text = chat_text;
        this.chat_on = chat_on;
        this.this_chat_use = this_chat_use;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return chat_id;
    }

    public String getChatUserId() {
        return chat_user_id;
    }

    public Boolean getIsImage() {
        return is_image;
    }

    public String getChatText() {
        return chat_text;
    }

    public String getChatOn() {
        return chat_on;
    }

    public String getThisChatUse() {
        return this_chat_use;
    }
}
