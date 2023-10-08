package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.nemosofts.envato.EnvatoProduct;
import androidx.nemosofts.envato.interfaces.EnvatoListener;
import androidx.nemosofts.theme.ColorUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nemosofts.classified.BuildConfig;
import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadAbout;
import nemosofts.classified.asyncTask.LoadLogin;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.AboutListener;
import nemosofts.classified.interfaces.LoginListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements EnvatoListener {

    private Helper helper;
    private SharedPref sharedPref;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideStatusBar();

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        pb = findViewById(R.id.pb_splash);

        findViewById(R.id.rl_splash).setBackgroundColor(ColorUtils.colorBackground(this));
        TextView splash_title = findViewById(R.id.tv_splash_title);
        splash_title.setTextColor(ColorUtils.colorTitle(this));

        loadAboutData();
    }

    private void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message){
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")){
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")){
                            setSaveData();
                        } else {
                            errorDialog(getString(R.string.err_unauthorized_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.err_server), getString(R.string.err_server_not_connected));
                    }
                }
            });
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_connected), getString(R.string.err_try_internet_connected));
        }
    }

    private void setSaveData() {
        new EnvatoProduct(this, this).execute();
    }

    private void loadSettings() {
        if (Boolean.TRUE.equals(Callback.isAppUpdate) && Callback.app_new_version != BuildConfig.VERSION_CODE){
            openDialogActivity(Callback.DIALOG_TYPE_UPDATE);
        } else if(Boolean.TRUE.equals(Callback.isMaintenance)){
            openDialogActivity(Callback.DIALOG_TYPE_MAINTENANCE);
        } else {
            if (Boolean.TRUE.equals(sharedPref.getIsFirst())) {
                if (Boolean.TRUE.equals(Callback.isLogin)){
                    openSignInActivity();
                } else {
                    sharedPref.setIsFirst(false);
                    openMainActivity();
                }
            } else {
                if (Boolean.FALSE.equals(sharedPref.getIsAutoLogin())) {
                    new Handler().postDelayed(this::openMainActivity, 2000);
                } else {
                    if (sharedPref.getLoginType().equals(Callback.LOGIN_TYPE_GOOGLE)) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            loadLogin(Callback.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                        } else {
                            sharedPref.setIsAutoLogin(false);
                            openMainActivity();
                        }
                    } else {
                        loadLogin(Callback.LOGIN_TYPE_NORMAL, "");
                    }
                }
            }
        }
    }

    private void loadLogin(final String loginType, final String authID) {
        if (helper.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String user_gender, String user_phone, String profile_img, String otp_status, String member, String registered_on) {
                    pb.setVisibility(View.GONE);
                    if (success.equals("1") && (!loginSuccess.equals("-1"))) {
                        sharedPref.setLoginDetails(user_id, user_name, user_phone, sharedPref.getEmail(), user_gender, profile_img, authID, sharedPref.getIsRemember(), sharedPref.getPassword(), loginType, otp_status, member, registered_on);
                        sharedPref.setIsLogged(true);
                    }
                    openMainActivity();
                }
            }, helper.getAPIRequest(Callback.METHOD_LOGIN, 0,"","","","","","",sharedPref.getEmail(),"","",sharedPref.getPassword(),authID,loginType,null, null));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            sharedPref.setIsAutoLogin(false);
            openMainActivity();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openSignInActivity() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "");
        startActivity(intent);
        finish();
    }

    private void openDialogActivity(String type) {
        Intent intent = new Intent(SplashActivity.this, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", type);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStartPairing() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected() {
        pb.setVisibility(View.GONE);
        loadSettings();
    }

    @Override
    public void onUnauthorized(String message) {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.err_unauthorized_access), message);
    }

    @Override
    public void onReconnect() {
        Toast.makeText(SplashActivity.this, "Please wait a minute", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.err_server), getString(R.string.err_server_not_connected));
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (title.equals(getString(R.string.err_internet_not_connected)) || title.equals(getString(R.string.err_server_not_connected))) {
            alertDialog.setNegativeButton(getString(R.string.retry), (dialog, which) -> loadAboutData());
        }
        alertDialog.setPositiveButton(getString(R.string.exit), (dialog, which) -> finish());
        alertDialog.show();
    }
}