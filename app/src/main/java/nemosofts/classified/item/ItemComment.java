package nemosofts.classified.item;

public class ItemComment {

    private final String id;
    private final String userid;
    private final String username;
    private final String user_email;
    private final String comment_text;
    private final String dp;
    private final String date;

    public ItemComment(String id, String userid, String username, String user_email, String comment_text, String dp, String date) {
        this.id = id;
        this.userid = userid;
        this.username = username;
        this.user_email = user_email;
        this.comment_text = comment_text;
        this.dp = dp;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userid;
    }

    public String getUserName() {
        return username;
    }

    public String getUserEmail() {
        return user_email;
    }

    public String getCommentText() {
        return comment_text;
    }

    public String getDp() {
        return dp;
    }

    public String getDate() {
        return date;
    }
}