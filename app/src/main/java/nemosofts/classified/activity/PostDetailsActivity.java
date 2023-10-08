package nemosofts.classified.activity;

import static android.Manifest.permission.CALL_PHONE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.activity.chat.ChatPrivateActivity;
import nemosofts.classified.activity.chat.CommentActivity;
import nemosofts.classified.activity.payment.PromotionsActivity;
import nemosofts.classified.adapter.gallery.GalleryPostDetailsAdapter;
import nemosofts.classified.adapter.home.AdapterPostHome;
import nemosofts.classified.adapter.home.AdapterSimilar;
import nemosofts.classified.asyncTask.LoadDetailsPost;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.dialog.BlockShopDialog;
import nemosofts.classified.dialog.FeedBackDialog;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.DetailsListener;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.item.ItemGallery;
import nemosofts.classified.item.ItemPostDatabase;
import nemosofts.classified.item.ItemUser;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.DBHelper;
import nemosofts.classified.utils.helper.Helper;

public class PostDetailsActivity extends AppCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private String post_id="", user_id="";
    private NestedScrollView ns_view;
    private LinearLayout ll_bottom;
    private ItemPostDatabase itemPost;
    private FrameLayout frameLayout;
    private String error_msg;
    private ProgressBar pb;
    private EnchantedViewPager enchantedViewPager;
    private TextView tv_title, tv_price, tv_use, tv_city,tv_category, tv_view, tv_share, tv_date;
    private WebView mWebView;
    private RecyclerView rv_similar;
    private ArrayList<ItemData> arrayListPost;
    private ItemUser itemUserPost;
    private Boolean isExpandable = false;
    private ImageView iv_fav;
    private LinearLayout ll_member;
    private TextView tv_member;
    private RelativeLayout rl_safety;
    private RelativeLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private Boolean is_block_shop = false;

    @SuppressLint("SetJavaScriptEnabled")
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
        setTitle("");

        helper = new Helper(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        post_id = getIntent().getStringExtra("post_id");
        user_id = getIntent().getStringExtra("user_id");

        progressDialog = new ProgressDialog(this, R.style.ThemeDialog);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        pb = findViewById(R.id.pb);
        ns_view = findViewById(R.id.ns_view);
        ll_bottom = findViewById(R.id.ll_bottom);
        frameLayout = findViewById(R.id.fl_empty);

        tv_title = findViewById(R.id.tv_details_title);
        tv_price = findViewById(R.id.tv_details_price);
        tv_use = findViewById(R.id.tv_details_use);
        tv_city = findViewById(R.id.tv_details_city);
        tv_category = findViewById(R.id.tv_details_category);
        tv_view = findViewById(R.id.tv_details_view);
        tv_share = findViewById(R.id.tv_details_share);
        tv_date = findViewById(R.id.tv_details_date);
        mWebView = findViewById(R.id.webView_det);
        iv_fav = findViewById(R.id.iv_fav);
        ll_member = findViewById(R.id.ll_member);
        tv_member = findViewById(R.id.tv_member);
        rl_safety = findViewById(R.id.rl_safety);
        coordinatorLayout = findViewById(R.id.rl);

        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.getSettings().setJavaScriptEnabled(true);

        enchantedViewPager = findViewById(R.id.view_pager);
        enchantedViewPager.useAlpha();
        enchantedViewPager.removeScale();
        enchantedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (Callback.arrayList_banner != null){
                    TextView tv_view_pager = findViewById(R.id.tv_view_pager);
                    tv_view_pager.setText(position+1 + "/" +Callback.arrayList_banner.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        arrayListPost = new ArrayList<>();
        rv_similar = findViewById(R.id.rv_similar);

        findViewById(R.id.rl_call).setOnClickListener(v -> {
            if (checkPer()) {
                showBottomSheetDialog();
            }
        });
        findViewById(R.id.rl_chat).setOnClickListener(v -> {
            if (sharedPref.isLogged()){
                showBottomChatDialog();
            } else {
                helper.clickLogin();
            }
        });
        findViewById(R.id.ll_report_detail).setOnClickListener(v -> {
            if (validate()){
                new FeedBackDialog(this).showDialog(itemPost.getId(),itemPost.getTitle());
            }
        });
        findViewById(R.id.ll_report_user).setOnClickListener(v -> {
            if (validate()){
                showBlockStoreDialog();
            }
        });

        findViewById(R.id.ll_report_promote).setOnClickListener(v -> {
            if (validate()){
                Intent intent = new Intent(PostDetailsActivity.this, PromotionsActivity.class);
                intent.putExtra("id", itemPost.getId());
                intent.putExtra("name", itemPost.getTitle());
                intent.putExtra("money", itemPost.getMoney());
                intent.putExtra("time", itemPost.getDateTime());
                intent.putExtra("image", itemPost.getImage1());
                intent.putExtra("cat_name", itemPost.getCatName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(PostDetailsActivity.this, intent, null);
            }
        });
        findViewById(R.id.tv_visit_store).setOnClickListener(v -> {
            if (itemUserPost != null){
                Intent intent = new Intent(PostDetailsActivity.this, VisitStoreActivity.class);
                intent.putExtra("user_id", itemUserPost.getId());
                intent.putExtra("name", itemUserPost.getName());
                intent.putExtra("email", itemUserPost.getEmail());
                intent.putExtra("mobile", itemUserPost.getMobile());
                intent.putExtra("profile_images", itemUserPost.getProfileImages());
                intent.putExtra("registered_on", itemUserPost.getRegisteredOn());
                intent.putExtra("member", itemUserPost.getMember());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(PostDetailsActivity.this, intent, null);
            }
        });

        ImageView arrow_faq = findViewById(R.id.arrow_faq);
        rl_safety.setOnClickListener(view -> {
            isExpandable = !isExpandable;
            findViewById(R.id.layout_notes).setVisibility(Boolean.TRUE.equals(isExpandable) ? View.VISIBLE : View.GONE);
            arrow_faq.setImageResource(Boolean.TRUE.equals(isExpandable) ? R.drawable.ic_up_arrow : R.drawable.ic_down_arrow);
        });

        iv_fav.setOnClickListener(view -> {
            if (sharedPref.isLogged()) {
                if (validate()){
                    loadFav();
                }
            } else {
                helper.clickLogin();
            }
        });

        LoadDetails();

        ImageView iv_similar = findViewById(R.id.iv_similar);
        iv_similar.setImageResource(Boolean.TRUE.equals(sharedPref.getGridSimilar()) ? R.drawable.ic_round_list : R.drawable.ic_grid_view);
        iv_similar.setOnClickListener(v -> {
            sharedPref.setGridSimilar(!sharedPref.getGridSimilar());
            iv_similar.setImageResource(Boolean.TRUE.equals(sharedPref.getGridSimilar()) ? R.drawable.ic_round_list : R.drawable.ic_grid_view);
            setSimilarAdapter();
        });

        LinearLayout ll_adView_1 = findViewById(R.id.ll_adView_1);
        helper.showBannerAd(ll_adView_1);

        LinearLayout ll_adView_2 = findViewById(R.id.ll_adView_2);
        helper.showBannerAd(ll_adView_2);

        findViewById(R.id.iv_text_fields).setOnClickListener(v -> {
            if (itemPost != null){
                setTextSize();
            }
        });

        findViewById(R.id.iv_share).setOnClickListener(v -> {
            if (itemPost != null){
                setShare();
            }
        });
    }

    @NonNull
    private Boolean validate() {
        if (Boolean.FALSE.equals(itemPost.isActive())) {
            showSnackBar(false, "Wait until admin approve it");
            return false;
        } else {
            return true;
        }
    }

    private void setSimilarAdapter() {
        if (!arrayListPost.isEmpty()) {
            findViewById(R.id.ll_similar).setVisibility(View.VISIBLE);
            if (Boolean.TRUE.equals(sharedPref.getGridSimilar())){
                GridLayoutManager grid_fresh = new GridLayoutManager(this, 2);
                grid_fresh.setSpanCount(2);
                rv_similar.setLayoutManager(grid_fresh);
                rv_similar.setItemAnimator(new DefaultItemAnimator());
                rv_similar.setHasFixedSize(true);
                AdapterPostHome adapterPostHome = new AdapterPostHome(this, arrayListPost, (itemData, position) -> {
                    post_id = arrayListPost.get(position).getId();
                    user_id = arrayListPost.get(position).getUserId();
                    LoadDetails();
                });
                rv_similar.setAdapter(adapterPostHome);
            } else {
                AdapterSimilar adapter = new AdapterSimilar(this,arrayListPost, (itemPost, position) -> {
                    post_id = arrayListPost.get(position).getId();
                    user_id = arrayListPost.get(position).getUserId();
                    LoadDetails();
                });
                LinearLayoutManager llm = new LinearLayoutManager(PostDetailsActivity.this);
                rv_similar.setLayoutManager(llm);
                rv_similar.setItemAnimator(new DefaultItemAnimator());
                rv_similar.setHasFixedSize(true);
                rv_similar.setAdapter(adapter);
            }
        } else {
            findViewById(R.id.ll_similar).setVisibility(View.GONE);
        }
    }

    private void LoadDetails() {
        if (helper.isNetworkAvailable()) {
            LoadDetailsPost detailsPost = new LoadDetailsPost(new DetailsListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                    ns_view.setVisibility(View.GONE);
                    ll_bottom.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemPostDatabase> postArrayList, ArrayList<ItemData> dataArrayList, ArrayList<ItemUser> userArrayList, ArrayList<ItemGallery> arrayListGallery, Boolean isBlockShop) {
                    if (success.equals("1")) {
                        if (!postArrayList.isEmpty()){
                            itemPost = postArrayList.get(0);

                            is_block_shop = isBlockShop;

                            if (!Callback.arrayList_banner.isEmpty()) {
                                Callback.arrayList_banner.clear();
                            }
                            if (arrayListGallery.isEmpty()){
                                Callback.arrayList_banner.add(new ItemGallery("1",postArrayList.get(0).getImage1()));
                            } else {
                                Callback.arrayList_banner.addAll(arrayListGallery);
                            }

                            if (!arrayListPost.isEmpty()){
                                arrayListPost.clear();
                            }
                            arrayListPost.addAll(dataArrayList);

                            if (!userArrayList.isEmpty()){
                                itemUserPost = userArrayList.get(0);
                            }

                            displayData();
                        } else {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        }
                    } else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_DETAILS, 0, post_id, user_id, "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            detailsPost.execute();
        } else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayData() {

        if (Boolean.TRUE.equals(is_block_shop)){
            new BlockShopDialog(this).showDialog(itemPost.getUserId());
        }

        ns_view.fullScroll(View.FOCUS_UP);

        setFavorite(itemPost.isFav());

        tv_title.setText(itemPost.getTitle());
        if (Boolean.TRUE.equals(Callback.currency_position)){
            tv_price.setText(Callback.currency_code+" "+ApplicationUtil.formatCurrency(itemPost.getMoney()));
        } else {
            tv_price.setText(ApplicationUtil.formatCurrency(itemPost.getMoney())+" "+Callback.currency_code);
        }

        if (itemPost.getSoldOut().equals("0")){
            findViewById(R.id.tv_sold_out).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tv_sold_out).setVisibility(View.GONE);
        }

        tv_use.setText(itemPost.getCondition());
        tv_city.setText(itemPost.getCityName());
        tv_category.setText(itemPost.getCatName());
        tv_view.setText(ApplicationUtil.format(Integer.valueOf(itemPost.getTotalViews())));
        tv_share.setText(ApplicationUtil.format(Integer.valueOf(itemPost.getTotalShare())));
        tv_date.setText("Posted on: "+itemPost.getDateTime());

        String htmlText = itemPost.getDescription();

        String textSize;
        int textData = sharedPref.getTextSize();
        if (0 == textData){
            textSize = "body{font-size:12px;}";
        } else if (1 == textData){
            textSize = "body{font-size:14px;}";
        } else if (2 == textData){
            textSize = "body{font-size:16px;}";
        } else if (3 == textData){
            textSize = "body{font-size:17px;}";
        } else if (4 == textData){
            textSize = "body{font-size:20px;}";
        } else {
            textSize = "body{font-size:14px;}";
        }

        String myCustomStyleString;
        if (Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode())) {
            myCustomStyleString = "<style channelType=\"text/css\">body,* {color:#DBDBDB; font-family: MyFont;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>"
                    + "<style type=\"text/css\">"+ textSize + "</style>";
        } else {
            myCustomStyleString = "<style channelType=\"text/css\">body,* {color:#161616; font-family: MyFont; text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>"
                    + "<style type=\"text/css\">"+ textSize + "</style>";
        }

        String htmlString;
        if(Boolean.FALSE.equals(Callback.isRTL)) {
            htmlString = myCustomStyleString + "<div>" + htmlText + "</div>";
        } else {
            htmlString = "<html dir=\"rtl\" lang=\"\"><body>" + myCustomStyleString + "<div>" + htmlText + "</div>" + "</body></html>";
        }

        mWebView.loadDataWithBaseURL("", htmlString, "text/html", "utf-8", null);

        if (itemUserPost == null){
            itemUserPost = new ItemUser("0",getString(R.string.app_name),"","","","","","","0","0","");
        }

        TextView tv_post_user = findViewById(R.id.tv_post_user);
        tv_post_user.setText(itemUserPost.getName());

        ImageView seller_img = findViewById(R.id.seller_img);
        if (!itemUserPost.getProfileImages().isEmpty()){
            Picasso.get()
                    .load(itemUserPost.getProfileImages())
                    .placeholder(R.drawable.ic_user_flat)
                    .into(seller_img);
        }

        if (itemUserPost.getMember().equals("1")){
            tv_member.setText("Member since "+itemUserPost.getRegisteredOn());
            ll_member.setVisibility(View.VISIBLE);
            rl_safety.setVisibility(View.GONE);
        } else {
            ll_member.setVisibility(View.GONE);
            rl_safety.setVisibility(View.VISIBLE);
        }
        setAdapter();
    }

    private void setAdapter() {
        if (!Callback.arrayList_banner.isEmpty()) {
            GalleryPostDetailsAdapter pagerAdapter = new GalleryPostDetailsAdapter(PostDetailsActivity.this, Callback.arrayList_banner);
            enchantedViewPager.setAdapter(pagerAdapter);
        }
        setSimilarAdapter();
        setEmpty();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setFavorite(@NonNull Boolean isFav) {
        if (Boolean.TRUE.equals(isFav)) {
            iv_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_turned_in));
        } else {
            iv_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_turned_in_not));
        }
    }

    private void setTextSize() {
        final String[] listItems = {"Extra Small", "Small", "Medium", "Large", "Extra Large"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailsActivity.this, R.style.ThemeDialog);
        builder.setTitle("Select Font Size");
        builder.setSingleChoiceItems(listItems, sharedPref.getTextSize(), (dialog, which) -> sharedPref.setTextSize(which));
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            displayData();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void setShare() {
        if (helper.isNetworkAvailable()) {
            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        ApplicationUtil.responsePost(Callback.API_URL, helper.getAPIRequest(Callback.METHOD_SHARE,0, itemPost.getId(),"","","","","","","","", "","", "", null, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, itemPost.getTitle() + " - " + getString(R.string.get_more) + "\n" + getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }

    private void setEmpty() {
        if (itemPost != null) {
            pb.setVisibility(View.GONE);
            ns_view.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.GONE);
            ns_view.setVisibility(View.INVISIBLE);
            ll_bottom.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> LoadDetails());

            frameLayout.addView(myView);
        }
    }

    private void showBlockStoreDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_block_store);
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_do).setOnClickListener(view -> {
            addToBlockUser(itemUserPost.getId());
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void addToBlockUser(String shop_id) {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String reportSuccess, String message) {
                    if (success.equals("1")) {
                        if (reportSuccess.equals("1")) {
                            finish();
                            Toast.makeText(PostDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PostDetailsActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_SHOP_BY_BLOCKED, 0, shop_id, "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
            loadFav.execute();
        } else {
            Toast.makeText(PostDetailsActivity.this, getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheetDialog() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_call, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        TextView tv_call_1 = dialog.findViewById(R.id.tv_call_1);
        TextView tv_call_2 = dialog.findViewById(R.id.tv_call_2);

        if (!itemPost.getPhone1().equals("")){
            tv_call_1.setText(itemPost.getPhone1());
        } else {
            dialog.findViewById(R.id.ll_call_1).setVisibility(View.GONE);
        }

        if (!itemPost.getPhone2().equals("")){
            tv_call_2.setText(itemPost.getPhone2());
        } else {
            dialog.findViewById(R.id.ll_call_2).setVisibility(View.GONE);
        }

        dialog.findViewById(R.id.ll_call_1).setOnClickListener(view1 -> {
            if (validate()){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+itemPost.getPhone1()));
                startActivity(callIntent);
            }
            dialog.dismiss();
        });

        dialog.findViewById(R.id.ll_call_2).setOnClickListener(view1 -> {
            if (validate()){
                Intent callIntent2 = new Intent(Intent.ACTION_CALL);
                callIntent2.setData(Uri.parse("tel:"+itemPost.getPhone2()));
                startActivity(callIntent2);
            }
            dialog.dismiss();
        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    private void showBottomChatDialog() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_chat, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        LinearLayout ll_chat = dialog.findViewById(R.id.ll_chat);
        assert ll_chat != null;
        if (itemPost.getUserId().equals(sharedPref.getUserId())){
            ll_chat.setVisibility(View.GONE);
        } else {
            ll_chat.setOnClickListener(view1 -> {
                if (validate()){
                    Intent intentComment = new Intent(PostDetailsActivity.this, ChatPrivateActivity.class);
                    intentComment.putExtra("post_id", itemPost.getId());
                    intentComment.putExtra("post_user_id", itemPost.getUserId());
                    intentComment.putExtra("chat_user_id", sharedPref.getUserId());
                    intentComment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityCompat.startActivity(PostDetailsActivity.this, intentComment, null);
                }
                dialog.dismiss();
            });
        }

        dialog.findViewById(R.id.ll_comment).setOnClickListener(view1 -> {
            if (validate()){
                Intent intentComment = new Intent(PostDetailsActivity.this, CommentActivity.class);
                intentComment.putExtra("post_id", itemPost.getId());
                intentComment.putExtra("user_id", itemPost.getUserId());
                intentComment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(PostDetailsActivity.this, intentComment, null);
            }
            dialog.dismiss();
        });
        dialog.findViewById(R.id.iv_close_chat).setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }

    private void loadFav() {
        if (sharedPref.isLogged()) {
            if (helper.isNetworkAvailable()) {
                LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                    @Override
                    public void onStart() {
                        setFavorite(!itemPost.isFav());
                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            itemPost.setFav(message.equals("Added to Favourite"));
                            setFavorite(itemPost.isFav());
                            showSnackBar(true, message);
                        } else {
                            showSnackBar(false, getString(R.string.err_server_not_connected));
                        }
                    }
                }, helper.getAPIRequest(Callback.METHOD_DO_FAV, 0, itemPost.getId(), "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
                loadFav.execute();
            } else {
                showSnackBar(false, getString(R.string.err_internet_not_connected));
            }
        } else {
            helper.clickLogin();
        }
    }

    public void showSnackBar(boolean success, String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(success ? R.drawable.bg_snack_bar_success : R.drawable.bg_snack_bar);
        snackbar.show();
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_post_details;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }

    @NonNull
    private Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(PostDetailsActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            requestPermissions(new String[]{CALL_PHONE}, 102);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 102) {
            boolean canUseCall = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!canUseCall) {
                showSnackBar(false, getResources().getString(R.string.err_cannot_use_features));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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