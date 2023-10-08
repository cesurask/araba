package nemosofts.classified.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.view.PinView;

public class OTPReceiveActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private String verificationId;
    private ProgressDialog progressDialog;
    private TextView btn_continue;
    private ProgressBar pb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> finish());
        }

        progressDialog = new ProgressDialog(this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        TextView tv_code_send= findViewById(R.id.tv_code_send);
        tv_code_send.setText(getResources().getString(R.string.code_is_sent_to)+ getIntent().getStringExtra("mobile"));

        verificationId = getIntent().getStringExtra("verificationId");

        PinView pinView = findViewById(R.id.pinView);

        findViewById(R.id.tv_request_again).setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(getIntent().getStringExtra("mobile"))// Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // (optional) Activity for callback binding
                    // If no activity is passed, reCAPTCHA verification can not be used.
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(OTPReceiveActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            btn_continue.setBackgroundColor(ColorUtils.colorBorder(OTPReceiveActivity.this));
                        }

                        @Override
                        public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            verificationId = newVerificationId;
                            btn_continue.setBackgroundColor(ColorUtils.colorAccent(OTPReceiveActivity.this));
                            super.onCodeSent(newVerificationId, forceResendingToken);
                        }
                    })
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        pb = findViewById(R.id.pb);
        btn_continue = findViewById(R.id.tv_btn_verify);
        btn_continue.setOnClickListener(view -> {
            if (pinView.getText().toString().trim().isEmpty()){
                pinView.setLineColor(Color.RED);
                Toast.makeText(this,getResources().getString(R.string.please_enter_valid_code), Toast.LENGTH_SHORT).show();
            } else {
                if (verificationId != null){
                    btn_continue.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.VISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, pinView.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            pinView.setLineColor(Color.GREEN);
                            setVerifiedSave();
                        }else {
                            pinView.setLineColor(Color.RED);
                            btn_continue.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void setVerifiedSave() {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadProfile = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            Callback.isProfileUpdate = true;
                            finish();
                        } else {
                            Toast.makeText(OTPReceiveActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OTPReceiveActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_OTP_VERIFIED, 0, "", "", "", "", sharedPref.getUserId(), "", "", getIntent().getStringExtra("mobile"), "", "", "", "", null, null));
            loadProfile.execute();
        } else {
            Toast.makeText(OTPReceiveActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_otp_receive;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}