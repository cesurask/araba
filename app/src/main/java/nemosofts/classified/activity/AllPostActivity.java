package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterPost;
import nemosofts.classified.asyncTask.LoadPost;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.PostListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.EndlessRecyclerViewScrollListener;

public class AllPostActivity extends AppCompatActivity {

    private Helper helper;
    private RecyclerView rv;
    private AdapterPost adapter;
    private ArrayList<ItemData> arrayList;
    private Boolean isOver = false;
    private Boolean isScroll = false;
    private int page = 1;
    private GridLayoutManager grid;
    private ProgressBar pb;
    private FloatingActionButton fab;
    private String error_msg;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipe_refresh;
    private TextView tv_category;
    private TextView tv_city;
    private ImageView iv_filter_sort;
    private Boolean isSearch = false;
    private Boolean isSearchText = false;

    private int  nativeAdPos = 0;
    AdLoader adLoader;
    ArrayList<NativeAd> arrayListNativeAds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Callback.search_item = "";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        helper = new Helper(this);

        helper = new Helper(this, (position, type) -> {
            Intent intent = new Intent(AllPostActivity.this, PostDetailsActivity.class);
            intent.putExtra("post_id", arrayList.get(position).getId());
            intent.putExtra("user_id", arrayList.get(position).getUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(AllPostActivity.this, intent, null);
        });

        arrayList = new ArrayList<>();

        swipe_refresh = findViewById(R.id.swipe_refresh);
        frameLayout = findViewById(R.id.fl_empty);
        fab = findViewById(R.id.fab);
        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);
        grid = new GridLayoutManager(AllPostActivity.this, 1);
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
                        getData(false);
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

