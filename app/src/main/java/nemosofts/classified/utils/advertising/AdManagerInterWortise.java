package nemosofts.classified.utils.advertising;

import android.annotation.SuppressLint;
import android.content.Context;

import com.wortise.ads.interstitial.InterstitialAd;

import nemosofts.classified.callback.Callback;

public class AdManagerInterWortise {
    @SuppressLint("StaticFieldLeak")
    static InterstitialAd interAd;
    private final Context ctx;

    public AdManagerInterWortise(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        interAd = new InterstitialAd((ctx), Callback.interstitialAdID);
        interAd.loadAd();
    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }
}