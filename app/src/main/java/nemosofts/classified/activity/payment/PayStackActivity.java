package nemosofts.classified.activity.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.tenbis.library.consts.CardType;
import com.tenbis.library.listeners.OnCreditCardStateChanged;
import com.tenbis.library.models.CreditCard;
import com.tenbis.library.views.CompactCreditCardInput;

import org.jetbrains.annotations.NotNull;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import nemosofts.classified.R;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class PayStackActivity extends AppCompatActivity implements OnCreditCardStateChanged {

    private Helper helper;
    private SharedPref sharedPref;
    private String planPrice;
    private String planGateway;
    private String post_id;
    private String planDays, planDays2, planDays3;
    private int isExpandable, isExpandable2, isExpandable3;
    private ProgressDialog pDialog;
    private CreditCard creditCard;
    private boolean isCardValid = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaystackSdk.initialize(this);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.payment));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        pDialog = new ProgressDialog(this, R.style.ThemeDialog);

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
        String payStackPublicKey = intent.getStringExtra("payStackPublicKey");

        PaystackSdk.setPublicKey(payStackPublicKey);

        Button btnPay = findViewById(R.id.btn_pay);
        String payString = getString(R.string.pay_via, planPrice, planCurrency, planGateWayText);
        btnPay.setText(payString);

        CompactCreditCardInput creditCardInput = findViewById(R.id.compact_credit_card_input);
        creditCardInput.addOnCreditCardStateChangedListener(this);

        btnPay.setOnClickListener(view -> {
            if (helper.isNetworkAvailable()) {
                if (isCardValid && creditCard != null) {
                    performCharge(sharedPref.getEmail());
                }
            } else {
                Toast.makeText(PayStackActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void performCharge(String email) {
        showProgressDialog();
        Charge charge = new Charge();
        charge.setCard(loadCardFromForm());
        charge.setEmail(email);
        double amount = Double.parseDouble(planPrice);
        charge.setAmount((int) amount * 100);
        PaystackSdk.chargeCard(PayStackActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                dismissProgressDialog();
                if (helper.isNetworkAvailable()) {
                    new  nemosofts.classified.asyncTask.Transaction(PayStackActivity.this).purchasedItem(post_id, transaction.getReference(), planGateway,isExpandable,isExpandable2,isExpandable3,planDays,planDays2,planDays3,planPrice);
                } else {
                    showError(getString(R.string.conne_msg1));
                }
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void showLoading(Boolean isProcessing) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                dismissProgressDialog();
                showError(error.getMessage());
            }
        });
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
        new AlertDialog.Builder(new ContextThemeWrapper(PayStackActivity.this, R.style.ThemeDialog))
                .setTitle(getString(R.string.pay_stack_error_1))
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

    private Card loadCardFromForm() {
        return new Card.Builder(creditCard.getCardNumber(), creditCard.getExpiryMonth(), creditCard.getExpiryYear(), creditCard.getCvv()).build();
    }

    @Override
    public void onCreditCardCvvValid(@NotNull String s) {

    }

    @Override
    public void onCreditCardExpirationDateValid(int i, int i1) {

    }

    @Override
    public void onCreditCardNumberValid(@NotNull String s) {

    }

    @Override
    public void onCreditCardTypeFound(@NotNull CardType cardType) {

    }

    @Override
    public void onCreditCardValid(@NotNull CreditCard creditCard) {
        isCardValid = true;
        this.creditCard = creditCard;
    }

    @Override
    public void onInvalidCardTyped() {
        isCardValid = false;
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_pay_stack;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT_N();
    }
}