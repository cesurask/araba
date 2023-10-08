package nemosofts.classified.activity.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStripeToken;
import nemosofts.classified.asyncTask.Transaction;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.StripeTokenListener;
import nemosofts.classified.utils.helper.Helper;

public class StripeActivity extends AppCompatActivity {

    private Helper helper;
    private String planPrice;
    private String planGateway;
    private String stripePublisherKey;
    private String post_id;
    private String planDays, planDays2, planDays3;
    private int isExpandable, isExpandable2, isExpandable3;
    private CardMultilineWidget cardMultilineWidget;
    private PaymentResultCallback paymentResultCallback;
    private Stripe stripe;
    private ProgressDialog pDialog;
    private String paymentIntentClientSecret = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        pDialog = new ProgressDialog(this);
        cardMultilineWidget = findViewById(R.id.cardInputWidget_checkout);

        paymentResultCallback = new PaymentResultCallback(StripeActivity.this);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        planDays = intent.getStringExtra("planDays");
        planDays2 = intent.getStringExtra("planDays2");
        planDays3 = intent.getStringExtra("planDays3");
        isExpandable = intent.getIntExtra("isExpandable",0);
        isExpandable2 = intent.getIntExtra("isExpandable2",0);
        isExpandable3 = intent.getIntExtra("isExpandable3",0);

        planPrice = intent.getStringExtra("planPrice");
        String planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        String planGateWayText = intent.getStringExtra("planGatewayText");
        stripePublisherKey = intent.getStringExtra("stripePublisherKey");

        Button btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        btnPay.setOnClickListener(view -> {
            if (helper.isNetworkAvailable()) {
                getToken();
            } else {
                Toast.makeText(StripeActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCheckout() {
        showProgressDialog();
        PaymentMethodCreateParams params = cardMultilineWidget.getPaymentMethodCreateParams();
        if (params != null) {
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            stripe = new Stripe(StripeActivity.this, stripePublisherKey);
            stripe.confirmPayment(this, confirmParams);
        } else {
            dismissProgressDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, paymentResultCallback);
    }


    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {

        StripeActivity activity;

        @NonNull
        private final WeakReference<StripeActivity> activityRef;

        PaymentResultCallback(@NonNull StripeActivity activity) {
            activityRef = new WeakReference<>(activity);
            this.activity = activityRef.get();
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    JSONObject jsonObject = new JSONObject(gson.toJson(paymentIntent));
                    String paymentId = jsonObject.getString("id");
                    new Transaction(activity).purchasedItem(activity.post_id, paymentId, activity.planGateway,activity.isExpandable,activity.isExpandable2,activity.isExpandable3,activity.planDays,activity.planDays2,activity.planDays3,activity.planPrice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                activity.showError(paymentIntent.getLastPaymentError().getMessage());
            }
            activity.dismissProgressDialog();
        }

        @Override
        public void onError(@NonNull Exception e) {
            final StripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.showError(e.getMessage());
            activity.dismissProgressDialog();
        }
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showError(String Title) {
        new AlertDialog.Builder(new ContextThemeWrapper(StripeActivity.this, R.style.ThemeDialog))
                .setTitle(getString(R.string.stripe_payment_error_1))
                .setMessage(Title)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .show();
    }

    private void getToken() {
        LoadStripeToken loadStripeToken = new LoadStripeToken(StripeActivity.this, planPrice, new StripeTokenListener() {
            @Override public void onStart() {
                showProgressDialog();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, String token) {
                if (success.equals("1")) {
                    if (!verifyStatus.equals("-1")) {
                        paymentIntentClientSecret = token;
                        startCheckout();
                    } else {
                        dismissProgressDialog();
                        showError(getString(R.string.stripe_token_error));
                    }
                } else {
                    dismissProgressDialog();
                    showError(getString(R.string.stripe_token_error));
                }
            }
        });
        loadStripeToken.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_stripe;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT_N();
    }
}