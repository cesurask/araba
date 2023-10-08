package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.CommentListener;
import nemosofts.classified.item.ItemComment;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadComments extends AsyncTask<String, String, String> {

    private final CommentListener commentListener;
    private final RequestBody requestBody;
    private final ArrayList<ItemComment> arrayList = new ArrayList<>();

    public LoadComments(CommentListener commentListener, RequestBody requestBody) {
        this.commentListener = commentListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        commentListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Callback.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj_com = jsonArray.getJSONObject(i);

                String nid = obj_com.getString("id");
                String uid = obj_com.getString("user_id");
                String user_name = obj_com.getString("user_name");
                String user_email = obj_com.getString("user_email");
                String user_dp = obj_com.getString("user_profile");
                String comment_text = obj_com.getString("comment_text");
                String comment_date = obj_com.getString("comment_on");

                ItemComment itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text, user_dp, comment_date);
                arrayList.add(itemComment);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        commentListener.onEnd(s, arrayList);
        super.onPostExecute(s);
    }
}
