package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterNotification;
import nemosofts.classified.asyncTask.LoadNotification;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.NotificationListener;
import nemosofts.classified.item.ItemNotification;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.EndlessRecyclerViewScrollListener;

public class NotificationActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private RecyclerView rv;
    private AdapterNotification adapter;
    private ArrayList<ItemNotification> arrayList;
    private ProgressBar pb;
    private String error_msg;
    private FrameLayout frameLayout;
    private int page = 1;
    private Boolean isOver = false, isScroll = false;

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

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        arrayList = new ArrayList<>();
        error_msg = getString(R.string.no_notification);
        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv);
        pb = findViewById(R.id.pb);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (Boolean.FALSE.equals(isOver)) {
                    new Handler().postDelayed(() -> {
                        isScroll = true;
                        loadData();
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });
        loadData();

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView);
    }

    private void loadData() {
        if (helper.isNetworkAvailable()) {
            LoadNotification loadNotification = new LoadNotification(new NotificationListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        pb.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onEnd(String success, ArrayList<ItemNotification> notificationArrayList) {
                    if (success.equals("1")) {
                        if (notificationArrayList.isEmpty()) {
                            isOver = true;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            error_msg = getString(R.string.no_notification);
                            setEmpty();
                        } else {
                            page = page + 1;
                            arrayList.addAll(notificationArrayList);
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_NOTIFICATION, page, "", "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            loadNotification.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if (Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterNotification(NotificationActivity.this, arrayList);
            rv.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
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

            TextView tv_empty_msg = myView.findViewById(R.id.tv_empty_msg);
            tv_empty_msg.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> loadData());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_notification;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}