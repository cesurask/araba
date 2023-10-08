package nemosofts.classified.activity.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.squareup.picasso.Picasso;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadPaymentSettings;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.PaymentSettingsListener;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.Helper;

public class SelectPlanActivity extends AppCompatActivity {

    private Helper helper;
    private String planName, planPrice, planDays;
    private String planName2, planPrice2, planDays2;
    private String planName3, planPrice3, planDays3;
    private Boolean isExpandable, isExpandable2, isExpandable3;
    private int isExpandable_int, isExpandable_int2, isExpandable_int3;
    private String id="", name="", money="", time="", image="", cat_name="";
    private ImageView post_img;
    private TextView post_text, post_city, post_pri, post_date;
    private TextView tv_planPrice, tv_name_date;
    private TextView tv_planPrice2, tv_name_date2;
    private TextView tv_planPrice3, tv_name_date3;
    private TextView tv_totalPrice_new, tv_Price_ta;

    private NestedScrollView nw_plan;
    private ProgressBar progressBar;
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        planName = getIntent().getStringExtra("planName");
        planPrice = getIntent().getStringExtra("planPrice");
        planDays = getIntent().getStringExtra("planDays");
        isExpandable = getIntent().getBooleanExtra("isExpandable",false);

        planName2 = getIntent().getStringExtra("planName2");
        planPrice2 = getIntent().getStringExtra("planPrice2");
        planDays2 = getIntent().getStringExtra("planDays2");
        isExpandable2 = getIntent().getBooleanExtra("isExpandable2",false);

        planName3 = getIntent().getStringExtra("planName3");
        planPrice3 = getIntent().getStringExtra("planPrice3");
        planDays3 = getIntent().getStringExtra("planDays3");
        isExpandable3 = getIntent().getBooleanExtra("isExpandable3",false);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        money = getIntent().getStringExtra("money");
        time = getIntent().getStringExtra("time");
        image = getIntent().getStringExtra("image");
        cat_name = getIntent().getStringExtra("cat_name");

        helper = new Helper(this);

        nw_plan = findViewById(R.id.nw_plan);
        progressBar = findViewById(R.id.pb);

        post_img = findViewById(R.id.iv_promotion_ads);
        post_text = findViewById(R.id.tv_promotion_ads);
        post_city = findViewById(R.id.tv_promotion_ads_city);
        post_pri = findViewById(R.id.tv_promotion_ads_pri);
        post_date = findViewById(R.id.tv_promotion_ads_date);

        tv_planPrice = findViewById(R.id.tv_planPrice);
        tv_name_date = findViewById(R.id.tv_name_date);
        tv_planPrice2 = findViewById(R.id.tv_planPrice2);
        tv_name_date2 = findViewById(R.id.tv_name_date2);
        tv_planPrice3 = findViewById(R.id.tv_planPrice3);
        tv_name_date3 = findViewById(R.id.tv_name_date3);
        tv_totalPrice_new = findViewById(R.id.tv_totalPrice_new);
        tv_Price_ta = findViewById(R.id.tv_Price_ta);

        findViewById(R.id.ll_speed_view).setVisibility(Boolean.TRUE.equals(isExpandable) ? View.VISIBLE : View.GONE);
        double totalPrice = 0.00;
        if (Boolean.FALSE.equals(isExpandable)){
            planPrice = String.valueOf(totalPrice);
        }

        findViewById(R.id.ll_top_ad_view).setVisibility(Boolean.TRUE.equals(isExpandable2) ? View.VISIBLE : View.GONE);
        if (Boolean.FALSE.equals(isExpandable2)){
            planPrice2 = String.valueOf(totalPrice);
        }

        findViewById(R.id.ll_spotlight_view).setVisibility(Boolean.TRUE.equals(isExpandable3) ? View.VISIBLE : View.GONE);
        if (Boolean.FALSE.equals(isExpandable3)){
            planPrice3 = String.valueOf(totalPrice);
        }

