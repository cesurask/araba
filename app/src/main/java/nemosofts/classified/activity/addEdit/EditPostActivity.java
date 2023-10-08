package nemosofts.classified.activity.addEdit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import nemosofts.classified.R;
import nemosofts.classified.adapter.gallery.AdapterGalleryEdit;
import nemosofts.classified.asyncTask.LoadDetailsPost;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.DetailsListener;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemCategory;
import nemosofts.classified.item.ItemCity;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.item.ItemGallery;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.item.ItemSubCategory;
import nemosofts.classified.item.ItemUser;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;
import okhttp3.RequestBody;

public class EditPostActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private ArrayList<String> arrayListType;
    private ArrayList<String> arrayList_cat, arrayList_catID;
    private ArrayList<String> arrayList_sub_cat, arrayList_sub_catID;
    private ArrayList<String> arrayList_districts, arrayList_districtsID;
    private Spinner spinner_type, spinner_category, spinner_sub_category, spinner_districts;
    private String cat_id = "", type = "", sub_cat_id = "", districts_id = "";
    private final int PICK_IMAGE_REQUEST = 1;
    private final int PICK_CAMERA_REQUEST= 2;
    private String imagePath = "";
    private Uri mImageCaptureUri;
    private ImageView iv_item_post;
    private RecyclerView rv_upload_gallery;
    private ArrayList<File> arrayList_gallery;
    private AdapterGalleryEdit adapterGallery;
    private RoundedImageView iv_upload_gallery;
    private final int PICK_MULTIPLE_IMAGE_REQUEST = 5;
    private EditText et_title, et_price, et_description, et_user_mobile, et_user_work_mobile;
    private String post_id="", user_id="";
    private ItemPostDatabase itemPost;
    private ArrayList<ItemGallery> arrayList_item_gallery;
    private Boolean add = false;

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
        toolbar.setTitle(getString(R.string.edit_post));

        post_id = getIntent().getStringExtra("post_id");
        user_id = getIntent().getStringExtra("user_id");

        progressDialog = new ProgressDialog(EditPostActivity.this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        helper = new Helper(this);
        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(this);

        arrayListType = new ArrayList<>();
        arrayList_gallery = new ArrayList<>();
        arrayList_item_gallery = new ArrayList<>();

        arrayList_cat = new ArrayList<>();
        arrayList_catID = new ArrayList<>();

        arrayList_sub_cat = new ArrayList<>();
        arrayList_sub_catID = new ArrayList<>();

        arrayList_districts = new ArrayList<>();
        arrayList_districtsID = new ArrayList<>();

        arrayListType.add("Use");
        arrayListType.add("New");

        iv_item_post = findViewById(R.id.iv_item_post);
        iv_upload_gallery = findViewById(R.id.iv_upload_gallery);
        rv_upload_gallery = findViewById(R.id.rv_upload_gallery);

        et_title = findViewById(R.id.et_title);
        et_price = findViewById(R.id.et_price);
        et_description = findViewById(R.id.et_description);
        et_user_mobile = findViewById(R.id.et_user_mobile);
        et_user_work_mobile = findViewById(R.id.et_user_work_mobile);

        rv_upload_gallery.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        if (Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode())){
            iv_item_post.setImageResource(R.drawable.placeholder_upload_night);
            iv_upload_gallery.setImageResource(R.drawable.placeholder_upload_night);
        } else {
            iv_item_post.setImageResource(R.drawable.placeholder_upload_light);
            iv_upload_gallery.setImageResource(R.drawable.placeholder_upload_light);
        }

        spinner_type = findViewById(R.id.spinner_type);
        spinner_category = findViewById(R.id.spinner_category);
        spinner_sub_category = findViewById(R.id.spinner_sub_category);
        spinner_districts = findViewById(R.id.spinner_districts);
        selectedListener();

        iv_item_post.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(checkPer())) {
                showCameraDialog();
            }
        });

        iv_upload_gallery.setOnClickListener(view -> {
            if (Boolean.TRUE.equals(checkPer())) {
                pickMultipleImage();
            }
        });

        findViewById(R.id.btn_upload_add_gallery).setOnClickListener(view -> {
            if (checkPer()) {
                pickMultipleImage();
            }
        });

        findViewById(R.id.tv_btn_continue).setOnClickListener(view -> {
            if (validate()) {
                uploadPost();
            }
        });

        LoadDetails();
    }

    private void LoadDetails() {
        if (helper.isNetworkAvailable()) {
            LoadDetailsPost detailsPost = new LoadDetailsPost(new DetailsListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, ArrayList<ItemPostDatabase> postArrayList, ArrayList<ItemData> dataArrayList, ArrayList<ItemUser> userArrayList, ArrayList<ItemGallery> arrayListGallery, Boolean isBlockShop) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (!postArrayList.isEmpty()){
                            itemPost = postArrayList.get(0);
                            if (!arrayListGallery.isEmpty()){
                                arrayList_item_gallery.addAll(arrayListGallery);
                            }

                            getData();
                        } else {
                            Toast.makeText(EditPostActivity.this, getString(R.string.err_no_data_found), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(EditPostActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_DETAILS, 0, post_id, user_id, "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            detailsPost.execute();
        } else {
            Toast.makeText(EditPostActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectedListener() {
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    type = "Use";
                } else if (i == 1) {
                    type = "New";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!arrayList_catID.isEmpty()) {
                    cat_id = arrayList_catID.get(position);
                } else {
                    cat_id = "";
                }
                if (Boolean.TRUE.equals(add)) {
                    getSubCategory();
                }
                add = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_sub_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!arrayList_sub_catID.isEmpty()) {
                    sub_cat_id = arrayList_sub_catID.get(position);
                } else {
                    sub_cat_id ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_districts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!arrayList_districtsID.isEmpty()) {
                    districts_id = arrayList_districtsID.get(position);
                } else {
                    districts_id ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void uploadPost() {
        if (helper.isNetworkAvailable()) {
            RequestBody requestBody;
            if (imagePath.equals("")){
                requestBody = helper.getAPIRequest(Callback.METHOD_EDIT_UPLOAD_POST,0,sub_cat_id,cat_id,districts_id,et_title.getText().toString(),sharedPref.getUserId(),et_price.getText().toString(),et_description.getText().toString(),et_user_mobile.getText().toString(),et_user_work_mobile.getText().toString(), "",post_id, type, null, arrayList_gallery);
            } else {
                requestBody = helper.getAPIRequest(Callback.METHOD_EDIT_UPLOAD_POST,0,sub_cat_id,cat_id,districts_id,et_title.getText().toString(),sharedPref.getUserId(),et_price.getText().toString(),et_description.getText().toString(),et_user_mobile.getText().toString(),et_user_work_mobile.getText().toString(), "",post_id, type, new File(imagePath), arrayList_gallery);
            }
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
                            uploadDialog(message);
                        } else {
                            Toast.makeText(EditPostActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EditPostActivity.this, getResources().getString(R.string.err_server_not_connected), Toast.LENGTH_LONG).show();
                    }
                }
            },requestBody);
            loadStatus.execute();
        } else {
            Toast.makeText(EditPostActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditPostActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(getString(R.string.upload_success));
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(getString(R.string.ok), (dialog, which) -> {

        });

        alertDialog.show();
    }

    @NonNull
    private Boolean validate() {
        if (et_title.getText().toString().trim().isEmpty()) {
            et_title.setError(getResources().getString(R.string.what_are_you_selling));
            et_title.requestFocus();
            return false;
        }else if (et_price.getText().toString().trim().isEmpty()) {
            et_price.setError(getResources().getString(R.string.enter_your_post_price));
            et_price.requestFocus();
            return false;
        } else if (et_user_mobile.getText().toString().isEmpty()) {
            et_user_mobile.setError(getResources().getString(R.string.err_phone));
            et_user_mobile.requestFocus();
            return false;
        } else if (cat_id.equals("")) {
            Toast.makeText(EditPostActivity.this, getResources().getString(R.string.select_1_cat), Toast.LENGTH_SHORT).show();
            return false;
        } else if (sub_cat_id.equals("")) {
            Toast.makeText(EditPostActivity.this, getResources().getString(R.string.select_1_sub_cat), Toast.LENGTH_SHORT).show();
            return false;
        } else if (districts_id.equals("")) {
            Toast.makeText(EditPostActivity.this, getResources().getString(R.string.select_1_districts), Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void showCameraDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_img, null);
        BottomSheetDialog dialog = new BottomSheetDialog(EditPostActivity.this);
        dialog.setContentView(view);
        LinearLayout ll_camera = dialog.findViewById(R.id.ll_camera);
        LinearLayout ll_image = dialog.findViewById(R.id.ll_image);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        Objects.requireNonNull(ll_camera).setOnClickListener(view1 -> {
            if (checkCam() && checkPer()) {
                openCamera();
            }
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_image).setOnClickListener(view1 -> {
            pickImage();
            dialog.dismiss();
        });
        Objects.requireNonNull(iv_close).setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }

    private void getData() {
        cat_id = itemPost.getCatId();
        sub_cat_id = itemPost.getSubCatId();
        districts_id = itemPost.getCityId();

        int cat_pos = 0;
        ArrayList<ItemCategory> arrayListCat = dbHelper.getCategory(null);
        if (!arrayListCat.isEmpty()){
            for (int i = 0; i < arrayListCat.size(); i++) {
                arrayList_cat.add(arrayListCat.get(i).getName());
                arrayList_catID.add(arrayListCat.get(i).getId());
                if (arrayListCat.get(i).getId().equals(cat_id)) {
                    cat_pos = i;
                }
            }
        }

        int districts_pos = 0;
        ArrayList<ItemCity> arrayListCity = dbHelper.getCity();
        if (!arrayListCity.isEmpty()){
            for (int i = 0; i < arrayListCity.size(); i++) {
                arrayList_districts.add(arrayListCity.get(i).getName());
                arrayList_districtsID.add(arrayListCity.get(i).getId());
                if (arrayListCity.get(i).getId().equals(districts_id)) {
                    districts_pos = i;
                }
            }
        }

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, R.layout.row_spinner, arrayListType);
        spinner_type.setAdapter(adapterType);

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, R.layout.row_spinner, arrayList_cat);
        spinner_category.setAdapter(adapterCat);
        spinner_category.setSelection(cat_pos);

        ArrayAdapter<String> adapterDistricts = new ArrayAdapter<>(this, R.layout.row_spinner, arrayList_districts);
        spinner_districts.setAdapter(adapterDistricts);
        spinner_districts.setSelection(districts_pos);

        int sub_pos = 0;
        ArrayList<ItemSubCategory> arrayListSubCat = dbHelper.getSubCategory(cat_id);
        if (!arrayListSubCat.isEmpty()){
            for (int i = 0; i < arrayListSubCat.size(); i++) {
                arrayList_sub_cat.add(arrayListSubCat.get(i).getName());
                arrayList_sub_catID.add(arrayListSubCat.get(i).getId());
                if (arrayListSubCat.get(i).getId().equals(sub_cat_id)) {
                    sub_pos = i;
                }
            }
        }

        ArrayAdapter<String> adapterSubCat = new ArrayAdapter<>(this, R.layout.row_spinner, arrayList_sub_cat);
        spinner_sub_category.setAdapter(adapterSubCat);
        spinner_sub_category.setSelection(sub_pos);

        setPostData();
    }

    private void setPostData() {
        et_title.setText(itemPost.getTitle());
        et_price.setText(itemPost.getMoney());
        et_description.setText(itemPost.getDescription());
        et_user_mobile.setText(itemPost.getPhone1());
        et_user_work_mobile.setText(itemPost.getPhone2());

        if (itemPost.getCondition().equals("Use")) {
            spinner_type.setSelection(0, true);
        } else {
            spinner_type.setSelection(1, true);
        }
        Picasso.get()
                .load(itemPost.getImage1())
                .placeholder(R.drawable.material_design_default)
                .into(iv_item_post);

        if (!arrayList_item_gallery.isEmpty()) {
            adapterGallery = new AdapterGalleryEdit(EditPostActivity.this, itemPost.getId(), arrayList_item_gallery, arrayList_gallery);
            rv_upload_gallery.setAdapter(adapterGallery);

            iv_upload_gallery.setVisibility(View.INVISIBLE);
        }
    }

    private void getSubCategory() {
        if (!cat_id.isEmpty()){
            if (!arrayList_sub_cat.isEmpty()){
                arrayList_sub_cat.clear();
            }
            if (!arrayList_sub_catID.isEmpty()){
                arrayList_sub_catID.clear();
            }
            ArrayList<ItemSubCategory> arrayListSubCat = dbHelper.getSubCategory(cat_id);
            if (!arrayListSubCat.isEmpty()){
                for (int i = 0; i < arrayListSubCat.size(); i++) {
                    arrayList_sub_cat.add(arrayListSubCat.get(i).getName());
                    arrayList_sub_catID.add(arrayListSubCat.get(i).getId());
                }
                if (!arrayList_sub_catID.isEmpty()) {
                    sub_cat_id = arrayList_sub_catID.get(0);
                }
            }

            ArrayAdapter<String> adapterSubCat = new ArrayAdapter<>(this, R.layout.row_spinner, arrayList_sub_cat);
            spinner_sub_category.setAdapter(adapterSubCat);
        }
    }

    private void openCamera() {
        String iconsStoragePath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM + File.separator + getString(R.string.app_name);
        File sdIconStorageDir = new File(iconsStoragePath);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String frame = "temp-" + n;
        String file_name = frame + ".jpg";

        File sdStorage = new File(iconsStoragePath, file_name);
        mImageCaptureUri = Uri.fromFile(sdStorage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_CAMERA_REQUEST);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void pickMultipleImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_MULTIPLE_IMAGE_REQUEST);
    }

    public Boolean checkPer() {
        if (Build.VERSION.SDK_INT >= 33){
            if ((ContextCompat.checkSelfPermission(EditPostActivity.this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_MEDIA_IMAGES}, 101);
                return false;
            } else {
                return true;
            }
        } else if (Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(EditPostActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 101);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(EditPostActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 101);
                return false;
            }
            return true;
        }
    }

    public Boolean checkCam() {
        if (Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(EditPostActivity.this, CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{CAMERA}, 102);
                return false;
            } else {
                return true;
            }
        } else {
            if ( (ContextCompat.checkSelfPermission(EditPostActivity.this, CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{CAMERA}, 102);
                }
                return false;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean canUseExternalStorage = false;
        boolean canUseCamera = false;
        if (requestCode == 101 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canUseExternalStorage = true;
            }
            if (!canUseExternalStorage) {
                Toast.makeText(EditPostActivity.this, getResources().getString(R.string.err_cannot_use_features), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 102 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canUseCamera = true;
            }
            if (!canUseCamera) {
                Toast.makeText(EditPostActivity.this, getResources().getString(R.string.err_cannot_use_features), Toast.LENGTH_SHORT).show();
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
                Bitmap bitmap_upload = MediaStore.Images.Media.getBitmap(EditPostActivity.this.getContentResolver(), uri);
                iv_item_post.setImageBitmap(bitmap_upload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(requestCode == PICK_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            imagePath = helper.getPathImage(mImageCaptureUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditPostActivity.this.getContentResolver(), mImageCaptureUri);
                iv_item_post.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null && data.getClipData().getItemCount() > 0) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    arrayList_gallery.add(new File(helper.getPathImage(data.getClipData().getItemAt(i).getUri())));
                    arrayList_item_gallery.add(new ItemGallery(new File(helper.getPathImage(data.getClipData().getItemAt(i).getUri()))));
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                String path = helper.getPathImage(uri);
                arrayList_gallery.add(new File(path));
                arrayList_item_gallery.add(new ItemGallery(new File(path)));
            }

            if (adapterGallery == null) {
                adapterGallery = new AdapterGalleryEdit(EditPostActivity.this, itemPost.getId(), arrayList_item_gallery, arrayList_gallery);
                rv_upload_gallery.setAdapter(adapterGallery);
            } else {
                adapterGallery.notifyDataSetChanged();
            }
            iv_upload_gallery.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_add_post;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sold_out) {
            showSoldOutDialog();
            return true;
        } else if (id == R.id.action_trash) {
            showTrashDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTrashDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditPostActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are you sure you want to delete this Post?");
        alertDialog.setNegativeButton(getString(R.string.yes), (dialog, which) -> loadTrash());
        alertDialog.setPositiveButton(getString(R.string.no), (dialogInterface, i) -> {});
        alertDialog.show();
    }

    private void showSoldOutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditPostActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle("Sold Out");
        if (itemPost.getSoldOut().equals("0")){
            alertDialog.setMessage("Is this product back to store?");
        } else {
            alertDialog.setMessage("Are you sure you want to sold out this Post?");
        }
        alertDialog.setNegativeButton(getString(R.string.yes), (dialog, which) -> loadSoldOut());
        alertDialog.setPositiveButton(getString(R.string.no), (dialogInterface, i) -> {});
        alertDialog.show();
    }

    private void loadSoldOut() {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        successDialog(message);
                    } else {
                        Toast.makeText(EditPostActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_DO_SOLD_OUT, 0, itemPost.getId(), "", "", "", "", "", "", "", "", "", "", "", null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(EditPostActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTrash() {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        Callback.isProfileUpdate = true;
                        successDialog(message);
                    } else {
                        Toast.makeText(EditPostActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_DO_TRASH, 0, itemPost.getId(), "", "", "", "", "", "", "", "", "", "", "", null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(EditPostActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void successDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditPostActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle("Success");
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(getString(R.string.ok), (dialog, which) -> finish());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}