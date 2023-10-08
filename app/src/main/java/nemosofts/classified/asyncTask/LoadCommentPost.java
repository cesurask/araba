package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.PostCommentListener;
import nemosofts.classified.item.ItemComment;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadCommentPost extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final PostCommentListener postCommentListener;
    private String success = "0", message = "";
    private ItemComment itemComment;

    public LoadCommentPost(PostCommentListener postCommentListener, RequestBody requestBody) {
        this.postCommentListener = postCommentListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        postCommentListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Callback.TAG_SUCCESS);
                message = c.getString(Callback.TAG_MSG);
                if(success.equals("1")) {

                    String nid = c.getString("id");
                    String uid = c.getString("user_id");
                    String user_name = c.getString("user_name");
                    String user_email = c.getString("user_email");
                    String user_dp = c.getString("user_profile");
                    String comment_text = c.getString("comment_text");
                    String comment_date = c.getString("comment_on");

                    itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text, user_dp, comment_date);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        postCommentListener.onEnd(s, success, message, itemComment);
        super.onPostExecute(s);
    }
}