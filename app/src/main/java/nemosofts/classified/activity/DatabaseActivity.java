package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterPostDelete;
import nemosofts.classified.asyncTask.LoadPostDatabase;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.DatabaseListener;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class DatabaseActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private RecyclerView rv;
    private AdapterPostDelete adapter;
    private ArrayList<ItemPostDatabase> arrayList;
    private ProgressBar pb;
    private FloatingActionButton fab;
    private String error_msg;
    private FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.database));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        fab = findViewById(R.id.fab);
        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = llm.findFirstVisibleItemPosition();
                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(v -> rv.smoothScrollToPosition(0));

        getData();
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadPostDatabase loadPost = new LoadPostDatabase(new DatabaseListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemPostDatabase> itemPosts) {
                    if (success.equals("1")) {
                        if (itemPosts.isEmpty()) {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            arrayList.addAll(itemPosts);
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_DATABASE, 0, "", "", "", "", sharedPref.getUserId(), "", "", "","","","","",null, null));
            loadPost.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        adapter = new AdapterPostDelete(DatabaseActivity.this, arrayList);
        rv.setAdapter(adapter);
        rv.scheduleLayoutAnimation();
        setEmpty();
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        if (Boolean.TRUE.equals(Callback.isProfileUpdate)) {
            Callback.isProfileUpdate = false;
            if(!arrayList.isEmpty()) {
                arrayList.clear();
            }
            if (adapter != null){
                adapter.notifyDataSetChanged();
            }
            getData();
        }
        super.onResume();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_database;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}