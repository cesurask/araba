package nemosofts.classified.activity.chat;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterChatPost;
import nemosofts.classified.asyncTask.LoadChatPost;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.ChatPostListener;
import nemosofts.classified.item.ItemChatPost;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.EndlessRecyclerViewScrollListener;

public class ChatPostActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private RecyclerView rv;
    private AdapterChatPost adapter;
    private ArrayList<ItemChatPost> arrayList;
    private Boolean isOver = false, isScroll = false;
    private int page = 1;
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
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
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
            LoadChatPost loadPost = new LoadChatPost(new ChatPostListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemChatPost> arrayListChatPost) {
                    if (success.equals("1")) {
                        if (arrayListChatPost.isEmpty()) {
                            isOver = true;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            arrayList.addAll(arrayListChatPost);
                            page = page + 1;
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_CHAT_POST, page, "", "", "", "", sharedPref.getUserId(), "", "", "","","","","",null, null));
            loadPost.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if(Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterChatPost(ChatPostActivity.this, arrayList, (itemChatPost, position) -> {
                Intent intentComment = new Intent(ChatPostActivity.this, ChatPrivateActivity.class);
                intentComment.putExtra("post_id", arrayList.get(position).getPostId());
                intentComment.putExtra("post_user_id", arrayList.get(position).getPostUserId());
                intentComment.putExtra("chat_user_id", arrayList.get(position).getChatUserId());
                intentComment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(ChatPostActivity.this, intentComment, null);
            });
            rv.setAdapter(adapter);
            rv.scheduleLayoutAnimation();
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

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> getData());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_chat_post;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}