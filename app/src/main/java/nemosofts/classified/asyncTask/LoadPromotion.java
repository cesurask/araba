package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.PromotionListener;
import nemosofts.classified.item.ItemProActive;
import nemosofts.classified.item.ItemPromotions;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadPromotion extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final PromotionListener listener;
    private final ArrayList<ItemPromotions> promotionsArrayList = new ArrayList<>();
    private final ArrayList<ItemProActive> activeArrayList = new ArrayList<>();

    public LoadPromotion(PromotionListener listener, RequestBody requestBody) {
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

            JSONArray jsonArrayHome1 = jsonObject.getJSONArray("subscription_list");
            for (int i = 0; i < jsonArrayHome1.length(); i++) {
                JSONObject objJson = jsonArrayHome1.getJSONObject(i);

                String id = objJson.getString("id");
                String plan_name = objJson.getString("plan_name");
                String plan_details = objJson.getString("plan_details");

                String plan_days = objJson.getString("plan_days");
                String plan_duration = objJson.getString("plan_duration");
                String plan_duration_type = objJson.getString("plan_duration_type");
                String plan_price = objJson.getString("plan_price");
                String days = objJson.getString("days");

                String plan_days_2 = objJson.getString("plan_days_2");
                String plan_duration_2 = objJson.getString("plan_duration_2");
                String plan_duration_type_2 = objJson.getString("plan_duration_type_2");
                String plan_price_2 = objJson.getString("plan_price_2");
                String days2 = objJson.getString("days2");

                String plan_days_3 = objJson.getString("plan_days_3");
                String plan_duration_3 = objJson.getString("plan_duration_3");
                String plan_duration_type_3 = objJson.getString("plan_duration_type_3");
                String plan_price_3 = objJson.getString("plan_price_3");
                String days3 = objJson.getString("days3");

                String promote_image = objJson.getString("promote_image");

                promotionsArrayList.add(new ItemPromotions(id, plan_name, plan_details,
                        plan_days, plan_duration, plan_duration_type, plan_price, days,
                        plan_days_2, plan_duration_2, plan_duration_type_2, plan_price_2, days2,
                        plan_days_3, plan_duration_3, plan_duration_type_3, plan_price_3, days3,
                        promote_image));
            }

            JSONArray jsonArrayHome3 = jsonObject.getJSONArray("subscription_type");
            for (int i = 0; i < jsonArrayHome3.length(); i++) {
                JSONObject objJson = jsonArrayHome3.getJSONObject(i);

                Boolean dailyBumpUp = Boolean.parseBoolean(objJson.getString("dailyBumpUp"));
                Boolean topAd = Boolean.parseBoolean(objJson.getString("topAd"));
                Boolean spotLight = Boolean.parseBoolean(objJson.getString("spotLight"));

                activeArrayList.add(new ItemProActive(dailyBumpUp, topAd, spotLight));
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, promotionsArrayList, activeArrayList);
        super.onPostExecute(s);
    }
}