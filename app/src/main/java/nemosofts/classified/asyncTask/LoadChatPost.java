package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.ChatPostListener;
import nemosofts.classified.item.ItemChatPost;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadChatPost extends AsyncTask<String, String, String> {

    private final ChatPostListener listener;
    private final ArrayList<ItemChatPost> arrayList = new ArrayList<>();
    private final RequestBody requestBody;
    private String verifyStatus = "0", message = "";

    public LoadChatPost(ChatPostListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected  String doInBackground(String... strings)  {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);

            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Callback.TAG_SUCCESS)) {

                    String id = obj.getString("id");
                    String post_id = obj.getString("post_id");
                    String post_title = obj.getString("post_title");

                    String chat_user_id = obj.getString("chat_user_id");
                    String chat_user_image = obj.getString("chat_user_image");
                    String chat_user_name = obj.getString("chat_user_name");

                    String post_user_id = obj.getString("post_user_id");
                    String post_user_image = obj.getString("post_user_image");
                    String post_user_name = obj.getString("post_user_name");

                    arrayList.add(new ItemChatPost(id,post_id,post_title,chat_user_id,chat_user_image,chat_user_name,post_user_id,post_user_image,post_user_name));

                } else {
                    verifyStatus = obj.getString(Callback.TAG_SUCCESS);
                    message = obj.getString(Callback.TAG_MSG);
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
        listener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}

