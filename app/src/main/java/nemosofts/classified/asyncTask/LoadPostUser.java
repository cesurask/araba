package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.PostUserListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadPostUser extends AsyncTask<String, String, String> {

    private final PostUserListener postListener;
    private final ArrayList<ItemData> arrayList = new ArrayList<>();
    private final RequestBody requestBody;
    private String verifyStatus = "0", message = "";

    public LoadPostUser(PostUserListener postListener, RequestBody requestBody) {
        this.postListener = postListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        postListener.onStart();
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
                    String user_id = obj.getString("user_id");
                    String title = obj.getString("title");
                    String cat_name = obj.getString("cat_name");
                    String money = obj.getString("money");
                    String date_time = obj.getString("date_time");
                    String post_image = obj.getString("post_image");
                    String sold_out = obj.getString("sold_out");

                    arrayList.add(new ItemData(id, user_id, title, cat_name, money, date_time, post_image, false, false, sold_out));

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
        postListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }

}

