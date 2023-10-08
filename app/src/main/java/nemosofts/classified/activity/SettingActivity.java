package nemosofts.classified.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.nemosofts.view.SwitchButton;

import com.onesignal.OneSignal;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

import nemosofts.classified.BuildConfig;
import nemosofts.classified.R;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;

public class SettingActivity extends AppCompatActivity {

    private ThemeEngine themeEngine;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private TextView tv_cache_size;
    private TextView tv_classic, tv_dark_grey, tv_dark, tv_dark_blue;
    private ImageView iv_dark_mode;

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

        themeEngine = new ThemeEngine(this);
        sharedPref = new SharedPref(this);

        progressDialog = new ProgressDialog(SettingActivity.this, R.style.ThemeDialog);
        progressDialog.setMessage(getString(R.string.clearing_cache));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        tv_classic = findViewById(R.id.tv_classic);
        tv_dark_grey = findViewById(R.id.tv_dark_grey);
        tv_dark = findViewById(R.id.tv_dark);
        tv_dark_blue = findViewById(R.id.tv_dark_blue);
        iv_dark_mode = findViewById(R.id.iv_dark_mode);
        tv_cache_size = findViewById(R.id.tv_cachesize);

        try {
            ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(iv_dark_mode, View.ALPHA, 0, 1);
            fadeAltAnim.setDuration(1500);
            fadeAltAnim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeCache();
        getThemeData();

        SwitchButton switch_noti = findViewById(R.id.switch_noti);
        switch_noti.setChecked(sharedPref.getIsNotification());
        switch_noti.setOnCheckedChangeListener((view, isChecked) -> {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(!isChecked);
            sharedPref.setIsNotification(isChecked);
        });
        findViewById(R.id.ll_block_list).setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, BlockListActivity.class)));
        findViewById(R.id.ll_about).setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, AboutUsActivity.class)));
        findViewById(R.id.ll_privacy).setOnClickListener(v ->  {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"privacy_policy.php");
            intent.putExtra("page_title", getResources().getString(R.string.privacy_policy));
            ActivityCompat.startActivity(SettingActivity.this, intent, null);
        });
        findViewById(R.id.ll_terms).setOnClickListener(v ->  {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"terms.php");
            intent.putExtra("page_title", getResources().getString(R.string.terms_and_conditions));
            ActivityCompat.startActivity(SettingActivity.this, intent, null);
        });

        findViewById(R.id.ll_cache).setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            FileUtils.deleteQuietly(getCacheDir());
                            FileUtils.deleteQuietly(getExternalCacheDir());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                        tv_cache_size.setText("0 MB");
                        super.onPostExecute(s);
                    }
                }.execute();
            }
        });

        tv_classic.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 0){
                setThemeMode(false, 0);
            }
        });
        tv_dark_grey.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 2){
                setThemeMode(true, 2);
            }
        });
        tv_dark_blue.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 3){
                setThemeMode(true, 3);
            }
        });
        tv_dark.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 1){
                setThemeMode(true, 1);
            }
        });
    }

    private void setThemeMode(Boolean isChecked, int isTheme) {
        themeEngine.setThemeMode(isChecked);
        themeEngine.setThemePage(isTheme);
        recreate();
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        tv_cache_size.setText(ApplicationUtil.readableFileSize(size));
    }

    private long getDirSize(File dir) {
        long size = 0;
        try {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file != null && file.isDirectory()) {
                    size += getDirSize(file);
                } else if (file != null && file.isFile()) {
                    size += file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private void getThemeData() {
        int theme = themeEngine.getThemePage();
        if (theme == 0){
            tv_classic.setBackgroundResource(R.drawable.bg_dark);
            tv_dark_grey.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_blue.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark.setBackgroundResource(R.drawable.bg_dark_none);

            tv_classic.setTextColor(ColorUtils.colorWhite(this));
            tv_dark_grey.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_blue.setTextColor(ColorUtils.colorTitle(this));
            tv_dark.setTextColor(ColorUtils.colorTitle(this));

            iv_dark_mode.setImageResource(R.drawable.classic);

        } else if (theme == 1){
            tv_classic.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_grey.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_blue.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark.setBackgroundResource(R.drawable.bg_dark);

            tv_classic.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_grey.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_blue.setTextColor(ColorUtils.colorTitle(this));
            tv_dark.setTextColor(ColorUtils.colorWhite(this));

            iv_dark_mode.setImageResource(R.drawable.dark);

        } else if (theme == 2){
            tv_classic.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_grey.setBackgroundResource(R.drawable.bg_dark);
            tv_dark_blue.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark.setBackgroundResource(R.drawable.bg_dark_none);

            tv_classic.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_grey.setTextColor(ColorUtils.colorWhite(this));
            tv_dark_blue.setTextColor(ColorUtils.colorTitle(this));
            tv_dark.setTextColor(ColorUtils.colorTitle(this));

            iv_dark_mode.setImageResource(R.drawable.dark_grey);
        } else if (theme == 3){
            tv_classic.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_grey.setBackgroundResource(R.drawable.bg_dark_none);
            tv_dark_blue.setBackgroundResource(R.drawable.bg_dark);
            tv_dark.setBackgroundResource(R.drawable.bg_dark_none);

            tv_classic.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_grey.setTextColor(ColorUtils.colorTitle(this));
            tv_dark_blue.setTextColor(ColorUtils.colorWhite(this));
            tv_dark.setTextColor(ColorUtils.colorTitle(this));

            iv_dark_mode.setImageResource(R.drawable.dark_blue);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            overridePendingTransition(0, 0);
            overridePendingTransition(0, 0);
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
            finish();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        overridePendingTransition(0, 0);
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}