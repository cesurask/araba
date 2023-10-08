package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.classified.R;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.utils.helper.Helper;

public class WebActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Intent intent = getIntent();
        String webWRL = intent.getStringExtra("web_url");
        String page_title = intent.getStringExtra("page_title");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setTitle(page_title);

        final ProgressBar pb = findViewById(R.id.pb_web);
        webView = findViewById(R.id.web);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                pb.setProgress(progress);
                if (progress == 100) {
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        assert webWRL != null;
        webView.loadUrl(webWRL);

        LinearLayout adView = findViewById(R.id.ll_adView);
        new Helper(this).showBannerAd(adView);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_web;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}