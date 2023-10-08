package nemosofts.classified.activity.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterComment;
import nemosofts.classified.asyncTask.LoadCommentPost;
import nemosofts.classified.asyncTask.LoadComments;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.CommentListener;
import nemosofts.classified.interfaces.PostCommentListener;
import nemosofts.classified.item.ItemComment;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.EndlessRecyclerViewScrollListener;

public class CommentActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private RecyclerView rv_comment;
    private AdapterComment adapter;
    private ArrayList<ItemComment> arrayList;
    private String post_id = "", user_id = "";
    private int page = 1;
    private Boolean isOver = false, isScroll = false, isPost = true;
    private String error_msg;
    private FrameLayout frameLayout;
    private ProgressBar pb;
    private EditText et_comment;

    @SuppressLint("ClickableViewAccessibility")
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

        post_id = getIntent().getStringExtra("post_id");
        user_id = getIntent().getStringExtra("user_id");

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv_comment = findViewById(R.id.rv_comment);
        pb = findViewById(R.id.pb);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_comment.setLayoutManager(llm);
        rv_comment.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (Boolean.FALSE.equals(isOver)) {
                    new Handler().postDelayed(() -> {
                        isScroll = true;
                        loadComments();
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        et_comment = findViewById(R.id.et_comment);
        findViewById(R.id.iv_comment).setOnClickListener(v -> {
            if (sharedPref.isLogged()) {
                if (et_comment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CommentActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (helper.isNetworkAvailable()) {
                        if (isPost){
                            isPost= false;
                            sendComment();
                        } else {
                            Toast.makeText(CommentActivity.this, "Please wait a minute", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CommentActivity.this, getResources().getString(R.string.conn_net_post_comment), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                helper.clickLogin();
            }
        });

        et_comment.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });

        et_comment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND){
                if (sharedPref.isLogged()) {
                    if (et_comment.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CommentActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                    } else {
                        if (helper.isNetworkAvailable()) {
                            if (Boolean.TRUE.equals(isPost)){
                                isPost= false;
                                sendComment();
                            } else {
                                Toast.makeText(CommentActivity.this, "Please wait a minute", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CommentActivity.this, getResources().getString(R.string.conn_net_post_comment), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    helper.clickLogin();
                }
            }
            return true;
        });

        loadComments();
    }

    private void sendComment() {
        LoadCommentPost loadCommentPost = new LoadCommentPost(new PostCommentListener() {
            @Override
            public void onStart() {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment) {
                isPost= true;
                if (success.equals("1")) {
                    if (isCommentPosted.equals("1")) {
                        arrayList.add(0, itemComment);
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        } else {
                            isScroll = false;
                            setAdapter();
                        }
                        et_comment.setText("");
                        rv_comment.smoothScrollToPosition(0);
                    }
                    Toast.makeText(CommentActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                }
            }
        }, helper.getAPIRequest(Callback.METHOD_POST_COMMENTS, 0, post_id, "", "", et_comment.getText().toString(), sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
        loadCommentPost.execute();
    }

    private void loadComments() {
        if (helper.isNetworkAvailable()) {
            LoadComments loadComments = new LoadComments(new CommentListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv_comment.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, ArrayList<ItemComment> arrayListComment) {
                    if (success.equals("1")) {
                        if (arrayListComment.isEmpty()) {
                            isOver = true;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            error_msg = getString(R.string.no_comment);
                            setEmpty();
                        } else {
                            arrayList.addAll(arrayListComment);
                            page = page + 1;
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_COMMENTS, page, post_id, "", "", "", sharedPref.getUserId(), "", "", "","","","","",null, null));
            loadComments.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if(Boolean.FALSE.equals(isScroll)) {
            adapter = new AdapterComment(user_id,this,arrayList);
            rv_comment.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("InflateParams")
    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv_comment.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            rv_comment.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> loadComments());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_comment;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}