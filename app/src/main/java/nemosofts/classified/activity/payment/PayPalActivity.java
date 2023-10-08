package nemosofts.classified.activity.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.Transaction;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.utils.helper.Helper;

public class PayPalActivity extends AppCompatActivity {

    private Helper helper;
    private String planPrice;
    private String planCurrency;
    private String planGateway , post_id;
    private String planDays, planDays2, planDays3;
    private int isExpandable, isExpandable2, isExpandable3;
    private static final int REQUEST_PAYPAL_PAYMENT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(PayPalActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        planDays = intent.getStringExtra("planDays");
        planDays2 = intent.getStringExtra("planDays2");
        planDays3 = intent.getStringExtra("planDays3");
        isExpandable = intent.getIntExtra("isExpandable",0);
        isExpandable2 = intent.getIntExtra("isExpandable2",0);
        isExpandable3 = intent.getIntExtra("isExpandable3",0);

        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        String planGateWayText = intent.getStringExtra("planGatewayText");
        String payPalClientId = intent.getStringExtra("payPalClientId");
        boolean isSandbox = intent.getBooleanExtra("isSandbox", false);

        String CONFIG_ENVIRONMENT;
        if (isSandbox) {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        } else {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        }

        PayPalConfiguration config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(payPalClientId);

        Intent intentService = new Intent(this, PayPalService.class);
        intentService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intentService);

        Button btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        buyPayPal();

        btnPay.setOnClickListener(view -> buyPayPal());
    }

    private void buyPayPal() {
        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(planPrice), planCurrency, getString(R.string.paypal_display_website), PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intentPay = new Intent(PayPalActivity.this, PaymentActivity.class);
        intentPay.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intentPay, REQUEST_PAYPAL_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYPAL_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
                        if (helper.isNetworkAvailable()) {
                            new Transaction(PayPalActivity.this).purchasedItem(post_id, paymentId, planGateway,isExpandable,isExpandable2,isExpandable3,planDays,planDays2,planDays3,planPrice);
                        } else {
                            showError(getString(R.string.conne_msg1));
                        }

                    } catch (JSONException e) {
                        showError(getString(R.string.paypal_payment_error_1));
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showError(getString(R.string.paypal_payment_error_2));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                showError(getString(R.string.paypal_payment_error_3));
            }
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(new ContextThemeWrapper(PayPalActivity.this, R.style.ThemeDialog))
                .setTitle(getString(R.string.paypal_payment_error_4))
                .setMessage(Title)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_pay_pal;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT_N();
    }
}