package nemosofts.classified.activity.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.classified.R;
import nemosofts.classified.activity.MainActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    boolean isPurchased = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("isPurchased")) {
            isPurchased = true;
        }

        findViewById(R.id.tv_btn_sale).setOnClickListener(v -> onBackPressed());
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_payment_success;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public void onBackPressed() {
        if (isPurchased) {
            Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}