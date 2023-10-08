package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.LoginListener;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadLogin extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final LoginListener listener;
    private String success = "0", message = "";
    private String user_id="", user_name = "", user_gender = "", profile_img = "", user_phone = "", otp_status = "0", member="", registered_on ="";

    public LoadLogin(LoginListener listener, RequestBody requestBody) {
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
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                success = jsonObject.getString(Callback.TAG_SUCCESS);
                if(success.equals("1")) {
                    user_id = jsonObject.getString("user_id");
                    user_name = jsonObject.getString("user_name");
                    user_phone = jsonObject.getString("user_phone");
                    user_gender = jsonObject.getString("user_gender");
                    profile_img = jsonObject.getString("profile_img");

                    otp_status = jsonObject.getString("otp_status");
                    member = jsonObject.getString("member");
                    registered_on = jsonObject.getString("registered_on");
                }
                message = jsonObject.getString(Callback.TAG_MSG);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, success, message, user_id, user_name, user_gender, user_phone, profile_img, otp_status, member, registered_on);
        super.onPostExecute(s);
    }

}