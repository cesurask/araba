package nemosofts.classified.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.PaymentSettingsListener;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.Helper;

public class LoadPaymentSettings extends AsyncTask<String, String, String> {

    private final Helper helper;
    private final PaymentSettingsListener listener;
    private String message = "", verifyStatus = "0";

    public LoadPaymentSettings(Context context, PaymentSettingsListener listener) {
        this.listener = listener;
        helper = new Helper(context);
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, helper.getAPIRequest(Callback.METHOD_PAY_DETAILS, 0, "", "", "", "", "", "", "", "", "","","","", null, null));
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Callback.TAG_ROOT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(Callback.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objJson = jsonArray.getJSONObject(i);
                    if (!objJson.has(Callback.TAG_SUCCESS)) {

                        Callback.currencyCode = objJson.getString("currency_code");

                        Callback.payPal = Boolean.parseBoolean(objJson.getString("paypal_payment_on_off"));
                        Callback.payPalSandbox = objJson.getString("paypal_mode").equals("sandbox");
                        Callback.payPalClientId = objJson.getString("paypal_client_id");

                        Callback.stripe  = Boolean.parseBoolean(objJson.getString("stripe_payment_on_off"));
                        Callback.stripePublisherKey = objJson.getString("stripe_publishable_key");

                        Callback.payStack  = Boolean.parseBoolean(objJson.getString("paystack_payment_on_off"));
                        Callback.payStackPublicKey = objJson.getString("paystack_public_key");

                        Callback.bankPay  = Boolean.parseBoolean(objJson.getString("bank_payment_on_off"));
                        Callback.bankName = objJson.getString("bank_name");
                        Callback.bankAccountNumber = objJson.getString("bank_account_number");
                        Callback.bankAccountDetails = objJson.getString("bank_account_details");

                    } else {
                        verifyStatus = objJson.getString(Callback.TAG_SUCCESS);
                        message = objJson.getString(Callback.TAG_MSG);
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
        listener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}