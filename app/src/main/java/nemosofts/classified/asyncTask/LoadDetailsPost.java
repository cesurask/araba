package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.DetailsListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.item.ItemGallery;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.item.ItemUser;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadDetailsPost extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final DetailsListener listener;

    private final ArrayList<ItemPostDatabase> postArrayList = new ArrayList<>();
    private final ArrayList<ItemData> dataArrayList = new ArrayList<>();
    private final ArrayList<ItemUser> userArrayList = new ArrayList<>();
    private final ArrayList<ItemGallery> arrayListGallery = new ArrayList<>();
    private Boolean isBlockShop = false;

    public LoadDetailsPost(DetailsListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);

            JSONArray jsonArrayPost = jsonObject.getJSONArray("post_id_details");
            for (int i = 0; i < jsonArrayPost.length(); i++) {
                JSONObject objJsonPost = jsonArrayPost.getJSONObject(i);

                String id = objJsonPost.getString("id");
                String user_id = objJsonPost.getString("user_id");
                String title = objJsonPost.getString("title");
                String description = objJsonPost.getString("description");
                String money = objJsonPost.getString("money");
                String phone_1 = objJsonPost.getString("phone_1");
                String phone_2 = objJsonPost.getString("phone_2");
                String condition = objJsonPost.getString("condition");
                String date_time = objJsonPost.getString("date_time");

                String post_image = objJsonPost.getString("post_image").replace(" ", "%20");
                if (post_image.equals("")) {
                    post_image = "null";
                }

                String cat_id = objJsonPost.getString("cat_id");
                String catName = objJsonPost.getString("category_name");

                String sub_cat_id = objJsonPost.getString("sub_cat_id");
                String subCatName = objJsonPost.getString("sub_category_name");

                String city_id = objJsonPost.getString("city_id");
                String cityName = objJsonPost.getString("city_name");

                String totalViews = objJsonPost.getString("total_views");
                String totalShare = objJsonPost.getString("total_share");
                Boolean active = objJsonPost.getString("active").equals("1");
                boolean isFav = objJsonPost.getBoolean("is_favorite");

                String sold_out = objJsonPost.getString("sold_out");

                isBlockShop = objJsonPost.getBoolean("is_block_shop");

                postArrayList.add(new ItemPostDatabase(id,user_id,title,description,money,phone_1,phone_2,condition,date_time,
                        post_image, cat_id, catName,sub_cat_id, subCatName,city_id,cityName, totalViews,totalShare,active, isFav, sold_out));
            }

            JSONArray jsonArray= jsonObject.getJSONArray("similar_post");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

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

                dataArrayList.add(new ItemData(id, user_id, title, cat_name, money, date_time, post_image, false, false, sold_out));
            }

            JSONArray jsonArrayGallery = jsonObject.getJSONArray("post_gallery");
            for (int i = 0; i < jsonArrayGallery.length(); i++) {
                JSONObject objJson = jsonArrayGallery.getJSONObject(i);

                String id = objJson.getString("id");
                String image = objJson.getString("image");

                arrayListGallery.add(new ItemGallery(id, image));
            }

            JSONArray jsonArraySpot = jsonObject.getJSONArray("user_post");
            for (int i = 0; i < jsonArraySpot.length(); i++) {
                JSONObject c = jsonArraySpot.getJSONObject(i);

                String userId = c.getString("user_id");
                String userName = c.getString("user_name");
                String userEmail = c.getString("user_email");
                String userPhone = c.getString("user_phone");
                String userGender = c.getString("user_gender");
                String profileImage = c.getString("profile_img");

                String otp_status = c.getString("otp_status");
                String member = c.getString("member");
                String registered_on = c.getString("registered_on");

                userArrayList.add(new ItemUser(userId, userName, userEmail, userPhone, userGender, "", "", profileImage, otp_status,member,registered_on));
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, postArrayList, dataArrayList, userArrayList, arrayListGallery, isBlockShop);
        super.onPostExecute(s);
    }
}