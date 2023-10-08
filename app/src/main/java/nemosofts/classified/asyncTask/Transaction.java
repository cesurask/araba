package nemosofts.classified.asyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import nemosofts.classified.R;
import nemosofts.classified.activity.payment.PaymentSuccessActivity;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class Transaction {

    private final ProgressDialog pDialog;
    private final Activity mContext;
    private final Helper helper;
    private final SharedPref sharedPref;

    public Transaction(Activity context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext, R.style.ThemeDialog);
        helper = new Helper(mContext);
        sharedPref = new SharedPref(mContext);
    }

    public void purchasedItem(String postId, String paymentId, String paymentGateway, int dailyBumpUp, int topAd, int spotLight, String dailyBumpUpDuration, String topAdDuration, String spotLightDuration, String amount) {
        LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
            @Override
            public void onStart() {
                showProgressDialog();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                switch (registerSuccess) {
                    case "1":
                        dismissProgressDialog();
                        ActivityCompat.finishAffinity(mContext);
                        Intent intentDashboard = new Intent(mContext, PaymentSuccessActivity.class);
                        intentDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentDashboard.putExtra("isPurchased", true);
                        mContext.startActivity(intentDashboard);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        break;
                    case "-1":
                        dismissProgressDialog();
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        dismissProgressDialog();
                        Toast.makeText(mContext, mContext.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }, helper.getAPIRequestTransaction(Callback.TRANSACTION_URL, postId , sharedPref.getUserId() , paymentId, paymentGateway, dailyBumpUp, topAd, spotLight, dailyBumpUpDuration, topAdDuration,spotLightDuration, amount, null));
        loadStatus.execute();
    }

    private void showProgressDialog() {
        pDialog.setMessage(mContext.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
