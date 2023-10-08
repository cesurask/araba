package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterPost;
import nemosofts.classified.asyncTask.LoadPostUser;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.PostUserListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.EndlessRecyclerViewScrollListener;

public class FavActivity extends AppCompatActivity {

    private Helper helper;
    private RecyclerView rv;
    private AdapterPost adapter;
    private ArrayList<ItemData> arrayList;
    private Boolean isOver = false, isScroll = false;
    private int page = 1;
    private GridLayoutManager grid;
    private ProgressBar pb;
    private FloatingActionButton fab;
    private String error_msg;
    private FrameLayout frameLayout;

    private int  nativeAdPos = 0;
    AdLoader adLoader;
    ArrayList<NativeAd> arrayListNativeAds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.favourite));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        helper = new Helper(this);
        helper = new Helper(this, (position, type) -> {
            Intent intent = new Intent(FavActivity.this, PostDetailsActivity.class);
            intent.putExtra("post_id", arrayList.get(position).getId());
            intent.putExtra("user_id", arrayList.get(position).getUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(FavActivity.this, intent, null);
        });

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        fab = findViewById(R.id.fab);
        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);
        grid = new GridLayoutManager(FavActivity.this, 1);
        grid.setSpanCount(2);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? grid.getSpanCount() : 1;
            }
        });
        rv.setLayoutManager(grid);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (Boolean.FALSE.equals(isOver)) {
                    new Handler().postDelayed(() -> {
                        isScroll = true;
                        getData();
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = grid.findFirstVisibleItemPosition();
                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });
        fab.setOnClickListener(v -> rv.smoothScrollToPosition(0));

        if(Boolean.TRUE.equals(Callback.isNativeAd)) {
            if(Callback.nativeAdShow%2 != 0) {
                nativeAdPos = Callback.nativeAdShow + 1;
            } else {
                nativeAdPos = Callback.nativeAdShow;
            }
        }

        getData();
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadPostUser loadPost = new LoadPostUser(new PostUserListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemData> arrayListPost) {
                    if (success.equals("1")) {
                        if (arrayListPost.isEmpty()) {
                            isOver = true;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            for (int i = 0; i < arrayListPost.size(); i++) {
                                arrayList.add(arrayListPost.get(i));
                                if (Boolean.TRUE.equals(Callback.isNativeAd)) {
                                    int abc = arrayList.lastIndexOf(null);
                                    if (((arrayList.size() - (abc + 1)) % nativeAdPos == 0)) {
                                        arrayList.add(null);
                                    }
                                }
                            }

                            page = page + 1;
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_POST_BY_FAV, page, "", "", "", "", new SharedPref(this).getUserId(), "", "", "","","","","",null, null));
            loadPost.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if(Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterPost(FavActivity.this, arrayList, (itemData, position) -> helper.showInterAd(position,""));
            rv.setAdapter(adapter);
            rv.scheduleLayoutAnimation();
            setEmpty();
            loadNativeAds();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadNativeAds() {
        if (Boolean.TRUE.equals(Callback.isNativeAd && !Callback.adNetwork.equals(Callback.AD_TYPE_WORTISE)
                && !Callback.adNetwork.equals(Callback.AD_TYPE_STARTAPP) && !Callback.adNetwork.equals(Callback.AD_TYPE_IRONSOURCE)) && arrayList.size() >= 10) {
            AdLoader.Builder builder = new AdLoader.Builder(FavActivity.this, Callback.nativeAdID);

            Bundle extras = new Bundle();
            if (ConsentInformation.getInstance(FavActivity.this).getConsentStatus() != ConsentStatus.PERSONALIZED) {
                extras.putString("npa", "1");
            }

            AdRequest adRequest;
            if(Callback.adNetwork.equals(Callback.AD_TYPE_ADMOB)) {
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            } else {
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                        .addNetworkExtrasBundle(FacebookMediationAdapter.class, extras)
                        .build();
            }

            adLoader = builder.forNativeAd(nativeAd -> {
                try {
                    if(adLoader.isLoading()) {
                        arrayListNativeAds.add(nativeAd);
                    } else {
                        arrayListNativeAds.add(nativeAd);
                        adapter.addAds(arrayListNativeAds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).build();
            adLoader.loadAds(adRequest, 5);
        }
    }

    @SuppressLint("InflateParams")
    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> getData());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_visit_store;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}