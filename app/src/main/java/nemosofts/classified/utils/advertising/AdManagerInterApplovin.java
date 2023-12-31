package nemosofts.classified.utils.advertising;

import android.app.Activity;
import android.content.Context;

import com.applovin.mediation.ads.MaxInterstitialAd;

import nemosofts.classified.callback.Callback;

public class AdManagerInterApplovin {
    static MaxInterstitialAd interstitialAd;
    private final Context ctx;

    public AdManagerInterApplovin(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        interstitialAd = new MaxInterstitialAd(Callback.interstitialAdID, (Activity) ctx);
        interstitialAd.loadAd();
    }

    public MaxInterstitialAd getAd() {
        return interstitialAd;
    }

    public static void setAd(MaxInterstitialAd appLovingInter) {
        interstitialAd = appLovingInter;
    }
}