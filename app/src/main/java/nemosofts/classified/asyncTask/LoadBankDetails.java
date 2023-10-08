package nemosofts.classified.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.BankDetailsListener;
import nemosofts.classified.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadBankDetails extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final BankDetailsListener successListener;
    private String success = "0", message = "", name = "", account_number = "", details = "";

    public LoadBankDetails(BankDetailsListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
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
                if (!c.has(Callback.TAG_SUCCESS)) {
                    name  = c.getString("bank_name");
                    account_number  = c.getString("bank_account_number");
                    details  = c.getString("bank_account_details");

                } else {
                    success = c.getString(Callback.TAG_SUCCESS);
                    message = c.getString(Callback.TAG_MSG);
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
        successListener.onEnd(s, success, message, name, account_number, details);
        super.onPostExecute(s);
    }
}