        swipe_refresh.setOnRefreshListener(this::refreshData);
        swipe_refresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_dark);

        iv_filter_sort = findViewById(R.id.iv_filter_sort);
        tv_category = findViewById(R.id.tv_category_filter);
        tv_city = findViewById(R.id.tv_city_filter);

        findViewById(R.id.rl_filter_sort).setOnClickListener(v -> openFilterDialog());
        findViewById(R.id.ll_filter_city).setOnClickListener(v -> {
            Intent result = new Intent(AllPostActivity.this, FilterActivity.class);
            result.putExtra("type", "city");
            result.putExtra("tbl", "tbl_city");
            startActivityForResult(result, 102);
        });
        findViewById(R.id.ll_filter_cat).setOnClickListener(v -> {
            Intent result = new Intent(AllPostActivity.this, FilterActivity.class);
            result.putExtra("type", "cat");
            result.putExtra("tbl", "tbl_category");
            startActivityForResult(result, 102);
        });

        if(Boolean.TRUE.equals(Callback.isNativeAd)) {
            if(Callback.nativeAdShow%2 != 0) {
                nativeAdPos = Callback.nativeAdShow + 1;
            } else {
                nativeAdPos = Callback.nativeAdShow;
            }
        }

        getFilterData();
        getData(false);

        // Search
        EditText edt_search = findViewById(R.id.edt_search);
        ImageView iv_search = findViewById(R.id.iv_search);
        edt_search.setEnabled(false);
        iv_search.setOnClickListener(v -> {
            if (Boolean.FALSE.equals(isSearch)){
                isSearch = true;
                iv_search.setImageResource(R.drawable.ic_close);
                edt_search.setVisibility(View.VISIBLE);
                edt_search.setEnabled(true);
                edt_search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT);

            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                edt_search.setText("");
                isSearch = false;
                iv_search.setImageResource(R.drawable.ic_search);
                edt_search.setVisibility(View.GONE);
                edt_search.setEnabled(false);
                Callback.search_item = "";
                if (Boolean.TRUE.equals(isSearchText)){
                    refreshData();
                }
            }
        });
        edt_search.setVisibility(Boolean.TRUE.equals(isSearch) ? View.VISIBLE : View.GONE);
        edt_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                if (edt_search.getText().toString().isEmpty()){
                    Callback.search_item = "";
                    isSearch = false;
                    iv_search.setImageResource(R.drawable.ic_search);
                    edt_search.setVisibility(View.GONE);
                    edt_search.setEnabled(false);
                    if (Boolean.TRUE.equals(isSearchText)){
                        refreshData();
                    }
                } else {
                    Callback.search_item = edt_search.getText().toString().replace(" ", "%20");
                    isSearchText = true;
                    refreshData();
                }
            }
            return true;
        });
    }

    private void openFilterDialog() {
        Dialog dialog_rate = new Dialog(AllPostActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.dialog_flter);

        final TextView lowest_to_highest = dialog_rate.findViewById(R.id.tv_lowest_to_highest);
        final TextView highest_to_lowest = dialog_rate.findViewById(R.id.tv_highest_to_lowest);
        final TextView oldest_to_first = dialog_rate.findViewById(R.id.tv_oldest_to_first);
        final TextView newest_to_first = dialog_rate.findViewById(R.id.tv_newest_to_first);

        switch (Callback.sort_by) {
            case "lowest":
                lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_active);
                highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                lowest_to_highest.setTextColor(ColorUtils.colorAccent(this));
                highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
                oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                break;
            case "highest":
                lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_active);
                oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                lowest_to_highest.setTextColor(ColorUtils.colorTitle(this));
                highest_to_lowest.setTextColor(ColorUtils.colorAccent(this));
                oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                break;
            case "oldest":
                lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                oldest_to_first.setBackgroundResource(R.drawable.bg_filter_active);
                newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                lowest_to_highest.setTextColor(ColorUtils.colorTitleSub(this));
                highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
                oldest_to_first.setTextColor(ColorUtils.colorAccent(this));
                newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                break;
            case "newest":
                lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
                oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
                newest_to_first.setBackgroundResource(R.drawable.bg_filter_active);
                lowest_to_highest.setTextColor(ColorUtils.colorTitleSub(this));
                highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
                oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
                newest_to_first.setTextColor(ColorUtils.colorAccent(this));
                break;
            default:
                break;
        }

        lowest_to_highest.setOnClickListener(view -> {
            lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_active);
            highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            lowest_to_highest.setTextColor(ColorUtils.colorAccent(this));
            highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
            oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            Callback.sort_by = "lowest";
        });
        highest_to_lowest.setOnClickListener(view -> {
            lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_active);
            oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            lowest_to_highest.setTextColor(ColorUtils.colorTitleSub(this));
            highest_to_lowest.setTextColor(ColorUtils.colorAccent(this));
            oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            Callback.sort_by = "highest";
        });
        oldest_to_first.setOnClickListener(view -> {
            lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            oldest_to_first.setBackgroundResource(R.drawable.bg_filter_active);
            newest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            lowest_to_highest.setTextColor(ColorUtils.colorTitleSub(this));
            highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
            oldest_to_first.setTextColor(ColorUtils.colorAccent(this));
            newest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            Callback.sort_by = "oldest";
        });
        newest_to_first.setOnClickListener(view -> {
            lowest_to_highest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            highest_to_lowest.setBackgroundResource(R.drawable.bg_filter_deactivate);
            oldest_to_first.setBackgroundResource(R.drawable.bg_filter_deactivate);
            newest_to_first.setBackgroundResource(R.drawable.bg_filter_active);
            lowest_to_highest.setTextColor(ColorUtils.colorTitleSub(this));
            highest_to_lowest.setTextColor(ColorUtils.colorTitleSub(this));
            oldest_to_first.setTextColor(ColorUtils.colorTitleSub(this));
            newest_to_first.setTextColor(ColorUtils.colorAccent(this));
            Callback.sort_by = "newest";
        });

        dialog_rate.findViewById(R.id.button_submit_filter).setOnClickListener(view -> {
            refreshData();
            getFilterData();
            dialog_rate.dismiss();
        });

        dialog_rate.findViewById(R.id.button_cancel_filter).setOnClickListener(view -> {
            if (!Callback.sort_by.equals("newest")){
                Callback.sort_by = "newest";
                refreshData();
                getFilterData();
            }
            dialog_rate.dismiss();
        });

        dialog_rate.findViewById(R.id.iv_filter_close).setOnClickListener(view -> dialog_rate.dismiss());

        dialog_rate.setCancelable(false);
        dialog_rate.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog_rate.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        arrayList.clear();
        isOver = false;
        isScroll = false;
        page = 1;
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
        getData(true);
    }

    private void getFilterData() {
        tv_category.setText(Callback.cat_name);
        tv_city.setText(Callback.city_name);
        if (Callback.sort_by.equals("newest")){
            iv_filter_sort.setVisibility(View.GONE);
        } else {
            if (iv_filter_sort.getVisibility() == View.GONE) {
                iv_filter_sort.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getData(Boolean refresh) {
        if (helper.isNetworkAvailable()) {
            LoadPost loadPost = new LoadPost(true ,new PostListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, ArrayList<ItemData> arrayListPost, ArrayList<ItemData> arrayListTop) {
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
                            ArrayList<ItemData> arrayListFilter = new ArrayList<>();
                            if (arrayList.isEmpty() && (!arrayListTop.isEmpty())){
                                arrayListFilter.addAll(arrayListTop);
                            }
                            arrayListFilter.addAll(arrayListPost);
                            for (int i = 0; i < arrayListFilter.size(); i++) {
                                arrayList.add(arrayListFilter.get(i));
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
                    if (Boolean.TRUE.equals(refresh)){
                        swipe_refresh.setRefreshing(false);
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_POST, page, "", "", Callback.search_item, "", new SharedPref(this).getUserId(), "", "", "","","","","top_post",null, null));
            loadPost.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if(Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterPost(AllPostActivity.this, arrayList, (itemData, position) -> helper.showInterAd(0,""));
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
            AdLoader.Builder builder = new AdLoader.Builder(AllPostActivity.this, Callback.nativeAdID);

            Bundle extras = new Bundle();
            if (ConsentInformation.getInstance(AllPostActivity.this).getConsentStatus() != ConsentStatus.PERSONALIZED) {
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
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> getData(false));

            frameLayout.addView(myView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            tv_category.setText(Callback.cat_name);
            tv_city.setText(Callback.city_name);
            refreshData();
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_all_post;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}