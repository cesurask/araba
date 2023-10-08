package nemosofts.classified.activity;

import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import androidx.nemosofts.Application;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import nemosofts.classified.R;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.BuildConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Analytics Initialization
        FirebaseAnalytics.getInstance(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        DBHelper DBHelper = new DBHelper(getApplicationContext());
        DBHelper.onCreate(DBHelper.getWritableDatabase());
        DBHelper.getAbout();

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.onesignal_app_id));

        new Helper(getApplicationContext()).initializeAds();
    }

    @Override
    public String setProductID() {
        return "30287878";
    }

    @Override
    public String setApplicationID() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
