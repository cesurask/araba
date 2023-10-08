package nemosofts.classified.adapter.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.activity.PostDetailsActivity;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.InterAdListener;
import nemosofts.classified.item.ItemSpotlight;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.helper.Helper;

public class HomePagerAdapter extends EnchantedViewPagerAdapter {

    private Context mContext;
    private final LayoutInflater inflater;
    private List<ItemSpotlight> arrayList;
    private final Helper helper;

    public HomePagerAdapter(Context context, List<ItemSpotlight> arrayList) {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.arrayList = arrayList;
        helper = new Helper(context, interAdListener);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.row_home_spotlight, container, false);

        final ItemSpotlight itemPost = arrayList.get(position);

        TextView tv_title = mCurrentView.findViewById(R.id.tv_spotlight_text);
        TextView tv_city = mCurrentView.findViewById(R.id.tv_spotlight_city);
        TextView tv_price_date = mCurrentView.findViewById(R.id.tv_spotlight_price_date);
        TextView tv_price = mCurrentView.findViewById(R.id.tv_spotlight_price);

        tv_title.setText(itemPost.getTitle());
        tv_city.setText(itemPost.getCityName());
        tv_price_date.setText(itemPost.getDateTime());
        if (Boolean.TRUE.equals(Callback.currency_position)){
            tv_price.setText(Callback.currency_code+" "+ ApplicationUtil.formatCurrency(itemPost.getMoney()));
        } else {
            tv_price.setText(ApplicationUtil.formatCurrency(itemPost.getMoney())+" "+Callback.currency_code);
        }

        LinearLayout ll_st_tl_3 = mCurrentView.findViewById(R.id.ll_spotlight_thumbnail_3);
        ImageView iv_thumb_1 = mCurrentView.findViewById(R.id.iv_spotlight_thumbnail_1);
        ImageView iv_thumb_2 = mCurrentView.findViewById(R.id.iv_spotlight_thumbnail_2);
        ImageView iv_thumb_3 = mCurrentView.findViewById(R.id.iv_spotlight_thumbnail_3);
        ImageView iv_thumb_full = mCurrentView.findViewById(R.id.iv_spotlight_thumbnail_full_2);

        if (itemPost.getImage3().isEmpty() || itemPost.getImage2().isEmpty()){
            Picasso.get()
                    .load(itemPost.getImage1())
                    .centerCrop()
                    .resize(450,350)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_thumb_full);
            ll_st_tl_3.setVisibility(View.GONE);
            iv_thumb_full.setVisibility(View.VISIBLE);
        } else {
            Picasso.get()
                    .load(itemPost.getImage1())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_thumb_1);
            ll_st_tl_3.setVisibility(View.VISIBLE);
            iv_thumb_full.setVisibility(View.GONE);
        }

        if (!itemPost.getImage2().isEmpty()){
            Picasso.get()
                    .load(itemPost.getImage2())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_thumb_2);
        }

        if (!itemPost.getImage3().isEmpty()){
            Picasso.get()
                    .load(itemPost.getImage3())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_thumb_3);
        }

        mCurrentView.findViewById(R.id.rl_spotlight_click).setOnClickListener(v -> helper.showInterAd(position, ""));
        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);
        return mCurrentView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    InterAdListener interAdListener = (position, type) -> {
        Intent intent = new Intent(mContext, PostDetailsActivity.class);
        intent.putExtra("post_id", arrayList.get(position).getId());
        intent.putExtra("user_id", arrayList.get(position).getUserId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    };
}