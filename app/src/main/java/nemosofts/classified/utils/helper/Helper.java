package nemosofts.classified.utils.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.banner.BannerAd;
import com.wortise.ads.consent.ConsentManager;
import com.wortise.ads.interstitial.InterstitialAd;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import nemosofts.classified.R;
import nemosofts.classified.activity.CustomAdsActivity;
import nemosofts.classified.activity.SignInActivity;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.InterAdListener;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.advertising.AdManagerInterAdmob;
import nemosofts.classified.utils.advertising.AdManagerInterApplovin;
import nemosofts.classified.utils.advertising.AdManagerInterStartApp;
import nemosofts.classified.utils.advertising.AdManagerInterWortise;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Helper {

    private final Context ctx;
    private InterAdListener interAdListener;

    public Helper(Context ctx) {
        this.ctx = ctx;
    }

    public Helper(Context ctx, InterAdListener interAdListener) {
        this.ctx = ctx;
        this.interAdListener = interAdListener;
    }

    @SuppressLint("MissingPermission")
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void initializeAds() {
        if (Callback.adNetwork.equals(Callback.AD_TYPE_ADMOB) || Callback.adNetwork.equals(Callback.AD_TYPE_FACEBOOK)) {
            MobileAds.initialize(ctx, initializationStatus -> {
            });
        }
        if (Callback.adNetwork.equals(Callback.AD_TYPE_STARTAPP)) {
            StartAppSDK.init(ctx, Callback.startappAppId, false);
            StartAppAd.disableSplash();
        }
        if (Callback.adNetwork.equals(Callback.AD_TYPE_APPLOVIN) && (!AppLovinSdk.getInstance(ctx).isInitialized())) {
            AppLovinSdk.initializeSdk(ctx);
            AppLovinSdk.getInstance(ctx).setMediationProvider("max");
            AppLovinSdk.getInstance(ctx).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("656822d9-18de-4120-994e-44d4245a4d63", "249d75a2-1ef2-8ff9-8885-c50384843a66"));
        }
        if (Callback.adNetwork.equals(Callback.AD_TYPE_IRONSOURCE)) {
            IronSource.init((Activity) ctx, Callback.ironAdsId, () -> {
            });
        }
        if (Callback.adNetwork.equals(Callback.AD_TYPE_WORTISE) && !WortiseSdk.isInitialized()) {
            WortiseSdk.initialize(ctx, ctx.getString(R.string.wortise_app_id));
        }
    }

    @NonNull
    @SuppressLint("VisibleForTests")
    private AdView showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(ctx);
        AdRequest adRequest;
        if(Callback.adNetwork.equals(Callback.AD_TYPE_ADMOB)) {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                    .addNetworkExtrasBundle(FacebookMediationAdapter.class, new Bundle())
                    .build();
        }
        adView.setAdUnitId(Callback.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
        return adView;
    }

    @NonNull
    @SuppressLint("VisibleForTests")
    private AdView showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(ctx);
        AdRequest adRequest;
        if(Callback.adNetwork.equals(Callback.AD_TYPE_ADMOB)) {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .addNetworkExtrasBundle(FacebookMediationAdapter.class, extras)
                    .build();
        }
        adView.setAdUnitId(Callback.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
        return adView;
    }

    public Object showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable() && Boolean.TRUE.equals(Callback.isBannerAd) && Boolean.TRUE.equals(!Callback.isPurchases)) {
            switch (Callback.adNetwork) {
                case Callback.AD_TYPE_ADMOB:
                case Callback.AD_TYPE_FACEBOOK:
                    if (ConsentInformation.getInstance(ctx).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                        return showNonPersonalizedAds(linearLayout);
                    } else {
                        return showPersonalizedAds(linearLayout);
                    }
                case Callback.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(ctx);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    return startAppBanner;
                case Callback.AD_TYPE_APPLOVIN:
                    MaxAdView adView = new MaxAdView(Callback.bannerAdID, ctx);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = ctx.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    linearLayout.addView(adView);
                    adView.loadAd();
                    return adView;
                case Callback.AD_TYPE_WORTISE:
                    boolean granted = (ConsentInformation.getInstance(ctx).getConsentStatus() == ConsentStatus.PERSONALIZED);
                    ConsentManager.set(ctx, granted);
                    BannerAd mBannerAd = new BannerAd(ctx);
                    mBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                    mBannerAd.setAdUnitId(Callback.bannerAdID);
                    linearLayout.addView(mBannerAd);
                    mBannerAd.loadAd();
                    return mBannerAd;
                case Callback.AD_TYPE_IRONSOURCE:
                    IronSourceBannerLayout iBannerAd  = IronSource.createBanner((Activity) ctx, ISBannerSize.BANNER);
                    linearLayout.addView(iBannerAd);
                    IronSource.loadBanner(iBannerAd);
                    return iBannerAd;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public void showInterAd(final int pos, final String type) {
        Callback.customAdCount = Callback.customAdCount + 1;
        if (Boolean.TRUE.equals(Callback.isCustomAds) && Callback.customAdCount % Callback.customAdShow == 0 && Boolean.TRUE.equals(!Callback.isPurchases)){
            ctx.startActivity(new Intent(ctx, CustomAdsActivity.class));
        }
        else if (Boolean.TRUE.equals(Callback.isInterAd && !Callback.isPurchases)) {
            Callback.adCount = Callback.adCount + 1;
            if (Callback.adCount % Callback.interstitialAdShow == 0) {
                switch (Callback.adNetwork) {
                    case Callback.AD_TYPE_ADMOB:
                        final AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(ctx);
                        if (adManagerInterAdmob.getAd() != null) {
                            adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }
                            });
                            adManagerInterAdmob.getAd().show((Activity) ctx);
                        } else {
                            AdManagerInterAdmob.setAd(null);
                            adManagerInterAdmob.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_STARTAPP:
                        final AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(ctx);
                        if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                            adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {

                                }

                                @Override
                                public void adClicked(Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterStartApp.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_APPLOVIN:
                        final AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(ctx);
                        if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                            adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {

                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {

                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {

                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                            adManagerInterApplovin.getAd().showAd();
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterApplovin.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;

                    case Callback.AD_TYPE_WORTISE:
                        final AdManagerInterWortise adManagerInterWortise = new AdManagerInterWortise(ctx);
                        if (adManagerInterWortise.getAd() != null && adManagerInterWortise.getAd().isAvailable()) {
                            adManagerInterWortise.getAd().setListener(new InterstitialAd.Listener() {
                                @Override
                                public void onInterstitialClicked(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialDismissed(@NonNull InterstitialAd interstitialAd) {
                                    AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialFailed(@NonNull InterstitialAd interstitialAd, @NonNull com.wortise.ads.AdError adError) {
                                    AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialLoaded(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialShown(@NonNull InterstitialAd interstitialAd) {

                                }
                            });
                            adManagerInterWortise.getAd().showAd();
                        } else {
                            AdManagerInterWortise.setAd(null);
                            adManagerInterWortise.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_IRONSOURCE:
                        if (IronSource.isInterstitialReady()) {
                            IronSource.setInterstitialListener(new InterstitialListener() {
                                @Override
                                public void onInterstitialAdReady() {

                                }

                                @Override
                                public void onInterstitialAdLoadFailed(IronSourceError error) {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdOpened() {

                                }

                                @Override
                                public void onInterstitialAdClosed() {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdShowFailed(IronSourceError error) {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdClicked() {
                                }

                                @Override
                                public void onInterstitialAdShowSucceeded() {

                                }
                            });
                            IronSource.showInterstitial();
                        } else {
                            interAdListener.onClick(pos, type);
                        }
                        IronSource.init((Activity) ctx, Callback.ironAdsId, IronSource.AD_UNIT.INTERSTITIAL);
                        IronSource.loadInterstitial();
                        break;
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public RequestBody getAPIRequest(String helper_name, int page, String itemID, String catID, String searchText, String reportMessage, String userID, String name, String email, String mobile, String gender, String password, String authID, String loginType, File file, ArrayList<File> fileArray) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", helper_name);
        jsObj.addProperty("application_id", ctx.getPackageName());

        if (Callback.METHOD_APP_DETAILS.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_LOGIN.equals(helper_name)){
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_REGISTER.equals(helper_name)){
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_gender", gender);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_PROFILE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_ACCOUNT_DELETE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_EDIT_PROFILE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_password", password);
        } else if (Callback.METHOD_USER_IMAGES_UPDATE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_FORGOT_PASSWORD.equals(helper_name)){
            jsObj.addProperty("user_email", email);
        } else if (Callback.METHOD_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_REMOVE_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_REPORT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("report_title", searchText);
            jsObj.addProperty("report_msg", reportMessage);
        } else if (Callback.METHOD_GET_RATINGS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("device_id", userID);
        } else if (Callback.METHOD_RATINGS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("device_id", userID);
            jsObj.addProperty("rate", authID);
            jsObj.addProperty("message", reportMessage);
        }

        else if (Callback.METHOD_HOME.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_POST.equals(helper_name)) {
            jsObj.addProperty("sort", Callback.sort_by);
            jsObj.addProperty("city_id", Callback.city_id);
            jsObj.addProperty("cat_id", Callback.cat_id);
            jsObj.addProperty("scat_id", Callback.scat_id);
            jsObj.addProperty("type", loginType);
            jsObj.addProperty("search_text", searchText);
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_DO_BLOCK.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("post_id", itemID);
        } else if (Callback.METHOD_POST_BY_BLOCKED.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("page", String.valueOf(page));
        }else if (Callback.METHOD_SHOP_BY_BLOCKED.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("shop_id", itemID);
        } else if (Callback.METHOD_OTP_VERIFIED.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("user_phone", mobile);
        } else if (Callback.METHOD_SHARE.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
        } else if (Callback.METHOD_DETAILS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("post_user_id", catID);
        } else if (Callback.METHOD_DO_FAV.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_POST_BY_FAV.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        }  else if (Callback.METHOD_VISIT_STORE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Callback.METHOD_PRO_TEST.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
        } else if (Callback.METHOD_DATABASE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Callback.METHOD_REMOVE_GALLERY_IMAGE.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("image_id", catID);
        } else if (Callback.METHOD_COMMENTS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Callback.METHOD_POST_COMMENTS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("comment_text", reportMessage);
        } else if (Callback.METHOD_DELETE_COMMENTS.equals(helper_name)) {
            jsObj.addProperty("comment_id", itemID);
        } else if (Callback.METHOD_ADD_PRODUCT.equals(helper_name)){
            jsObj.addProperty("user_id", userID);

            jsObj.addProperty("sub_cat_id", itemID);
            jsObj.addProperty("cat_id", catID);
            jsObj.addProperty("districts_id", searchText);

            jsObj.addProperty("post_title", reportMessage);
            jsObj.addProperty("post_price", name);
            jsObj.addProperty("post_description", email);
            jsObj.addProperty("post_phone_1", mobile);
            jsObj.addProperty("post_phone_2", gender);
            jsObj.addProperty("post_type", loginType);
        } else if (Callback.METHOD_EDIT_UPLOAD_POST.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("post_id", authID);

            jsObj.addProperty("sub_cat_id", itemID);
            jsObj.addProperty("cat_id", catID);
            jsObj.addProperty("districts_id", searchText);

            jsObj.addProperty("post_title", reportMessage);
            jsObj.addProperty("post_price", name);
            jsObj.addProperty("post_description", email);
            jsObj.addProperty("post_phone_1", mobile);
            jsObj.addProperty("post_phone_2", gender);
            jsObj.addProperty("post_type", loginType);
        } else if (Callback.METHOD_DO_TRASH.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
        } else if (Callback.METHOD_DO_SOLD_OUT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
        }

        else if (Callback.METHOD_SEND_CHAT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("chat_text", searchText);
            jsObj.addProperty("post_user_id", catID);
            jsObj.addProperty("chat_user_id", userID);
            jsObj.addProperty("chat_type", loginType);
            jsObj.addProperty("chat_on_user_id", authID);
        } else if (Callback.METHOD_GET_CHAT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("post_user_id", catID);
            jsObj.addProperty("chat_user_id", userID);
            jsObj.addProperty("chat_on_user_id", authID);
        } else if (Callback.METHOD_CHAT_POST.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("page", String.valueOf(page));
        }

        switch (helper_name) {
            case Callback.METHOD_EDIT_UPLOAD_POST:
            case Callback.METHOD_ADD_PRODUCT: {
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (file != null) {
                    builder.addFormDataPart("post_featured_image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
                if (fileArray != null){
                    for (int i = 0; i < fileArray.size(); i++) {
                        builder.addFormDataPart("post_gallery_image[]", fileArray.get(i).getName(), RequestBody.create(MEDIA_TYPE_PNG, fileArray.get(i)));
                    }
                }
                return builder.addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
            }
            case Callback.METHOD_SEND_CHAT:
            case Callback.METHOD_REGISTER:
            case Callback.METHOD_USER_IMAGES_UPDATE: {
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (file != null) {
                    builder.addFormDataPart("image_data", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
                return builder.addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
            }
            default:
                return new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString()))
                        .build();
        }
    }

    public RequestBody getAPIRequestTransaction(String helper_name, String postId, String userID, String paymentId, String paymentGateway, int dailyBumpUp, int topAd, int spotLight, String dailyBumpUpDuration, String topAdDuration, String spotLightDuration, String amount,  File file) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", helper_name);
        jsObj.addProperty("application_id", ctx.getPackageName());

        if (Callback.TRANSACTION_URL.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("post_id", postId);

            jsObj.addProperty("payment_id", paymentId);
            jsObj.addProperty("gateway", paymentGateway);

            jsObj.addProperty("daily_bump_up", String.valueOf(dailyBumpUp));
            jsObj.addProperty("top_ad", String.valueOf(topAd));
            jsObj.addProperty("spot_light", String.valueOf(spotLight));

            jsObj.addProperty("daily_bump_up_duration", dailyBumpUpDuration);
            jsObj.addProperty("top_ad_duration", topAdDuration);
            jsObj.addProperty("spot_light_duration", spotLightDuration);

            jsObj.addProperty("payment_amount", amount);
        }

        if (Callback.METHOD_BANK_PAY.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("post_id", postId);

            jsObj.addProperty("payment_id", paymentId);
            jsObj.addProperty("gateway", paymentGateway);

            jsObj.addProperty("daily_bump_up", String.valueOf(dailyBumpUp));
            jsObj.addProperty("top_ad", String.valueOf(topAd));
            jsObj.addProperty("spot_light", String.valueOf(spotLight));

            jsObj.addProperty("daily_bump_up_duration", dailyBumpUpDuration);
            jsObj.addProperty("top_ad_duration", topAdDuration);
            jsObj.addProperty("spot_light_duration", spotLightDuration);

            jsObj.addProperty("payment_amount", amount);
        }

        if (Callback.METHOD_BANK_PAY.equals(helper_name)) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (file != null) {
                builder.addFormDataPart("image_data", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
            return builder.addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
        }
        return new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
    }


    public void clickLogin() {
        SharedPref sharePref = new SharedPref(ctx);
        if (sharePref.isLogged()) {
            logout((Activity) ctx, sharePref);
            Toast.makeText(ctx, ctx.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ctx, SignInActivity.class);
            intent.putExtra("from", "app");
            ctx.startActivity(intent);
        }
    }

    public void logout(Activity activity, @NonNull SharedPref sharePref) {
        if (sharePref.getLoginType().equals(Callback.LOGIN_TYPE_GOOGLE)) {
            FirebaseAuth.getInstance().signOut();
        }
        sharePref.setIsAutoLogin(false);
        sharePref.setIsLogged(false);
        sharePref.setLoginDetails("", "", "", "", "", "", "", false, "", Callback.LOGIN_TYPE_NORMAL, "0", "0", "");
        Intent intent1 = new Intent(ctx, SignInActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        ctx.startActivity(intent1);
        activity.finish();
    }

    @SuppressLint("Range")
    public String getPathImage(Uri uri) {
        try {
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();

                if (returnn == null) {
                    String path = null, image_id = null;
                    Cursor cursor2 = ctx.getContentResolver().query(uri, null, null, null, null);
                    if (cursor2 != null) {
                        cursor2.moveToFirst();
                        image_id = cursor2.getString(0);
                        image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
                        cursor2.close();
                    }

                    Cursor cursor3 = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
                    if (cursor3 != null) {
                        cursor3.moveToFirst();
                        path = cursor3.getString(cursor3.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor3.close();
                    }
                    return path;
                }
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }
}
