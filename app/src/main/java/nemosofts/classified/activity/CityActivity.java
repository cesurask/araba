package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterCity;
import nemosofts.classified.asyncTask.LoadFilter;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.FilterListener;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemSubCategory;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;

public class CityActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemCity> arrayList;
    private ProgressBar pb;
    private String error_msg;
    private FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.city));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        helper = new Helper(this);
        dbHelper = new DBHelper(this);

        helper = new Helper(this, (position, type) -> {
            Callback.cat_id= "";
            Callback.cat_name= "Category";
            Callback.scat_id= "";
            Callback.scat_name= "";
            Callback.city_id = arrayList.get(position).getId();
            Callback.city_name = arrayList.get(position).getName();
            Callback.search_item = "";
            startActivity(new Intent(CityActivity.this, AllPostActivity.class));
        });

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);
        GridLayoutManager grid = new GridLayoutManager(this, 2);
        grid.setSpanCount(2);
        rv.setLayoutManager(grid);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        try {
            new LoadDBHelper().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.iv_change).setOnClickListener(v -> getData());

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(ll_adView);
    }

    class LoadDBHelper extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (!arrayList.isEmpty()) {
                arrayList.clear();
            }
            frameLayout.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                arrayList = dbHelper.getCity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (arrayList.isEmpty()) {
                getData();
            } else {
                setAdapter();
            }
        }
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadFilter loadFilter = new LoadFilter(CityActivity.this, new FilterListener() {
                @Override
                public void onStart() {
                    if (!arrayList.isEmpty()) {
                        arrayList.clear();
                    }
                    frameLayout.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemCity> arrayListCity, ArrayList<ItemCategory> arrayListCategory, ArrayList<ItemSubCategory> arrayListSubCategory) {
                    if (success.equals("1")) {
                        if (arrayListCity.isEmpty()) {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            arrayList.addAll(arrayListCity);
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_FILTER_LIST, 0, "", "", "", "", "", "", "", "", "", "", "", "", null, null));
            loadFilter.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapter() {
        if (!arrayList.isEmpty()) {
            AdapterCity adapterCity = new AdapterCity(arrayList, (itemCat, position) -> helper.showInterAd(position,""));
            rv.setAdapter(adapterCity);
            rv.scheduleLayoutAnimation();
        } else {
            error_msg = getString(R.string.err_no_data_found);
        }
        setEmpty();
    }

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

            @SuppressLint("InflateParams") View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> getData());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_city;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public void onDestroy() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}