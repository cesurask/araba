package nemosofts.classified.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.StripeTokenListener;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.Helper;

public class LoadStripeToken extends AsyncTask<String, String, String> {

    private final Helper helper;
    private final StripeTokenListener tokenListener;
    private String message = "", token = "", amount = "", verifyStatus = "0";


    public LoadStripeToken(Context context, String amount, StripeTokenListener tokenListener) {
        this.tokenListener = tokenListener;
        this.amount = amount;
        helper = new Helper(context);
    }

    @Override
    protected void onPreExecute() {
        tokenListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, helper.getAPIRequest(Callback.STRIPE_TOKEN_URL, 0, "", "", amount, "", "", "", "", "", "","","","", null, null));
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Callback.TAG_ROOT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(Callback.TAG_ROOT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    if (!c.has(Callback.TAG_SUCCESS)) {
                        token = c.getString("stripe_payment_token");
                    } else {
                        verifyStatus = c.getString(Callback.TAG_SUCCESS);
                        message = c.getString(Callback.TAG_MSG);
                    }
                }
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        tokenListener.onEnd(s, verifyStatus, message,token);
        super.onPostExecute(s);
    }
}