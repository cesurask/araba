package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.PostListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadPost extends AsyncTask<String, String, String> {

    private final PostListener postListener;
    private final ArrayList<ItemData> arrayList = new ArrayList<>();
    private final ArrayList<ItemData> arrayListTop = new ArrayList<>();
    private final RequestBody requestBody;
    private final Boolean isTopPost;

    public LoadPost(Boolean isTopPost, PostListener postListener, RequestBody requestBody) {
        this.postListener = postListener;
        this.requestBody = requestBody;
        this.isTopPost = isTopPost;
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
            JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);


            JSONArray jsonArrayCat = jsonObject.getJSONArray("post_list");

            for (int i = 0; i < jsonArrayCat.length(); i++) {
                JSONObject objJson = jsonArrayCat.getJSONObject(i);

                    String id = objJson.getString("id");
                    String user_id = objJson.getString("user_id");
                    String title = objJson.getString("title");
                    String cat_name = objJson.getString("cat_name");
                    String money = objJson.getString("money");
                    String date_time = objJson.getString("date_time");
                    String post_image = objJson.getString("post_image").replace(" ", "%20");
                    if (post_image.equals("")) {
                        post_image = "null";
                    }
                    String sold_out = objJson.getString("sold_out");

                    arrayList.add(new ItemData(id, user_id, title, cat_name, money, date_time, post_image, false, false, sold_out));
            }

            JSONArray jsonArraySpot = jsonObject.getJSONArray("top_list");

            for (int i = 0; i < jsonArraySpot.length(); i++) {
                JSONObject objJsonSpot = jsonArraySpot.getJSONObject(i);

                String id = objJsonSpot.getString("id");
                String user_id = objJsonSpot.getString("user_id");
                String title = objJsonSpot.getString("title");
                String cat_name = objJsonSpot.getString("cat_name");
                String money = objJsonSpot.getString("money");
                String date_time = objJsonSpot.getString("date_time");
                String post_image = objJsonSpot.getString("post_image").replace(" ", "%20");
                if (post_image.equals("")) {
                    post_image = "null";
                }
                String sold_out = objJsonSpot.getString("sold_out");

                arrayListTop.add(new ItemData(id, user_id, title, cat_name, money, date_time, post_image, !isTopPost, isTopPost, sold_out));
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        postListener.onEnd(s, arrayList, arrayListTop);
        super.onPostExecute(s);
    }

}

