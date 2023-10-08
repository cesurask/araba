package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterSuperCategory;
import nemosofts.classified.adapter.AdapterSuperCategorySub;
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

public class CategoryActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private RecyclerView rv_cat;
    private ArrayList<ItemCategory> arrayList_cat;
    private AdapterSuperCategory adapter;
    private String error_msg;
    private FrameLayout frameLayout;
    private ProgressBar pb;
    private RecyclerView rv_sub_cat;
    private ArrayList<ItemSubCategory> arrayList_sub_cat;
    private String catID = "", catName = "";
    private int position_cat = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.category));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        helper = new Helper(this);
        dbHelper = new DBHelper(this);

        helper = new Helper(this, (position, type) -> {
            Callback.city_id = "";
            Callback.cat_id = "";
            Callback.scat_id = arrayList_sub_cat.get(position).getId();
            Callback.scat_name = arrayList_sub_cat.get(position).getName();
            Callback.cat_name = catName;
            Callback.search_item = "";
            startActivity(new Intent(CategoryActivity.this, AllPostActivity.class));
        });

        arrayList_cat = new ArrayList<>();
        arrayList_sub_cat = new ArrayList<>();

        pb = findViewById(R.id.pb);
        frameLayout = findViewById(R.id.fl_empty);

        rv_cat = findViewById(R.id.rv_cat);
        LinearLayoutManager llm = new LinearLayoutManager(CategoryActivity.this);
        rv_cat.setLayoutManager(llm);
        rv_cat.setItemAnimator(new DefaultItemAnimator());
        rv_cat.setHasFixedSize(true);

        rv_sub_cat = findViewById(R.id.rv_sub_cat);
        GridLayoutManager grid = new GridLayoutManager(this, 3);
        grid.setSpanCount(3);
        rv_sub_cat.setLayoutManager(grid);
        rv_sub_cat.setItemAnimator(new DefaultItemAnimator());
        rv_sub_cat.setHasFixedSize(true);

        setCategoryAdapter();

        findViewById(R.id.iv_change).setOnClickListener(v -> getData());
        findViewById(R.id.ll_view_all).setOnClickListener(v -> {
            Callback.city_id = "";
            Callback.scat_id = "";
            Callback.cat_id = arrayList_cat.get(position_cat).getId();
            Callback.cat_name = arrayList_cat.get(position_cat).getName();
            Callback.search_item = "";
            startActivity(new Intent(CategoryActivity.this, AllPostActivity.class));
        });

    }

    private void setCategoryAdapter() {
        if (!arrayList_cat.isEmpty()){
            arrayList_cat.clear();
        }
        arrayList_cat = dbHelper.getCategory(null);
        if (!arrayList_cat.isEmpty()) {
            adapter = new AdapterSuperCategory(arrayList_cat, (itemCat, position) -> {
                adapter.select(position);
                position_cat = position;
                if (!arrayList_cat.get(position).getId().equals(catID)){
                    catID = arrayList_cat.get(position).getId();
                    catName = arrayList_cat.get(position).getName();
                    getSubCategory();
                }
            });
            rv_cat.setAdapter(adapter);
            adapter.select(0);
            catID = arrayList_cat.get(0).getId();
            catName = arrayList_cat.get(0).getName();
            getSubCategory();
            setEmpty();
        } else {
            getData();
        }
    }

    private void getSubCategory() {
        if (!arrayList_sub_cat.isEmpty()){
            arrayList_sub_cat.clear();
        }
        arrayList_sub_cat = dbHelper.getSubCategory(catID);
        if (!arrayList_sub_cat.isEmpty()) {
            AdapterSuperCategorySub adapterSuper = new AdapterSuperCategorySub(arrayList_sub_cat, (itemCat, position) -> helper.showInterAd(position,""));
            rv_sub_cat.setAdapter(adapterSuper);
        }
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadFilter loadFilter = new LoadFilter(CategoryActivity.this, new FilterListener() {
                @Override
                public void onStart() {
                    frameLayout.setVisibility(View.GONE);
                    rv_sub_cat.setVisibility(View.GONE);
                    rv_cat.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemCity> arrayListCity, ArrayList<ItemCategory> arrayListCategory, ArrayList<ItemSubCategory> arrayListSubCategory) {
                    if (success.equals("1")) {
                        if (arrayListCategory.isEmpty()) {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            setCategoryAdapter();
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

    @SuppressLint("InflateParams")
    private void setEmpty() {
        if (!arrayList_cat.isEmpty()) {
            pb.setVisibility(View.INVISIBLE);
            rv_cat.setVisibility(View.VISIBLE);
            rv_sub_cat.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            rv_cat.setVisibility(View.GONE);
            rv_sub_cat.setVisibility(View.GONE);
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
        return R.layout.activity_category;
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