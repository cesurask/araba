package nemosofts.classified.activity.payment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.BuildConfig;
import nemosofts.classified.R;
import nemosofts.classified.activity.WebActivity;
import nemosofts.classified.adapter.AdapterPromotions;
import nemosofts.classified.asyncTask.LoadPromotion;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.ifSupported.IsRTL;
import nemosofts.classified.ifSupported.IsScreenshot;
import nemosofts.classified.interfaces.PromotionListener;
import nemosofts.classified.item.ItemProActive;
import nemosofts.classified.item.ItemPromotions;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;

public class PromotionsActivity extends AppCompatActivity {

    private Helper helper;
    private ProgressBar progressBar;
    private RecyclerView rv;
    private ArrayList<ItemPromotions> arrayList;
    private ArrayList<ItemProActive> itemActive;
    private String error_msg;
    private FrameLayout frameLayout;
    private String id="", name="", money="", time="", image="", cat_name="";
    private ImageView post_img;
    private TextView post_text, post_city, post_pri, post_date;

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

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        money = getIntent().getStringExtra("money");
        time = getIntent().getStringExtra("time");
        image = getIntent().getStringExtra("image");
        cat_name = getIntent().getStringExtra("cat_name");

        itemActive = new ArrayList<>();
        arrayList = new ArrayList<>();

        post_img = findViewById(R.id.iv_promotion_ads);
        post_text = findViewById(R.id.tv_promotion_ads);
        post_city = findViewById(R.id.tv_promotion_ads_city);
        post_pri = findViewById(R.id.tv_promotion_ads_pri);
        post_date = findViewById(R.id.tv_promotion_ads_date);

        progressBar = findViewById(R.id.pb);
        rv = findViewById(R.id.rv_promote);
        frameLayout = findViewById(R.id.fl_empty);

        LinearLayoutManager llm = new LinearLayoutManager(PromotionsActivity.this);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        findViewById(R.id.tv_btn_continue).setOnClickListener(v -> {
            if(new SharedPref(this).isLogged()) {
                if (arrayList.get(0).isExpandable() || arrayList.get(1).isExpandable() || arrayList.get(2).isExpandable()){
                    Intent intent = new Intent(PromotionsActivity.this, SelectPlanActivity.class);
                    intent.putExtra("planName", arrayList.get(0).getPlanName());
                    intent.putExtra("planPrice", arrayList.get(0).isPrice());

                    intent.putExtra("planDuration", arrayList.get(0).getPlanDuration());
                    intent.putExtra("planPriceDuration", arrayList.get(0).getPlanDuration());
                    intent.putExtra("planDays", arrayList.get(0).isDay());
                    intent.putExtra("isExpandable", arrayList.get(0).isExpandable());

                    intent.putExtra("planName2", arrayList.get(1).getPlanName());
                    intent.putExtra("planPrice2", arrayList.get(1).isPrice());
                    intent.putExtra("planDuration2", arrayList.get(1).getPlanDuration());
                    intent.putExtra("planPriceDuration2", arrayList.get(1).getPlanDuration());
                    intent.putExtra("planDays2", arrayList.get(1).isDay());
                    intent.putExtra("isExpandable2", arrayList.get(1).isExpandable());

                    intent.putExtra("planName3", arrayList.get(2).getPlanName());
                    intent.putExtra("planPrice3", arrayList.get(2).isPrice());
                    intent.putExtra("planDuration3", arrayList.get(2).getPlanDuration());
                    intent.putExtra("planPriceDuration3", arrayList.get(2).getPlanDuration());
                    intent.putExtra("planDays3", arrayList.get(2).isDay());
                    intent.putExtra("isExpandable3", arrayList.get(2).isExpandable());

                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("money", money);
                    intent.putExtra("time", time);
                    intent.putExtra("image", image);
                    intent.putExtra("cat_name", cat_name);

                    startActivity(intent);
                } else {
                    Toast.makeText(PromotionsActivity.this, getString(R.string.select_one_option), Toast.LENGTH_SHORT).show();
                }
            } else {
                helper.clickLogin();
            }
        });

        findViewById(R.id.tv_terms).setOnClickListener(v -> {
            Intent intent = new Intent(PromotionsActivity.this, WebActivity.class);
            intent.putExtra("web_url", BuildConfig.BASE_URL+"terms.php");
            intent.putExtra("page_title", getResources().getString(R.string.terms_and_conditions));
            ActivityCompat.startActivity(PromotionsActivity.this, intent, null);
        });

        loadData();
        getPostData();
    }

    private void loadData() {
        if (helper.isNetworkAvailable()) {
            LoadPromotion loadAbout = new LoadPromotion(new PromotionListener() {
                @Override public void onStart() {

                }

                @Override
                public void onEnd(String success, ArrayList<ItemPromotions> promotionsArrayList, ArrayList<ItemProActive> activeArrayList) {
                    if (success.equals("1")) {
                        if (promotionsArrayList.isEmpty()) {
                            error_msg = getString(R.string.err_no_data_found);
                            setEmpty();
                        }else {
                            itemActive.addAll(activeArrayList);
                            arrayList.addAll(promotionsArrayList);
                            setAdapter();
                        }
                    }else {
                        error_msg = getString(R.string.err_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.getAPIRequest(Callback.METHOD_PRO_TEST, 0, id, "", "", "", "", "", "", "", "", "", "", "",null, null));
            loadAbout.execute();
        }else {
            error_msg = getString(R.string.err_internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapter() {
        ItemProActive active = itemActive.get(0);
        AdapterPromotions adapter = new AdapterPromotions(Callback.currency_code_promotions,active.isDailyBumpUp(), active.isTopAd(), active.isSpotLight(), arrayList);
        rv.setAdapter(adapter);
        rv.scheduleLayoutAnimation();
        setEmpty();
    }

    @SuppressLint("SetTextI18n")
    private void getPostData() {
        Picasso.get()
                .load(image)
                .centerCrop()
                .resize(300,300)
                .placeholder(R.color.load_color_4)
                .into(post_img);

        post_text.setText(name);
        post_city.setText(cat_name);
        post_pri.setText(Callback.currency_code+" "+ ApplicationUtil.formatCurrency(money));
        post_date.setText(time);
    }

    @SuppressLint("InflateParams")
    private void setEmpty() {
        if (!arrayList.isEmpty()) {
            rv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.ll_empty_try).setOnClickListener(v -> loadData());

            frameLayout.addView(myView);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_promotions;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}