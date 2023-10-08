package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterFilter;
import nemosofts.classified.asyncTask.LoadFilter;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.FilterListener;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemFilter;
import nemosofts.classified.item.ItemSubCategory;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;

public class FilterActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private AdapterFilter adapter;
    private ArrayList<ItemFilter> arrayListCategory;
    private ArrayList<ItemFilter> arrayListSubCategory;
    private String type = "cat", typeOld = "";
    private String cat_id = "";
    int selectedPos = -1, positionCat=0;
    private Boolean add_cat = false;
    private Button filter_remove;
    private TextView tv_filter_title_back, tv_filter_title_all;
    private LinearLayout filter_menu, filter_menu_all;
    private ProgressBar progressBar;

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

        type = getIntent().getExtras().getString("type");
        typeOld = getIntent().getExtras().getString("type");

        helper = new Helper(this);
        dbHelper = new DBHelper(this);

        arrayListCategory = new ArrayList<>();
        arrayListSubCategory = new ArrayList<>();

        filter_remove = findViewById(R.id.button_filter_remove);
        filter_menu = findViewById(R.id.filter_menu);
        filter_menu_all= findViewById(R.id.filter_menu_all);
        tv_filter_title_back = findViewById(R.id.tv_filter_title_back);
        tv_filter_title_all = findViewById(R.id.tv_filter_title_all);
        progressBar = findViewById(R.id.pb_filter);

        filter_menu.setVisibility(View.GONE);
        filter_menu_all.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rv_filter);
        LinearLayoutManager llm = new LinearLayoutManager(FilterActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        filter_menu_all.setOnClickListener(v -> applyFilter());
        filter_remove.setOnClickListener(view -> {
            if (!Callback.city_id.isEmpty() || !Callback.cat_id.isEmpty() || !Callback.scat_id.isEmpty()){
                Callback.city_id =  "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.scat_id = "";
                Callback.scat_name = "";
                selectedPos = -1;
                Intent intent = new Intent();
                intent.putExtra("result", "remove");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if (Callback.city_id.isEmpty() && Callback.cat_id.isEmpty() && Callback.scat_id.isEmpty()){
            filter_remove.setBackgroundColor(ColorUtils.colorBorder(this));
        } else {
            filter_remove.setBackgroundColor(ContextCompat.getColor(this, R.color.color_filter_remove));
        }

        findViewById(R.id.button_filter).setOnClickListener(view -> applyFilter());
        findViewById(R.id.iv_change).setOnClickListener(v -> getFilterData());
        findViewById(R.id.tv_backspace).setOnClickListener(v -> {
            ArrayList<ItemFilter> arrayListClear = new ArrayList<>();
            adapter = new AdapterFilter(this,arrayListClear);
            Callback.scat_id = "";
            Callback.scat_name= "";
            filter_menu.setVisibility(View.GONE);
            filter_menu_all.setVisibility(View.GONE);
            cat_id = "";
            type = typeOld;
            getDB(type);
        });

        getDB(type);
    }

    private void getFilterData() {
        if (helper.isNetworkAvailable()) {
            LoadFilter loadFilter = new LoadFilter(FilterActivity.this, new FilterListener() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemCity> arrayListCity, ArrayList<ItemCategory> arrayListCategory, ArrayList<ItemSubCategory> arrayListSubCategory) {
                    progressBar.setVisibility(View.GONE);
                    if (success.equals("1")) {
                        getDB(type);
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_FILTER_LIST, 0, "", "", "", "", "", "", "", "", "", "", "", "", null, null));
            loadFilter.execute();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getDB(@NonNull String dataType) {
        if (dataType.equals("cat")){
            if (!arrayListCategory.isEmpty()){
                arrayListCategory.clear();
            }
            ArrayList<ItemCategory> arrayListCat = dbHelper.getCategory(null);
            if (!arrayListCat.isEmpty()){
                for (int i = 0; i < arrayListCat.size(); i++) {
                    arrayListCategory.add(new ItemFilter(arrayListCat.get(i).getId(), arrayListCat.get(i).getName()));
                }
                setAdapter(false);
            } else {
                getFilterData();
            }
        }

        if (dataType.equals("city")){
            if (!arrayListCategory.isEmpty()){
                arrayListCategory.clear();
            }
            ArrayList<ItemCity> arrayListCity = dbHelper.getCity();
            if (!arrayListCity.isEmpty()){
                for (int i = 0; i < arrayListCity.size(); i++) {
                    arrayListCategory.add(new ItemFilter(arrayListCity.get(i).getId(), arrayListCity.get(i).getName()));
                }
                setAdapter(false);
            } else {
                getFilterData();
            }
        }

        if (dataType.equals("scat")){
            if (!arrayListSubCategory.isEmpty()){
                arrayListSubCategory.clear();
            }
            ArrayList<ItemSubCategory> arrayListSubCat = dbHelper.getSubCategory(cat_id);
            if (!arrayListSubCat.isEmpty()){
                for (int i = 0; i < arrayListSubCat.size(); i++) {
                    arrayListSubCategory.add(new ItemFilter(arrayListSubCat.get(i).getId(), arrayListSubCat.get(i).getName()));
                }
                setAdapter(true);
            } else {
                getFilterData();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void setAdapter(Boolean isSubCategory) {
        if (recyclerView.getVisibility() == View.GONE){
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (Boolean.TRUE.equals(isSubCategory)){
            adapter = new AdapterFilter(this,arrayListSubCategory);
        } else {
            adapter = new AdapterFilter(this,arrayListCategory);
        }
        recyclerView.setAdapter(adapter);
        adapter.select(selectedPos);

        adapter.setOnItemClickListener(position -> {
            adapter.select(position);
            selectedPos = position;
            if (type.equals("cat")){
                ItemFilter filter = arrayListCategory.get(position);
                ArrayList<ItemFilter> arrayListClear = new ArrayList<>();
                adapter = new AdapterFilter(this,arrayListClear);

                String isId = filter.getId();
                String isName = filter.getName();

                filter_menu.setVisibility(View.VISIBLE);
                filter_menu_all.setVisibility(View.VISIBLE);
                tv_filter_title_back.setText("Back to All categories");
                tv_filter_title_all.setText("Apply All ads in "+isName);

                cat_id = isId;
                type = "scat";
                Callback.cat_id = isId;
                Callback.cat_name = isName;
                Callback.scat_id = "";
                Callback.scat_name = "";
                positionCat = position;
                selectedPos = -1;
                add_cat = true;

                getDB("scat");
            }
            if (Callback.city_id.isEmpty() || Callback.cat_id.isEmpty() || Callback.scat_id.isEmpty()){
                filter_remove.setBackgroundColor(ColorUtils.colorBorder(this));
            } else {
                filter_remove.setBackgroundColor(ContextCompat.getColor(this, R.color.color_filter_remove));
            }
        });
    }

    private void applyFilter() {
        Intent intent = new Intent();
        intent.putExtra("result", "add");
        if (selectedPos != -1){
            ItemFilter itemPlan;
            if (type.equals("scat")){
                itemPlan = arrayListSubCategory.get(selectedPos);
            } else {
                itemPlan = arrayListCategory.get(selectedPos);
            }
            String isId = itemPlan.getId();
            String isName = itemPlan.getName();
            switch (type) {
                case "city":
                    Callback.city_id = isId;
                    Callback.city_name = isName;
                    break;
                case "cat":
                    Callback.cat_id = isId;
                    Callback.cat_name = isName;
                    break;
                case "scat":
                    Callback.scat_id = isId;
                    Callback.scat_name = isName;
                    break;
            }
            setResult(RESULT_OK, intent);
        } else if (Boolean.TRUE.equals(add_cat)){
            ItemFilter itemPlan = arrayListCategory.get(positionCat);
            String isId = itemPlan.getId();
            String isName = itemPlan.getName();
            Callback.cat_id = isId;
            Callback.cat_name = isName;
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_filter;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}