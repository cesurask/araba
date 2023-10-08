package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.SocialLoginListener;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadRegister extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final SocialLoginListener socialLoginListener;
    private String success = "0", message = "";
    private String user_id = "", user_name = "", email = "", auth_id = "", otp_status = "0", member="", registered_on ="";

    public LoadRegister(SocialLoginListener socialLoginListener, RequestBody requestBody) {
        this.socialLoginListener = socialLoginListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        socialLoginListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Callback.TAG_SUCCESS);
                message = c.getString(Callback.TAG_MSG);
                if(c.has("user_id")) {
                    user_id = c.getString("user_id");
                    user_name = c.getString("user_name");
                    auth_id = c.getString("auth_id");
                    email = c.getString( "user_email");

                    otp_status = c.getString("otp_status");
                    member = c.getString("member");
                    registered_on = c.getString("registered_on");
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
        socialLoginListener.onEnd(s, success, message, user_id, user_name, email, auth_id, otp_status, member, registered_on);
        super.onPostExecute(s);
    }
}