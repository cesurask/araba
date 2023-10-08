package nemosofts.classified.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.AppCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import nemosofts.classified.R;
import nemosofts.classified.adapter.gallery.GalleryPostDetailsAdapterFull;
import nemosofts.classified.callback.Callback;

public class WallpaperDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setTitle("");

        Intent intent = getIntent();
        int pos = intent.getIntExtra("pos",0);

        EnchantedViewPager pager = findViewById(R.id.view_pager_wall);
        pager.useAlpha();
        pager.removeScale();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                if (Callback.arrayList_banner != null){
                    TextView tv_view_pager = findViewById(R.id.tv_view_pager_wall);
                    tv_view_pager.setText(position+1 + "/" + Callback.arrayList_banner.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        GalleryPostDetailsAdapterFull pagerAdapter = new GalleryPostDetailsAdapterFull(WallpaperDetailsActivity.this, Callback.arrayList_banner);
        pager.setAdapter(pagerAdapter);
        if (pos > 1) {
            pager.setCurrentItem(pos);
        }
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.activity_wallpaper_details;
    }

    @Override
    public int setAppCompat() {
        return AppCompat.COMPAT();
    }
}