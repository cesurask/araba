package nemosofts.classified.adapter.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemGallery;

public class GalleryPostDetailsAdapterFull extends EnchantedViewPagerAdapter {

    private final LayoutInflater inflater;
    private final ArrayList<ItemGallery> arrayList;

    public GalleryPostDetailsAdapterFull(Context ctx, ArrayList<ItemGallery> arrayList) {
        super(arrayList);
        inflater = LayoutInflater.from(ctx);
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.row_gallery_full, container, false);

        ProgressBar loading = mCurrentView.findViewById(R.id.loading);
        ImageView iv_banner = mCurrentView.findViewById(R.id.iv_home_banner);

        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.material_design_default)
                .into(iv_banner, new Callback() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        loading.setVisibility(View.GONE);
                    }
                });

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

}