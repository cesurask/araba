package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.DatabaseListener;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadPostDatabase extends AsyncTask<String, String, String> {

    private final DatabaseListener postListener;
    private final ArrayList<ItemPostDatabase> arrayList = new ArrayList<>();
    private final RequestBody requestBody;
    private String verifyStatus = "0", message = "";

    public LoadPostDatabase(DatabaseListener postListener, RequestBody requestBody) {
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
                    String description = obj.getString("description");
                    String money = obj.getString("money");
                    String phone_1 = obj.getString("phone_1");
                    String phone_2 = obj.getString("phone_2");
                    String condition = obj.getString("condition");
                    String date_time = obj.getString("date_time");

                    String post_image = obj.getString("post_image").replace(" ", "%20");
                    if (post_image.equals("")) {
                        post_image = "null";
                    }

                    String cat_id = obj.getString("cat_id");
                    String catName = obj.getString("category_name");

                    String sub_cat_id = obj.getString("sub_cat_id");
                    String subCatName = obj.getString("sub_category_name");

                    String city_id = obj.getString("city_id");
                    String cityName = obj.getString("city_name");

                    String totalViews = obj.getString("total_views");
                    String totalShare = obj.getString("total_share");
                    Boolean active = obj.getString("active").equals("1");
                    boolean isFav = obj.getBoolean("is_favorite");

                    String sold_out = obj.getString("sold_out");

                    arrayList.add(new ItemPostDatabase(id,user_id,title,description,money,phone_1,phone_2,condition,date_time,
                            post_image, cat_id, catName,sub_cat_id, subCatName,city_id,cityName, totalViews,totalShare,active, isFav, sold_out));
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

