package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.ChatListener;
import nemosofts.classified.item.ItemChat;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadChat extends AsyncTask<String, String, String> {

    private final ChatListener listener;
    private final ArrayList<ItemChat> arrayList = new ArrayList<>();
    private final RequestBody requestBody;
    private String verifyStatus = "0", message = "";

    public LoadChat(ChatListener listener, RequestBody requestBody) {
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

                    String did = obj.getString("did");
                    String chat_id = obj.getString("chat_id");
                    String chat_user_id = obj.getString("chat_user_id");
                    boolean isImage = obj.getBoolean("is_image");
                    String chat_text = obj.getString("chat_text");
                    String chat_on = obj.getString("chat_on");
                    String chat_user_on = obj.getString("chat_user_on");

                    arrayList.add(new ItemChat(did,chat_id,chat_user_id,isImage,chat_text,chat_on,chat_user_on));

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

