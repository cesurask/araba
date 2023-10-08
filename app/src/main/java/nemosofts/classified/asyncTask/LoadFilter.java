package nemosofts.classified.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.FilterListener;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemSubCategory;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.DBHelper;
import okhttp3.RequestBody;

public class LoadFilter extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final FilterListener filterListener;
    private final ArrayList<ItemCity> arrayListCity = new ArrayList<>();
    private final ArrayList<ItemCategory> arrayListCategory = new ArrayList<>();
    private final ArrayList<ItemSubCategory> arrayListSubCategory = new ArrayList<>();
    private final DBHelper dbHelper;

    public LoadFilter(Context context, FilterListener filterListener, RequestBody requestBody) {
        this.filterListener = filterListener;
        this.requestBody = requestBody;
        dbHelper = new DBHelper(context);
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllSubCategory();
        dbHelper.removeAllCategory();
        dbHelper.removeAllCity();
        filterListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);

            JSONArray jsonArray = jsonObject.getJSONArray("city_data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                String id = objJson.getString("aid");
                String name = objJson.getString("city_name");

                ItemCity objItem = new ItemCity(id, name);
                arrayListCity.add(objItem);
                dbHelper.addToCityList(objItem);
            }

            JSONArray jsonArrayCat = jsonObject.getJSONArray("category_data");
            for (int i = 0; i < jsonArrayCat.length(); i++) {
                JSONObject objJson = jsonArrayCat.getJSONObject(i);

                String id = objJson.getString("cid");
                String name = objJson.getString("category_name");
                String image = objJson.getString("category_image");

                ItemCategory objItem = new ItemCategory(id, name, image);
                arrayListCategory.add(objItem);
                dbHelper.addToCategoryList(objItem);
            }

            JSONArray jsonArraySubCat = jsonObject.getJSONArray("sub_category_data");
            for (int i = 0; i < jsonArraySubCat.length(); i++) {
                JSONObject objJson = jsonArraySubCat.getJSONObject(i);

                String id = objJson.getString("sid");
                String catID = objJson.getString("sub_cat_id");
                String name = objJson.getString("sub_category_name");
                String image = objJson.getString("sub_category_image");

                ItemSubCategory objItem = new ItemSubCategory(id, catID, name, image);
                arrayListSubCategory.add(objItem);
                dbHelper.addToSubCategoryList(objItem);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        filterListener.onEnd(s, arrayListCity, arrayListCategory, arrayListSubCategory);
        super.onPostExecute(s);
    }
}