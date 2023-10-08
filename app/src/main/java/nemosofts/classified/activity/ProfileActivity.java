package nemosofts.classified.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import com.squareup.picasso.Picasso;

import nemosofts.classified.BuildConfig;
import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadProfile;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.ProfileListener;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class ProfileActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private TextView textView_name, textView_email;
    private ImageView iv_profile;

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

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        progressDialog = new ProgressDialog(this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        textView_name = findViewById(R.id.tv_profile_name);
        textView_email = findViewById(R.id.tv_profile_email);
        iv_profile = findViewById(R.id.iv_profile);

        if (sharedPref.isLogged() && !sharedPref.getUserId().equals("")) {
            loadUserProfile();
        } else {
            helper.clickLogin();
        }

        findViewById(R.id.rl_profile).setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)));
        findViewById(R.id.iv_notifications).setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, NotificationActivity.class)));
        findViewById(R.id.ll_policy).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"privacy_policy.php");
            intent.putExtra("page_title", getResources().getString(R.string.privacy_policy));
            ActivityCompat.startActivity(ProfileActivity.this, intent, null);
        });
        findViewById(R.id.ll_terms).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"terms.php");
            intent.putExtra("page_title", getResources().getString(R.string.terms_and_conditions));
            ActivityCompat.startActivity(ProfileActivity.this, intent, null);
        });
        findViewById(R.id.ll_trash).setOnClickListener(view -> showDeleteDialog());
        findViewById(R.id.ll_logout).setOnClickListener(view -> helper.clickLogin());
        findViewById(R.id.vw_otp).setVisibility(sharedPref.getOtpStatus().equals("1") ? View.GONE : View.VISIBLE);
        findViewById(R.id.ll_otp).setVisibility(sharedPref.getOtpStatus().equals("1") ? View.GONE : View.VISIBLE);
        findViewById(R.id.ll_otp).setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, OTPActivity.class)));

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView);
    }

    private void showDeleteDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_do).setOnClickListener(view -> {
            dialog.dismiss();
            loadDelete();
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void loadDelete() {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        helper.clickLogin();
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_ACCOUNT_DELETE, 0, "", "", "", "",sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        if (helper.isNetworkAvailable()) {
            LoadProfile loadProfile = new LoadProfile(new ProfileListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String isApiSuccess, String message, String user_id, String user_name, String email, String mobile, String gender, String profile, String otp_status, String member, String registered_on) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (isApiSuccess.equals("1")) {
                            sharedPref.setUserName(user_name);
                            sharedPref.setEmail(email);
                            sharedPref.setUserMobile(mobile);
                            sharedPref.setOtpStatus(otp_status);
                            sharedPref.setProfileImages(profile);
                            setVariables();
                        } else {
                            helper.logout(ProfileActivity.this, sharedPref);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_PROFILE, 0, "", "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            loadProfile.execute();
        } else {
            Toast.makeText(ProfileActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    public void setVariables() {
        textView_name.setText(sharedPref.getUserName());
        textView_email.setText(sharedPref.getEmail());
        if (!sharedPref.getProfileImages().isEmpty()){
            try {
                findViewById(R.id.pb_iv_profile).setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(sharedPref.getProfileImages())
                        .placeholder(R.drawable.user_photo)
                        .into(iv_profile, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                findViewById(R.id.pb_iv_profile).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                findViewById(R.id.pb_iv_profile).setVisibility(View.GONE);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        if (Boolean.TRUE.equals(Callback.isProfileUpdate)) {
            Callback.isProfileUpdate = false;
            loadUserProfile();
        }
        super.onResume();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}