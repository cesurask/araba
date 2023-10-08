package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import nemosofts.classified.R;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;

public class OTPActivity extends AppCompatActivity {

    private TextView pinView;
    private String inputString = "";
    private Vibrator vibrator;
    private ProgressBar pb;
    private TextView btn_continue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ImageView iv_line_animations = findViewById(R.id.iv_line_animations);
        iv_line_animations.setImageResource(R.drawable.line_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_line_animations.getDrawable();
        animationDrawable.start();

        pinView = findViewById(R.id.tv_number);
        pinView.setText("+");

        findViewById(R.id.tv_1).setOnClickListener(v -> calculate(1));
        findViewById(R.id.tv_2).setOnClickListener(v -> calculate(2));
        findViewById(R.id.tv_3).setOnClickListener(v -> calculate(3));
        findViewById(R.id.tv_4).setOnClickListener(v -> calculate(4));
        findViewById(R.id.tv_5).setOnClickListener(v -> calculate(5));
        findViewById(R.id.tv_6).setOnClickListener(v -> calculate(6));
        findViewById(R.id.tv_7).setOnClickListener(v -> calculate(7));
        findViewById(R.id.tv_8).setOnClickListener(v -> calculate(8));
        findViewById(R.id.tv_9).setOnClickListener(v -> calculate(9));
        findViewById(R.id.tv_0).setOnClickListener(v -> calculate(0));

        findViewById(R.id.rl_backspace).setOnClickListener(v -> {
            vibrator.vibrate(70);
            if (inputString.length() > 1 ) {
                inputString = inputString.substring(0, inputString.length() - 1);
                pinView.setText("+"+inputString);
            } else {
                pinView.setText("+");
                inputString = "";
                btn_continue.setBackgroundColor(ColorUtils.colorBorder(this));
            }
        });

        findViewById(R.id.rl_backspace).setOnLongClickListener(v -> {
            pinView.setText("+");
            inputString = "";
            btn_continue.setBackgroundColor(ColorUtils.colorBorder(this));
            return true;
        });

        pb = findViewById(R.id.pb);
        btn_continue = findViewById(R.id.tv_btn_continue);
        btn_continue.setOnClickListener(v -> {
            if (inputString.trim().isEmpty()){
                Toast.makeText(this,getResources().getString(R.string.enter_your_phone_number), Toast.LENGTH_SHORT).show();
            } else {
                sendOTP();
            }
        });

        btn_continue.setBackgroundColor(ColorUtils.colorBorder(this));
    }

    private void sendOTP() {
        pb.setVisibility(View.VISIBLE);
        btn_continue.setVisibility(View.INVISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(pinView.getText().toString())// Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    btn_continue.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    btn_continue.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    btn_continue.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), OTPReceiveActivity.class);
                    intent.putExtra("mobile", pinView.getText().toString());
                    intent.putExtra("verificationId", verificationId);
                    startActivity(intent);
                    finish();
                    super.onCodeSent(verificationId, forceResendingToken);
                }
            })
            .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @SuppressLint("SetTextI18n")
    private void calculate(int number) {
        vibrator.vibrate(70);
        if (inputString.equals("")){
            inputString = String.valueOf(number);
        } else {
            inputString +=  String.valueOf(number);
        }
        pinView.setText("+"+inputString);
        btn_continue.setBackgroundColor(ColorUtils.colorAccent(this));
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_otp;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}