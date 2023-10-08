package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.wortise.ads.natives.GoogleNativeAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.dialog.FeedBackDialog;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class AdapterPost extends RecyclerView.Adapter {

    private final SharedPref sharedPref;
    private final Helper helper;
    private final ArrayList<ItemData> arrayList;
    private final Context context;
    private final RecyclerItemClickListener listener;
    private final int VIEW_PROG = -1;
    private int columnWidth = 0;

    Boolean isAdLoaded = false;
    List<NativeAd> mNativeAdsAdmob = new ArrayList<>();

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView title;
        private final TextView city;
        private final TextView pri;
        private final TextView date;
        private final ImageView iv_speed;
        private final ImageView iv_top;
        private final LinearLayout rl_post;
        private final RelativeLayout rl_sold_out;
        private final RelativeLayout iv_more;

        private MyViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.iv_post_img);
            title = view.findViewById(R.id.tv_post_title);
            city = view.findViewById(R.id.tv_post_city);
            pri = view.findViewById(R.id.tv_post_pri);
            date = view.findViewById(R.id.tv_post_date);
            iv_speed = view.findViewById(R.id.iv_speed_post);
            iv_top = view.findViewById(R.id.iv_top_ad_post);
            rl_post = view.findViewById(R.id.rl_post);
            rl_sold_out = view.findViewById(R.id.rl_sold_out);

            iv_more = view.findViewById(R.id.rl_more_post);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rl_native_ad;
        boolean isAdRequested = false;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    public AdapterPost(Context context, ArrayList<ItemData> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
        sharedPref = new SharedPref(context);
        helper = new Helper(context);
        columnWidth = ApplicationUtil.getColumnWidth(2, 0, context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_native_ad, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final ItemData itemPost = arrayList.get(position);

            ((MyViewHolder) holder).img.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth));

            Picasso.get()
                    .load(itemPost.getImage())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(((MyViewHolder) holder).img);

            if (itemPost.getSoldOut().equals("0")){
                ((MyViewHolder) holder).rl_sold_out.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).rl_sold_out.setVisibility(View.GONE);
            }

            if (Boolean.TRUE.equals(itemPost.getTopAd())){
                ((MyViewHolder) holder).iv_top.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).rl_post.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.topAd)));
            } else {
                ((MyViewHolder) holder).iv_top.setVisibility(View.GONE);
            }

            if (Boolean.TRUE.equals(itemPost.getDailyBumpUp())){
                ((MyViewHolder) holder).iv_speed.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).rl_post.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dailyBumpUp)));
            } else {
                ((MyViewHolder) holder).iv_speed.setVisibility(View.GONE);
            }

            ((MyViewHolder) holder).date.setText(itemPost.getDateTime());
            ((MyViewHolder) holder).title.setText(String.valueOf(itemPost.getTitle()));
            ((MyViewHolder) holder).city.setText(itemPost.getCatName());
            if (Boolean.TRUE.equals(Callback.currency_position)){
                ((MyViewHolder) holder).pri.setText(Callback.currency_code+" "+ApplicationUtil.formatCurrency(itemPost.getMoney()));
            }else {
                ((MyViewHolder) holder).pri.setText(ApplicationUtil.formatCurrency(itemPost.getMoney())+" "+Callback.currency_code);
            }

            ((MyViewHolder) holder).iv_more.setOnClickListener(v -> {
                try {
                    openBottomSheet(holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            ((MyViewHolder) holder).rl_post.setOnClickListener(v -> listener.onClickListener(itemPost, holder.getAbsoluteAdapterPosition()));

        } else if (holder instanceof ADViewHolder) {
            if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                switch (Callback.adNetwork) {
                    case Callback.AD_TYPE_ADMOB:
                    case Callback.AD_TYPE_FACEBOOK:
                        if (Boolean.TRUE.equals(isAdLoaded) && (mNativeAdsAdmob.size() >= 5)) {

                            int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                            @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                            populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(adView);

                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                        }
                        break;
                    case Callback.AD_TYPE_STARTAPP:
                        if (!((ADViewHolder) holder).isAdRequested) {
                            StartAppNativeAd nativeAd = new StartAppNativeAd(context);

                            nativeAd.loadAd(new NativeAdPreferences()
                                    .setAdsNumber(1)
                                    .setAutoBitmapDownload(true)
                                    .setPrimaryImageSize(2), new AdEventListener() {
                                @Override
                                public void onReceiveAd(@NonNull Ad ad) {
                                    try {
                                        if (!nativeAd.getNativeAds().isEmpty()) {
                                            @SuppressLint("InflateParams") RelativeLayout nativeAdView = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);

                                            ImageView icon = nativeAdView.findViewById(R.id.icon);
                                            TextView title = nativeAdView.findViewById(R.id.title);
                                            TextView description = nativeAdView.findViewById(R.id.description);
                                            Button button = nativeAdView.findViewById(R.id.button);

                                            Picasso.get()
                                                    .load(nativeAd.getNativeAds().get(0).getImageUrl())
                                                    .into(icon);
                                            title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                            description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                            button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");

                                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                            ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailedToReceiveAd(Ad ad) {
                                    ((ADViewHolder) holder).isAdRequested = false;
                                }
                            });
                            ((ADViewHolder) holder).isAdRequested = true;
                        }
                        break;
                    case Callback.AD_TYPE_WORTISE:
                        if (!((ADViewHolder) holder).isAdRequested) {
                            GoogleNativeAd googleNativeAd = new GoogleNativeAd(
                                    context, Callback.nativeAdID, new GoogleNativeAd.Listener() {
                                @Override
                                public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {

                                }

                                @Override
                                public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.wortise.ads.AdError adError) {

                                }

                                @Override
                                public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {

                                }

                                @Override
                                public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd, @NonNull NativeAd nativeAd) {
                                    @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                    ((ADViewHolder) holder).rl_native_ad.addView(adView);

                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }
                            });
                            googleNativeAd.load();
                            ((ADViewHolder) holder).isAdRequested = true;
                        }
                        break;case Callback.AD_TYPE_IRONSOURCE:
                        if (!((ADViewHolder) holder).isAdRequested) {
                            IronSource.init((Activity) context, Callback.ironAdsId, IronSource.AD_UNIT.BANNER);
                            IronSourceBannerLayout banner = IronSource.createBanner((Activity) context, ISBannerSize.BANNER);
                            banner.setBannerListener(new com.ironsource.mediationsdk.sdk.BannerListener() {
                                @Override
                                public void onBannerAdLoaded() {
                                    ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                    ((ADViewHolder) holder).rl_native_ad.addView(banner);

                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onBannerAdLoadFailed(IronSourceError error) {

                                }

                                @Override
                                public void onBannerAdClicked() {
                                }

                                @Override
                                public void onBannerAdScreenPresented() {
                                }

                                @Override
                                public void onBannerAdScreenDismissed() {
                                }

                                @Override
                                public void onBannerAdLeftApplication() {
                                }
                            });
                            IronSource.loadBanner(banner);
                            ((ADViewHolder) holder).isAdRequested = true;
                        }
                        break;
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemData itemPost, int position);
    }

    private void openBottomSheet(int adapterPosition) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.bottom_sheet_post, null);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);

        TextView tv_sheet_text = dialog.findViewById(R.id.tv_sheet_text);
        TextView tv_sheet_date = dialog.findViewById(R.id.tv_sheet_date);
        RoundedImageView iv_sheet_post = dialog.findViewById(R.id.iv_sheet_post);
        LinearLayout ll_sheet_blocked = dialog.findViewById(R.id.ll_sheet_blocked);
        LinearLayout ll_sheet_report = dialog.findViewById(R.id.ll_sheet_report);
        LinearLayout ll_sheet_share = dialog.findViewById(R.id.ll_sheet_share);
        LinearLayout ll_sheet_fav = dialog.findViewById(R.id.ll_sheet_fav);

        if (iv_sheet_post != null){
            Picasso.get()
                    .load(arrayList.get(adapterPosition).getImage())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_sheet_post);
        }
        Objects.requireNonNull(tv_sheet_text).setText(arrayList.get(adapterPosition).getTitle());
        Objects.requireNonNull(tv_sheet_date).setText(arrayList.get(adapterPosition).getDateTime());
        Objects.requireNonNull(ll_sheet_blocked).setOnClickListener(view1 -> {
            loadBlockList(adapterPosition);
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_report).setOnClickListener(view2 -> {
            new FeedBackDialog((Activity) context).showDialog(arrayList.get(adapterPosition).getId(),arrayList.get(adapterPosition).getTitle());
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_share).setOnClickListener(view3 -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, arrayList.get(adapterPosition).getTitle() + " - " + context.getString(R.string.get_more) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_fav).setOnClickListener(view4 -> {
            loadFav(adapterPosition);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void loadBlockList(int pos) {
        if (sharedPref.isLogged()) {
            if (helper.isNetworkAvailable()) {
                LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            try {
                                notifyDataSetChanged();
                                arrayList.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, helper.getAPIRequest(Callback.METHOD_DO_BLOCK, 0, arrayList.get(pos).getId(), "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
                loadFav.execute();
            } else {
                Toast.makeText(context, context.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            helper.clickLogin();
        }
    }

    private void loadFav(int pos) {
        if (sharedPref.isLogged()) {
            if (helper.isNetworkAvailable()) {
                LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, helper.getAPIRequest(Callback.METHOD_DO_FAV, 0,  arrayList.get(pos).getId(), "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
                loadFav.execute();
            } else {
                Toast.makeText(context, context.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            helper.clickLogin();
        }
    }

    public void addAds(List<NativeAd> arrayListNativeAds) {
        isAdLoaded = true;
        mNativeAdsAdmob.addAll(arrayListNativeAds);
        for (int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i) == null) {
                notifyItemChanged(i);
            }
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

}