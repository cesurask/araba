package nemosofts.classified.activity.payment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;

import java.io.File;
import java.io.IOException;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadBankDetails;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.BankDetailsListener;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class BankPayActivity extends AppCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private ImageView iv_image;
    private String imagePath = "";
    private final int PICK_IMAGE_REQUEST = 1;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private String planPrice;
    private String post_id;
    private String planDays, planDays2, planDays3;
    private int isExpandable, isExpandable2, isExpandable3;
    private TextView tv_totalPrice_new, tv_Price_ta;
    private ProgressBar pb;
    private NestedScrollView nv_bank_pay;

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

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        progressDialog = new ProgressDialog(BankPayActivity.this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        planDays = intent.getStringExtra("planDays");
        planDays2 = intent.getStringExtra("planDays2");
        planDays3 = intent.getStringExtra("planDays3");
        isExpandable = intent.getIntExtra("isExpandable",0);
        isExpandable2 = intent.getIntExtra("isExpandable2",0);
        isExpandable3 = intent.getIntExtra("isExpandable3",0);
        planPrice = intent.getStringExtra("planPrice");
        String planCurrency = intent.getStringExtra("planCurrency");

        pb = findViewById(R.id.pb);
        nv_bank_pay = findViewById(R.id.nv_bank_pay);

        tv_totalPrice_new = findViewById(R.id.tv_totalPrice_new);
        tv_Price_ta= findViewById(R.id.tv_Price_ta);

        tv_Price_ta.setText(planCurrency);
        double result = Double.parseDouble(planPrice);
        String finalResult = Double.toString(result);
        tv_totalPrice_new.setText(finalResult+"0");

        iv_image = findViewById(R.id.iv_upload_wall_submit);
        if (Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode())){
            iv_image.setImageResource(R.drawable.placeholder_upload_night);
        } else {
            iv_image.setImageResource(R.drawable.placeholder_upload_light);
        }
        iv_image.setOnClickListener(view -> {
            if (checkPer()) {
                pickImage();
            }
        });

        findViewById(R.id.tv_btn_continue).setOnClickListener(view -> {
            if (sharedPref.isLogged()) {
                if (helper.isNetworkAvailable()) {
                    if (imagePath.equals("")) {
                        Toast.makeText(BankPayActivity.this, getResources().getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                    } else{
                        try {
                            uploadData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(BankPayActivity.this, getResources().getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
                }
            } else {
                helper.clickLogin();
            }
        });

        getData();
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadBankDetails loadBank = new LoadBankDetails(new BankDetailsListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                    nv_bank_pay.setVisibility(View.GONE);
                }
                @Override
                public void onEnd(String success, String registerSuccess, String message, String name, String account_number, String details) {
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")) {
                        TextView et_bank_name = findViewById(R.id.tv_bank_name);
                        et_bank_name.setText(name);

                        TextView et_account_number = findViewById(R.id.tv_account_number);
                        et_account_number.setText(account_number);

                        TextView et_account_details = findViewById(R.id.tv_account_details);
                        et_account_details.setText(details);

                        nv_bank_pay.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(BankPayActivity.this, getResources().getString(R.string.err_server_not_connected), Toast.LENGTH_LONG).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_BANK_DETAILS, 0, "", "", "", "", "", "", "", "","","","","",null, null));
            loadBank.execute();
        } else {
            Toast.makeText(BankPayActivity.this, getResources().getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    if (registerSuccess.equals("1")) {
                        if (Boolean.TRUE.equals(new ThemeEngine(BankPayActivity.this).getIsThemeMode())){
                            iv_image.setImageResource(R.drawable.placeholder_upload_night);
                        } else {
                            iv_image.setImageResource(R.drawable.placeholder_upload_light);
                        }
                        imagePath = "";
                    }
                    Toast.makeText(BankPayActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BankPayActivity.this, getResources().getString(R.string.err_server_not_connected), Toast.LENGTH_LONG).show();
                }
            }
        }, helper.getAPIRequestTransaction(Callback.METHOD_BANK_PAY, post_id , sharedPref.getUserId() , "none", "Bank Transfer",
                isExpandable, isExpandable2, isExpandable3, planDays, planDays2,planDays3, planPrice,  new File(imagePath)));
        loadStatus.execute();
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_bank_pay;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @NonNull
    private Boolean checkPer() {
        if (Build.VERSION.SDK_INT >= 33){
            if ((ContextCompat.checkSelfPermission(BankPayActivity.this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_MEDIA_IMAGES}, 101);
                return false;
            } else {
                return true;
            }
        } else if (Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(BankPayActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(BankPayActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean canUseExternalStorage = false;
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canUseExternalStorage = true;
            }
            if (!canUseExternalStorage) {
                Toast.makeText(BankPayActivity.this, getResources().getString(R.string.err_cannot_use_features), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imagePath = helper.getPathImage(uri);
            try {
                Bitmap bitmap_upload = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                iv_image.setImageBitmap(bitmap_upload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}