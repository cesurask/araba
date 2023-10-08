package nemosofts.classified.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.classified.BuildConfig;
import nemosofts.classified.R;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;

public class AboutUsActivity extends AppCompatActivity {

    TextView tv_author, tv_email, tv_website, tv_contact, tv_description, tv_version;

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

        tv_author = findViewById(R.id.tv_company);
        tv_email = findViewById(R.id.tv_email);
        tv_website = findViewById(R.id.tv_website);
        tv_contact = findViewById(R.id.tv_contact);
        tv_description = findViewById(R.id.tv_app_des);
        tv_version = findViewById(R.id.tv_version);

        if (Callback.itemAbout != null){
            tv_author.setText(!Callback.itemAbout.getAuthor().trim().isEmpty() ? Callback.itemAbout.getAuthor() : "");
            tv_email.setText(!Callback.itemAbout.getEmail().trim().isEmpty() ? Callback.itemAbout.getEmail() : "");
            tv_website.setText(!Callback.itemAbout.getWebsite().trim().isEmpty() ? Callback.itemAbout.getWebsite() : "");
            tv_contact.setText(!Callback.itemAbout.getContact().trim().isEmpty() ? Callback.itemAbout.getContact() : "");
            tv_description.setText(!Callback.itemAbout.getAppDesc().trim().isEmpty() ? Callback.itemAbout.getAppDesc() : "");

            findViewById(R.id.ll_domain).setOnClickListener(v -> {
                String web_url = Callback.itemAbout.getEmail();
                if (web_url.contains("http://") || web_url.contains("https://")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
                } else if (!web_url.isEmpty()){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+web_url)));
                } else {
                    Toast.makeText(AboutUsActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            });

            findViewById(R.id.ll_contact).setOnClickListener(v -> {
                String contact = Callback.itemAbout.getContact(); // use country code with your phone number
                if (!contact.isEmpty()){
                    String url = "https://api.whatsapp.com/send?phone=" + contact;
                    try {
                        PackageManager pm = getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.ll_email).setOnClickListener(v -> {
                String email = Callback.itemAbout.getEmail();
                if (!isEmailValid(email)){
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email,});
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    intent.putExtra(Intent.EXTRA_TEXT, "note");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(AboutUsActivity.this, getString(R.string.err_invalid_email), Toast.LENGTH_SHORT).show();
                }
            });
        }
        tv_version.setText(BuildConfig.VERSION_NAME);

        findViewById(R.id.ll_share).setOnClickListener(v -> {
            final String appName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + appName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share"));
        });
        findViewById(R.id.ll_rate).setOnClickListener(v -> {
            final String appName = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        });
    }

    private boolean isEmailValid(@NonNull String email) {
        return email.contains("@") && !email.contains(" ");
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_about_us;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}