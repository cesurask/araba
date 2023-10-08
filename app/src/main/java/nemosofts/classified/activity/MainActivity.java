package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.activity.addEdit.AddPostActivity;
import nemosofts.classified.activity.chat.ChatPostActivity;
import nemosofts.classified.adapter.home.AdapterHome;
import nemosofts.classified.asyncTask.LoadAbout;
import nemosofts.classified.asyncTask.LoadHome;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.dialog.ExitDialog;
import nemosofts.classified.dialog.InvalidUserDialog;
import nemosofts.classified.interfaces.AboutListener;
import nemosofts.classified.interfaces.HomeListener;
import nemosofts.classified.item.ItemPostHome;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.advertising.AdManagerInterAdmob;
import nemosofts.classified.utils.advertising.AdManagerInterApplovin;
import nemosofts.classified.utils.advertising.AdManagerInterStartApp;
import nemosofts.classified.utils.advertising.AdManagerInterWortise;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Helper helper;
    DBHelper dbHelper;
    SharedPref sharedPref;
    MenuItem menu_login, menu_profile;
    ReviewManager manager;
    ReviewInfo reviewInfo;

    ProgressBar progressBar;
    FrameLayout frameLayout;
    RecyclerView rv_home;
    AdapterHome adapterHome;
    ArrayList<ItemPostHome> arrayList;
    private String errr_msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new Helper(this);
        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        toggle.setToolbarNavigationClickListener(view -> drawer.openDrawer(GravityCompat.START));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode())) {
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_profile = menu.findItem(R.id.nav_profile);

        changeLoginName();
        loadAboutData();

        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });

        arrayList = new ArrayList<>();

        progressBar = findViewById(R.id.pb_home);
        frameLayout = findViewById(R.id.fl_empty);
        rv_home = findViewById(R.id.rv_home);

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        rv_home.setLayoutManager(llm);
        rv_home.setItemAnimator(new DefaultItemAnimator());
        rv_home.setHasFixedSize(true);

        loadHome();

        findViewById(R.id.iv_add_post).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddPostActivity.class)));
        findViewById(R.id.iv_search).setOnClickListener(v -> {
            Callback.city_id = "";
            Callback.city_name = "City";
            Callback.cat_id = "";
            Callback.cat_name = "Category";
            Callback.search_item = "";
            startActivity(new Intent(MainActivity.this, AllPostActivity.class));
        });
    }

    private void loadHome() {
        if (helper.isNetworkAvailable()) {
            LoadHome loadHome = new LoadHome(this, new HomeListener() {
                @Override
                public void onStart() {
                    frameLayout.setVisibility(View.GONE);
                    rv_home.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String message, ArrayList<ItemPostHome> arrayListPost) {
                    if (success.equals("1")) {

                        if (!arrayListPost.isEmpty()){

                            arrayList.addAll(arrayListPost);

                            adapterHome = new AdapterHome(MainActivity.this, arrayListPost);
                            rv_home.setAdapter(adapterHome);
                            setEmpty();
                        } else {
                            errr_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        }
                    } else if (success.equals("-2")) {
                        new InvalidUserDialog(MainActivity.this, message);
                    } else {
                        errr_msg = getString(R.string.err_server);
                        setEmpty();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }, helper.getAPIRequest(Callback.METHOD_HOME, 0, "", "", "", "", new SharedPref(this).getUserId(), "", "", "", "", "", "", "", null, null));
            loadHome.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    public void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv_home.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            rv_home.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(errr_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> loadHome());

            frameLayout.addView(myView);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeLoginName() {
        if (menu_login != null) {
            if (sharedPref.isLogged()) {
                menu_profile.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.drawable.ic_logout));
            } else {
                menu_profile.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.drawable.ic_login));
            }
        }
    }

    public void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        helper.initializeAds();
                        dbHelper.addtoAbout();
                        if (Boolean.TRUE.equals(Callback.isInterAd)) {
                            switch (Callback.adNetwork) {
                                case Callback.AD_TYPE_ADMOB:
                                    AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(getApplicationContext());
                                    adManagerInterAdmob.createAd();
                                    break;
                                case Callback.AD_TYPE_STARTAPP:
                                    AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(getApplicationContext());
                                    adManagerInterStartApp.createAd();
                                    break;
                                case Callback.AD_TYPE_APPLOVIN:
                                    AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(MainActivity.this);
                                    adManagerInterApplovin.createAd();
                                    break;
                                case Callback.AD_TYPE_WORTISE:
                                    AdManagerInterWortise adManagerInterWortise = new AdManagerInterWortise(MainActivity.this);
                                    adManagerInterWortise.createAd();
                                    break;
                            }
                        }
                    }
                }
            });
            loadAbout.execute();
        } else {
            try {
                dbHelper.getAbout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_latest:
                Callback.city_id = "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.search_item = "";
                startActivity(new Intent(MainActivity.this, AllPostActivity.class));
                break;
            case R.id.nav_city:
                startActivity(new Intent(MainActivity.this, CityActivity.class));
                break;
            case R.id.nav_category:
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                break;
            case R.id.nav_post_add:
                startActivity(new Intent(MainActivity.this, AddPostActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_favourite:
                startActivity(new Intent(MainActivity.this, FavActivity.class));
                break;
            case R.id.nav_chat:
                startActivity(new Intent(MainActivity.this, ChatPostActivity.class));
                break;
            case R.id.nav_database:
                startActivity(new Intent(MainActivity.this, DatabaseActivity.class));
                break;
            case R.id.nav_settings:
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
                break;
            case R.id.nav_login:
                helper.clickLogin();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        changeLoginName();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
            flow.addOnCompleteListener(task1 -> new ExitDialog(this));
        } else {
            new ExitDialog(this);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}