package nemosofts.classified.asyncTask;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.HomeListener;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.item.ItemPostHome;
import nemosofts.classified.item.ItemSpotlight;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.DBHelper;
import okhttp3.RequestBody;

public class LoadHome extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context ctx;
    private final RequestBody requestBody;
    private final HomeListener homeListener;
    private final ArrayList<ItemPostHome> arrayListPost = new ArrayList<>();
    private String message = "";
    private String successAPI = "1";
    private final DBHelper dbHelper;

    public LoadHome(Context ctx, HomeListener homeListener, RequestBody requestBody) {
        this.ctx = ctx;
        this.homeListener = homeListener;
        this.requestBody = requestBody;
        dbHelper = new DBHelper(ctx);
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllCategory();
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);

            try {
                JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);

                ItemPostHome itemPostHome;
                String postTitle, postType, postId;

                if (jsonObject.has("category_data")) {

                    postTitle = ctx.getString(R.string.for_you);
                    postType = "category";
                    itemPostHome = new ItemPostHome("", postTitle, postType, "category");

                    JSONArray jsonArrayCat = jsonObject.getJSONArray("category_data");
                    Log.d(TAG, String.valueOf(jsonArrayCat));
                    for (int i = 0; i < jsonArrayCat.length(); i++) {
                        JSONObject objJson = jsonArrayCat.getJSONObject(i);

                        String id = objJson.getString("cid");
                        String name = objJson.getString("category_name");
                        String image = objJson.getString("category_image");

                        ItemCategory objItem = new ItemCategory(id, name, image);
                        dbHelper.addToCategoryList(objItem);
                    }

                    ArrayList<ItemCategory> arrayListCategory = dbHelper.getCategory("8");
                    if (!arrayListCategory.isEmpty()){
                        itemPostHome.setArrayListCategories(arrayListCategory);
                        arrayListPost.add(itemPostHome);
                    }
                }

                if (jsonObject.has("spotlight_data")) {

                    JSONArray jsonArraySpotlight = jsonObject.getJSONArray("spotlight_data");

                    postTitle = "Spotlight Post";
                    postType = "spotlight";
                    itemPostHome = new ItemPostHome("", postTitle, postType, "spotlight");

                    ArrayList<ItemSpotlight> arrayListSpotlight = new ArrayList<>();
                    for (int i = 0; i < jsonArraySpotlight.length(); i++) {
                        JSONObject objJsonSpotlight = jsonArraySpotlight.getJSONObject(i);

                        String id = objJsonSpotlight.getString("id");
                        String user_id = objJsonSpotlight.getString("user_id");
                        String title = objJsonSpotlight.getString("title");
                        String money = objJsonSpotlight.getString("money");
                        String cityName = objJsonSpotlight.getString("city_name");
                        String date_time = objJsonSpotlight.getString("date_time");

                        String image_1 = objJsonSpotlight.getString("image_1").replace(" ", "%20");
                        if (image_1.equals("")) {
                            image_1 = "null";
                        }
                        String image_2 = objJsonSpotlight.getString("image_2").replace(" ", "%20");
                        String image_3 = objJsonSpotlight.getString("image_3").replace(" ", "%20");

                        arrayListSpotlight.add(new ItemSpotlight(id,user_id,title,money,cityName,date_time,image_1,image_2,image_3));
                    }
                    itemPostHome.setArrayListSpotlight(arrayListSpotlight);
                    arrayListPost.add(itemPostHome);
                }

                if (jsonObject.has("post_data")) {

                    JSONArray jsonArrayLatest = jsonObject.getJSONArray("post_data");

                    postTitle = ctx.getString(R.string.fresh_recommendations);
                    postType = "Latest";
                    itemPostHome = new ItemPostHome("", postTitle, postType, "Latest");

                    ArrayList<ItemData> arrayListLatest = new ArrayList<>();
                    for (int i = 0; i < jsonArrayLatest.length(); i++) {
                        JSONObject objJsonLatest = jsonArrayLatest.getJSONObject(i);

                        String id = objJsonLatest.getString("id");
                        String user_id = objJsonLatest.getString("user_id");
                        String title = objJsonLatest.getString("title");
                        String cat_name = objJsonLatest.getString("cat_name");
                        String money = objJsonLatest.getString("money");
                        String date_time = objJsonLatest.getString("date_time");
                        String post_image = objJsonLatest.getString("post_image").replace(" ", "%20");
                        if (post_image.equals("")) {
                            post_image = "null";
                        }
                        String sold_out = objJsonLatest.getString("sold_out");

                        arrayListLatest.add(new ItemData(id, user_id, title, cat_name, money, date_time, post_image, false, false, sold_out));

                    }
                    itemPostHome.setArrayListPost(arrayListLatest);
                    arrayListPost.add(itemPostHome);
                }

                if (jsonObject.has("home_sections")) {

                    JSONArray jsonArraySection = jsonObject.getJSONArray("home_sections");

                    for (int j = 0; j < jsonArraySection.length(); j++) {

                        JSONObject jObjHome = jsonArraySection.getJSONObject(j);

                        postId = jObjHome.getString("home_id");
                        postTitle = jObjHome.getString("home_title");
                        postType = jObjHome.getString("home_type");
                        itemPostHome = new ItemPostHome(postId, postTitle, postType, "sections");

                        if (jObjHome.has("home_content")) {
                            JSONArray jsonArrayHomeContent = jObjHome.getJSONArray("home_content");

                            ArrayList<ItemData> arrayList = new ArrayList<>();
                            for (int i = 0; i < jsonArrayHomeContent.length(); i++) {
                                JSONObject objJson = jsonArrayHomeContent.getJSONObject(i);

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
                            itemPostHome.setArrayListPost(arrayList);
                            arrayListPost.add(itemPostHome);
                        }
                    }
                }

            } catch (Exception e) {
                JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                successAPI = jsonObject.getString(Callback.TAG_SUCCESS);
                message = jsonObject.getString(Callback.TAG_MSG);

                e.printStackTrace();
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        homeListener.onEnd(s, message, arrayListPost);
        super.onPostExecute(s);
    }

}