        getPostData();
        if (Callback.currencyCode.isEmpty()){
            getPaymentSetting();
        } else {
            displayData();
        }

        RadioGroup radioGroup = findViewById(R.id.radioGrp);

        findViewById(R.id.tv_btn_continue).setOnClickListener(v -> {
            int radioSelected = radioGroup.getCheckedRadioButtonId();
            if (radioSelected != -1) {
                switch (radioSelected) {
                    case R.id.rdPaypal:
                        Intent intentPayPal = new Intent(SelectPlanActivity.this, PayPalActivity.class);
                        intentPayPal.putExtra("isExpandable", isExpandable_int);
                        intentPayPal.putExtra("isExpandable2", isExpandable_int2);
                        intentPayPal.putExtra("isExpandable3", isExpandable_int3);
                        intentPayPal.putExtra("planDays", planDays);
                        intentPayPal.putExtra("planDays2", planDays2);
                        intentPayPal.putExtra("planDays3", planDays3);

                        intentPayPal.putExtra("planPrice", tv_totalPrice_new.getText().toString());
                        intentPayPal.putExtra("planCurrency", Callback.currencyCode);
                        intentPayPal.putExtra("planGateway", "Paypal");
                        intentPayPal.putExtra("planGatewayText", getString(R.string.paypal));
                        intentPayPal.putExtra("isSandbox", Callback.payPalSandbox);
                        intentPayPal.putExtra("payPalClientId", Callback.payPalClientId);
                        intentPayPal.putExtra("post_id", id);
                        startActivity(intentPayPal);
                        break;

                    case R.id.rdStripe:
                        Intent intentStripe = new Intent(SelectPlanActivity.this, StripeActivity.class);
                        intentStripe.putExtra("isExpandable", isExpandable_int);
                        intentStripe.putExtra("isExpandable2", isExpandable_int2);
                        intentStripe.putExtra("isExpandable3", isExpandable_int3);
                        intentStripe.putExtra("planDays", planDays);
                        intentStripe.putExtra("planDays2", planDays2);
                        intentStripe.putExtra("planDays3", planDays3);

                        intentStripe.putExtra("planPrice", tv_totalPrice_new.getText().toString());
                        intentStripe.putExtra("planCurrency",Callback.currencyCode);
                        intentStripe.putExtra("planGateway", "Stripe");
                        intentStripe.putExtra("planGatewayText", getString(R.string.stripe));
                        intentStripe.putExtra("stripePublisherKey", Callback.stripePublisherKey);
                        intentStripe.putExtra("post_id", id);
                        startActivity(intentStripe);
                        break;

                    case R.id.rdPayStack:
                        Intent intentPayStack = new Intent(SelectPlanActivity.this, PayStackActivity.class);
                        intentPayStack.putExtra("isExpandable", isExpandable_int);
                        intentPayStack.putExtra("isExpandable2", isExpandable_int2);
                        intentPayStack.putExtra("isExpandable3", isExpandable_int3);
                        intentPayStack.putExtra("planDays", planDays);
                        intentPayStack.putExtra("planDays2", planDays2);
                        intentPayStack.putExtra("planDays3", planDays3);

                        intentPayStack.putExtra("planPrice", tv_totalPrice_new.getText().toString());
                        intentPayStack.putExtra("planCurrency", Callback.currencyCode);
                        intentPayStack.putExtra("planGateway", "Paystack");
                        intentPayStack.putExtra("planGatewayText", getString(R.string.pay_stack));
                        intentPayStack.putExtra("payStackPublicKey", Callback.payStackPublicKey);
                        intentPayStack.putExtra("post_id", id);
                        startActivity(intentPayStack);
                        break;

                    case R.id.rdBankPay:
                        Intent intentRazor = new Intent(SelectPlanActivity.this, BankPayActivity.class);
                        intentRazor.putExtra("isExpandable", isExpandable_int);
                        intentRazor.putExtra("isExpandable2", isExpandable_int2);
                        intentRazor.putExtra("isExpandable3", isExpandable_int3);
                        intentRazor.putExtra("planDays", planDays);
                        intentRazor.putExtra("planDays2", planDays2);
                        intentRazor.putExtra("planDays3", planDays3);

                        intentRazor.putExtra("planName", planName);
                        intentRazor.putExtra("planPrice", tv_totalPrice_new.getText().toString());
                        intentRazor.putExtra("planCurrency", Callback.currencyCode);
                        intentRazor.putExtra("planGateway", "Bank Transfer");
                        intentRazor.putExtra("planGatewayText", getString(R.string.bank_pay));
                        intentRazor.putExtra("razorPayKey", "");
                        intentRazor.putExtra("post_id", id);
                        startActivity(intentRazor);
                        break;
                    default:
                        break;
                }
            } else {
                Toast.makeText(SelectPlanActivity.this, getString(R.string.select_gateway), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPaymentSetting() {
        if (helper.isNetworkAvailable()) {
            LoadPaymentSettings loadPaymentSettings = new LoadPaymentSettings(this, new PaymentSettingsListener() {
                @Override
                public void onStart() {
                    nw_plan.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            displayData();
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
            loadPaymentSettings.execute();
        } else {
            nw_plan.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getPostData() {
        Picasso.get()
                .load(image)
                .placeholder(R.color.load_color_4)
                .into(post_img);

        post_text.setText(name);
        post_city.setText(cat_name);
        post_pri.setText(Callback.currency_code + " " + ApplicationUtil.formatCurrency(money));
        post_date.setText(time);
    }

    @SuppressLint("SetTextI18n")
    private void displayData() {
        isExpandable_int = Boolean.TRUE.equals(isExpandable) ? 1 : 0;
        isExpandable_int2 = Boolean.TRUE.equals(isExpandable2) ? 1 : 0;
        isExpandable_int3 = Boolean.TRUE.equals(isExpandable3) ? 1 : 0;

        tv_planPrice.setText(Callback.currencyCode + " " + planPrice);
        tv_name_date.setText(planName + " for " + planDays);

        tv_planPrice2.setText(Callback.currencyCode + " " + planPrice2);
        tv_name_date2.setText(planName2 + " for " + planDays2);

        tv_planPrice3.setText(Callback.currencyCode + " " + planPrice3);
        tv_name_date3.setText(planName3 +" for " + planDays3);

        tv_Price_ta.setText(Callback.currencyCode);

        double result = Double.parseDouble(planPrice) + Double.parseDouble(planPrice2) + Double.parseDouble(planPrice3);
        String finalResult = Double.toString(result);
        tv_totalPrice_new.setText(finalResult+"0");

        findViewById(R.id.rdPaypal).setVisibility(Boolean.TRUE.equals(Callback.payPal) ? View.VISIBLE : View.GONE);
        findViewById(R.id.viewPaypal).setVisibility(Boolean.TRUE.equals(Callback.payPal) ? View.VISIBLE : View.GONE);

        findViewById(R.id.rdStripe).setVisibility(Boolean.TRUE.equals(Callback.stripe) ? View.VISIBLE : View.GONE);
        findViewById(R.id.viewStripe).setVisibility(Boolean.TRUE.equals(Callback.stripe) ? View.VISIBLE : View.GONE);

        findViewById(R.id.rdPayStack).setVisibility(Boolean.TRUE.equals(Callback.payStack) ? View.VISIBLE : View.GONE);
        findViewById(R.id.viewPayStack).setVisibility(Boolean.TRUE.equals(Callback.payStack) ? View.VISIBLE : View.GONE);

        findViewById(R.id.rdBankPay).setVisibility(Boolean.TRUE.equals(Callback.bankPay) ? View.VISIBLE : View.GONE);

        if (Boolean.TRUE.equals(!Callback.payPal && !Callback.stripe && !Callback.bankPay) && Boolean.TRUE.equals(!Callback.payStack)) {
            nw_plan.setVisibility(View.GONE);
        } else {
            nw_plan.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_select_plan;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}