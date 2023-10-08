package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.ProfileListener;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadProfile extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final ProfileListener profileListener;
    private String success = "0", userId = "", userName = "", userEmail = "", userPhone = "", userGender = "", profileImage = "", otp_status= "0", member= "0", registered_on= "";

    public LoadProfile(ProfileListener profileListener, RequestBody requestBody) {
        this.profileListener = profileListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        profileListener.onStart();
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
                userId = c.getString("user_id");
                userName = c.getString("user_name");
                userEmail = c.getString("user_email");
                userPhone = c.getString("user_phone");
                userGender = c.getString("user_gender");
                profileImage = c.getString("profile_img");

                otp_status = c.getString("otp_status");
                member = c.getString("member");
                registered_on = c.getString("registered_on");
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        profileListener.onEnd(s, success, "", userId, userName, userEmail, userPhone, userGender, profileImage, otp_status, member, registered_on);
        super.onPostExecute(s);
    }
}