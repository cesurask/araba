package nemosofts.classified.activity.chat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.adapter.AdapterChat;
import nemosofts.classified.asyncTask.LoadChat;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.ChatListener;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemChat;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import okhttp3.RequestBody;

public class ChatPrivateActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private String post_id = "", post_user_id = "", chat_user_id = "", authID= "";
    private ProgressBar pb_send;
    private ProgressDialog progressDialog;
    private EditText et_chat;
    private ImageView iv_chat;
    private final int PICK_IMAGE_REQUEST = 1;
    private String imagePath = "";
    private RecyclerView rv;
    private ArrayList<ItemChat> arrayList;
    private String error_msg;
    private FrameLayout frameLayout;
    private ProgressBar pb;
    private AdapterChat adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setTitle(getString(R.string.chat_private));

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        post_user_id = intent.getStringExtra("post_user_id");
        chat_user_id = intent.getStringExtra("chat_user_id");

        progressDialog = new ProgressDialog(ChatPrivateActivity.this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        if (post_user_id.equals(sharedPref.getUserId())){
            authID = chat_user_id;
        } else {
            authID = sharedPref.getUserId();
        }

        pb_send = findViewById(R.id.pb_send);

        et_chat = findViewById(R.id.et_chat);
        iv_chat = findViewById(R.id.iv_chat);
        iv_chat.setOnClickListener(v -> {
            if (sharedPref.isLogged()) {
                if (et_chat.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ChatPrivateActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (pb_send.getVisibility() == View.INVISIBLE){
                        sendComment(false);
                    }
                }
            } else {
                helper.clickLogin();
            }
        });
        et_chat.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND){
                if (sharedPref.isLogged()) {
                    if (et_chat.getText().toString().trim().isEmpty()) {
                        Toast.makeText(ChatPrivateActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                    } else {
                        if (pb_send.getVisibility() == View.INVISIBLE){
                            sendComment(false);
                        }
                    }
                } else {
                    helper.clickLogin();
                }
            }
            return true;
        });

        findViewById(R.id.iv_add_img).setOnClickListener(v -> {
            if (Boolean.TRUE.equals(checkPer())) {
                pickImage();
            }
        });

        arrayList = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        rv = findViewById(R.id.rv_chat);
        pb = findViewById(R.id.pb);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        loadChat();
    }

    private void loadChat() {
        if (helper.isNetworkAvailable()) {
            LoadChat loadComments = new LoadChat(new ChatListener() {
                @Override
                public void onStart() {
                    if (arrayList.isEmpty()) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemChat> arrayListChat) {
                    if (success.equals("1")) {
                        if (arrayListChat.isEmpty()) {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        } else {
                            if (!arrayList.isEmpty()){
                                arrayList.clear();
                            }
                            arrayList.addAll(arrayListChat);
                            setAdapter();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_GET_CHAT, 0, post_id, post_user_id, "", "", sharedPref.getUserId(), "", "", "", "", "", authID, "", null, null));
            loadComments.execute();
        } else {
            Toast.makeText(ChatPrivateActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            rv.smoothScrollToPosition(arrayList.size());
        } else {
            adapter = new AdapterChat(this,arrayList);
            rv.setAdapter(adapter);
            setEmpty();
        }
    }

    private void sendComment(boolean isImage) {
        if (helper.isNetworkAvailable()) {
            RequestBody requestBody;
            if (isImage){
                requestBody = helper.getAPIRequest(Callback.METHOD_SEND_CHAT, 0, post_id, post_user_id, "", "", sharedPref.getUserId(), "", "", "", "", "", authID, "img", new File(imagePath), null);
            } else {
                requestBody = helper.getAPIRequest(Callback.METHOD_SEND_CHAT, 0, post_id, post_user_id, et_chat.getText().toString().replace(" ","%20"), "", sharedPref.getUserId(), "", "", "", "", "", authID, "chat", null, null);
            }
            LoadStatus sendComment = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    if (isImage){
                        progressDialog.show();
                    }
                    pb_send.setVisibility(View.VISIBLE);
                    iv_chat.setVisibility(View.INVISIBLE);
                    et_chat.setEnabled(false);
                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    if (success.equals("1")) {
                        et_chat.setText("");
                        loadChat();
                        Toast.makeText(ChatPrivateActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatPrivateActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    pb_send.setVisibility(View.INVISIBLE);
                    iv_chat.setVisibility(View.VISIBLE);
                    et_chat.setEnabled(true);
                }
            }, requestBody);
            sendComment.execute();
        } else {
            Toast.makeText(ChatPrivateActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    public Boolean checkPer() {
        if (Build.VERSION.SDK_INT >= 33){
            if ((ContextCompat.checkSelfPermission(ChatPrivateActivity.this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_MEDIA_IMAGES}, 101);
                return false;
            } else {
                return true;
            }
        } else if (Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(ChatPrivateActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 101);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(ChatPrivateActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 101);
                return false;
            }
            return true;
        }
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

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> loadChat());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_chat_private;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean canUseExternalStorage = false;
        if (requestCode == 101 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canUseExternalStorage = true;
            }
            if (!canUseExternalStorage) {
                Toast.makeText(ChatPrivateActivity.this, getResources().getString(R.string.err_cannot_use_features), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imagePath = helper.getPathImage(uri);
            sendComment(true);
        }
    }
}