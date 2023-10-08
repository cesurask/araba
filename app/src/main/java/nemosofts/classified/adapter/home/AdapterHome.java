package nemosofts.classified.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.activity.AllPostActivity;
import nemosofts.classified.activity.CategoryActivity;
import nemosofts.classified.activity.PostDetailsActivity;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.InterAdListener;
import nemosofts.classified.item.ItemPostHome;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;
import nemosofts.classified.utils.recycler.RecyclerItemClickListener;

public class AdapterHome extends RecyclerView.Adapter {

    Context context;
    Helper helper;
    SharedPref sharedPref;
    List<ItemPostHome> arrayList;
    int clickPos = 0;

    final int VIEW_PROG = 0;
    final int VIEW_SPOTLIGHT = 1;
    final int VIEW_CATEGORIES = 2;
    final int VIEW_LATEST = 3;
    final int VIEW_GRID = 4;
    final int VIEW_HORIZONTAL = 5;
    final int VIEW_VERTICAL = 6;

    public AdapterHome(Context context, List<ItemPostHome> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        helper = new Helper(context, interAdListener);
        sharedPref = new SharedPref(context);
    }

    static class SpotlightHolder extends RecyclerView.ViewHolder {

        EnchantedViewPager enchantedViewPager;
        HomePagerAdapter homePagerAdapter;

        SpotlightHolder(View view) {
            super(view);
            enchantedViewPager = view.findViewById(R.id.viewPager_home);
            enchantedViewPager.useAlpha();
            enchantedViewPager.removeScale();
        }
    }

    class CategoriesHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_cat;
        AdapterHomeCategories adapterHomeCategories;
        TextView tv_title;
        LinearLayout ll_home_view_all;

        CategoriesHolder(View view) {
            super(view);
            rv_cat = view.findViewById(R.id.rv_home_cat);
            tv_title = view.findViewById(R.id.tv_home_title);
            ll_home_view_all = view.findViewById(R.id.ll_home_view_all);
            GridLayoutManager grid = new GridLayoutManager(context, 4);
            grid.setSpanCount(4);
            rv_cat.setLayoutManager(grid);
            rv_cat.setItemAnimator(new DefaultItemAnimator());
            rv_cat.setHasFixedSize(true);
        }
    }

    class LatestHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_latest;
        TextView tv_title;
        LinearLayout ll_home_view_all;
        ImageView iv_lay;

        LatestHolder(View view) {
            super(view);
            rv_latest = view.findViewById(R.id.rv_home_post);
            tv_title = view.findViewById(R.id.tv_home_title);
            ll_home_view_all = view.findViewById(R.id.ll_home_view_all);
            iv_lay = view.findViewById(R.id.iv_lay);
        }
    }

    class GridHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_grid;
        AdapterPostHome adapterPostGrid;
        TextView tv_title;
        LinearLayout ll_home_view_all;

        GridHolder(View view) {
            super(view);
            rv_grid = view.findViewById(R.id.rv_home_cat);
            tv_title = view.findViewById(R.id.tv_home_title);
            ll_home_view_all = view.findViewById(R.id.ll_home_view_all);
            GridLayoutManager grid_fresh = new GridLayoutManager(context, 2);
            grid_fresh.setSpanCount(2);
            rv_grid.setLayoutManager(grid_fresh);
            rv_grid.setItemAnimator(new DefaultItemAnimator());
            rv_grid.setHasFixedSize(true);
        }
    }

    class HorizontalHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_horizontal;
        AdapterPostHome adapterPostHorizontal;
        TextView tv_title;
        LinearLayout ll_home_view_all;

        HorizontalHolder(View view) {
            super(view);
            rv_horizontal = view.findViewById(R.id.rv_home_cat);
            tv_title = view.findViewById(R.id.tv_home_title);
            ll_home_view_all = view.findViewById(R.id.ll_home_view_all);
            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rv_horizontal.setLayoutManager(llm);
            rv_horizontal.setItemAnimator(new DefaultItemAnimator());
            rv_horizontal.setHasFixedSize(true);
        }
    }

    class VerticalHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_vertical;
        AdapterSimilar adapterPostVertical;
        TextView tv_title;
        LinearLayout ll_home_view_all;

        VerticalHolder(View view) {
            super(view);
            rv_vertical = view.findViewById(R.id.rv_home_cat);
            tv_title = view.findViewById(R.id.tv_home_title);
            ll_home_view_all = view.findViewById(R.id.ll_home_view_all);
            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            rv_vertical.setLayoutManager(llm);
            rv_vertical.setItemAnimator(new DefaultItemAnimator());
            rv_vertical.setHasFixedSize(true);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_SPOTLIGHT) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_spotlight, parent, false);
            return new SpotlightHolder(itemView);
        } else if (viewType == VIEW_CATEGORIES) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new CategoriesHolder(itemView);
        } else if (viewType == VIEW_LATEST) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_post, parent, false);
            return new LatestHolder(itemView);
        } else if (viewType == VIEW_GRID) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new GridHolder(itemView);
        } else if (viewType == VIEW_HORIZONTAL) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new HorizontalHolder(itemView);
        } else if (viewType == VIEW_VERTICAL) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_ui_categories, parent, false);
            return new VerticalHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SpotlightHolder) {
            if (((SpotlightHolder) holder).homePagerAdapter == null) {
                ((SpotlightHolder) holder).enchantedViewPager.setFocusable(false);
                ((SpotlightHolder) holder).homePagerAdapter = new HomePagerAdapter(context, arrayList.get(holder.getAdapterPosition()).getArrayListSpotlight());
                ((SpotlightHolder) holder).enchantedViewPager.setAdapter(((SpotlightHolder) holder).homePagerAdapter);
            }
        }
        else if (holder instanceof CategoriesHolder) {

            ((CategoriesHolder) holder).tv_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());

            ((CategoriesHolder) holder).adapterHomeCategories = new AdapterHomeCategories(arrayList.get(holder.getAdapterPosition()).getArrayListCategories());
            ((CategoriesHolder) holder).rv_cat.setAdapter(((CategoriesHolder) holder).adapterHomeCategories);

            ((CategoriesHolder) holder).rv_cat.addOnItemTouchListener(new RecyclerItemClickListener(context, (view, position1) -> {
                clickPos = holder.getAdapterPosition();
                helper.showInterAd(position1, context.getString(R.string.category));
            }));

            ((CategoriesHolder) holder).ll_home_view_all.setOnClickListener(v -> context.startActivity(new Intent(context, CategoryActivity.class)));
        }
        else if (holder instanceof LatestHolder) {

            ((LatestHolder) holder).tv_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());
            ((LatestHolder) holder).iv_lay.setImageResource(Boolean.TRUE.equals(sharedPref.getGrid()) ? R.drawable.ic_round_list : R.drawable.ic_grid_view);

            if (Boolean.TRUE.equals(sharedPref.getGrid())){
                GridLayoutManager grid_fresh = new GridLayoutManager(context, 2);
                grid_fresh.setSpanCount(2);
                ((LatestHolder) holder).rv_latest.setLayoutManager(grid_fresh);
                ((LatestHolder) holder).rv_latest.setItemAnimator(new DefaultItemAnimator());
                ((LatestHolder) holder).rv_latest.setHasFixedSize(true);
                AdapterPostHome adapterPostHome = new AdapterPostHome(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemData, position12) -> {
                    clickPos = holder.getAdapterPosition();
                    helper.showInterAd(position12, context.getString(R.string.latest));
                });
                ((LatestHolder) holder).rv_latest.setAdapter(adapterPostHome);
            } else {
                LinearLayoutManager llm = new LinearLayoutManager(context);
                ((LatestHolder) holder).rv_latest.setLayoutManager(llm);
                ((LatestHolder) holder).rv_latest.setItemAnimator(new DefaultItemAnimator());
                ((LatestHolder) holder).rv_latest.setHasFixedSize(true);
                AdapterSimilar adapter = new AdapterSimilar(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemPost, position13) -> {
                    clickPos = holder.getAdapterPosition();
                    helper.showInterAd(position13, context.getString(R.string.latest));
                });
                ((LatestHolder) holder).rv_latest.setAdapter(adapter);
            }

            ((LatestHolder) holder).iv_lay.setOnClickListener(v1 -> {
                sharedPref.setGrid(!sharedPref.getGrid());
                ((LatestHolder) holder).iv_lay.setImageResource(Boolean.TRUE.equals(sharedPref.getGrid()) ? R.drawable.ic_round_list : R.drawable.ic_grid_view);

                if (Boolean.TRUE.equals(sharedPref.getGrid())){
                    GridLayoutManager grid_fresh = new GridLayoutManager(context, 2);
                    grid_fresh.setSpanCount(2);
                    ((LatestHolder) holder).rv_latest.setLayoutManager(grid_fresh);
                    ((LatestHolder) holder).rv_latest.setItemAnimator(new DefaultItemAnimator());
                    ((LatestHolder) holder).rv_latest.setHasFixedSize(true);
                    AdapterPostHome adapterPostHome = new AdapterPostHome(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemData, position22) -> {
                        clickPos = holder.getAdapterPosition();
                        helper.showInterAd(position22, context.getString(R.string.latest));
                    });
                    ((LatestHolder) holder).rv_latest.setAdapter(adapterPostHome);
                } else {
                    LinearLayoutManager llm = new LinearLayoutManager(context);
                    ((LatestHolder) holder).rv_latest.setLayoutManager(llm);
                    ((LatestHolder) holder).rv_latest.setItemAnimator(new DefaultItemAnimator());
                    ((LatestHolder) holder).rv_latest.setHasFixedSize(true);
                    AdapterSimilar adapter = new AdapterSimilar(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemPost, position23) -> {
                        clickPos = holder.getAdapterPosition();
                        helper.showInterAd(position23, context.getString(R.string.latest));
                    });
                    ((LatestHolder) holder).rv_latest.setAdapter(adapter);
                }
            });

            ((LatestHolder) holder).ll_home_view_all.setOnClickListener(v -> {
                Callback.city_id = "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.search_item = "";
                context.startActivity(new Intent(context, AllPostActivity.class));
            });
        }
        else if (holder instanceof GridHolder) {

            ((GridHolder) holder).tv_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());

            ((GridHolder) holder).adapterPostGrid = new AdapterPostHome(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemData, position18) -> {
                clickPos = holder.getAdapterPosition();
                helper.showInterAd(position18, context.getString(R.string.latest));
            });
            ((GridHolder) holder).rv_grid.setAdapter(((GridHolder) holder).adapterPostGrid);

            ((GridHolder) holder).ll_home_view_all.setOnClickListener(v -> {
                Callback.city_id = "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.search_item = "";
                context.startActivity(new Intent(context, AllPostActivity.class));
            });
        }
        else if (holder instanceof HorizontalHolder) {

            ((HorizontalHolder) holder).tv_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());

            ((HorizontalHolder) holder).adapterPostHorizontal = new AdapterPostHome(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemPost, position19) -> {
                clickPos = holder.getAdapterPosition();
                helper.showInterAd(position19, context.getString(R.string.latest));
            });
            ((HorizontalHolder) holder).rv_horizontal.setAdapter(((HorizontalHolder) holder).adapterPostHorizontal);

            ((HorizontalHolder) holder).ll_home_view_all.setOnClickListener(v -> {
                Callback.city_id = "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.search_item = "";
                context.startActivity(new Intent(context, AllPostActivity.class));
            });
        }
        else if (holder instanceof VerticalHolder) {

            ((VerticalHolder) holder).tv_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());

            ((VerticalHolder) holder).adapterPostVertical = new AdapterSimilar(context, arrayList.get(holder.getAdapterPosition()).getArrayListPost(), (itemPost, position20) -> {
                clickPos = holder.getAdapterPosition();
                helper.showInterAd(position20, context.getString(R.string.latest));
            });
            ((VerticalHolder) holder).rv_vertical.setAdapter(((VerticalHolder) holder).adapterPostVertical);

            ((VerticalHolder) holder).ll_home_view_all.setOnClickListener(v -> {
                Callback.city_id = "";
                Callback.city_name = "City";
                Callback.cat_id = "";
                Callback.cat_name = "Category";
                Callback.search_item = "";
                context.startActivity(new Intent(context, AllPostActivity.class));
            });
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return arrayList.get(position) == null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (arrayList.get(position).getType()) {
            case "spotlight":
                return VIEW_SPOTLIGHT;
            case "category":
                return VIEW_CATEGORIES;
            case "Latest":
                return VIEW_LATEST;
            case "grid":
                return VIEW_GRID;
            case "horizontal":
                return VIEW_HORIZONTAL;
            case "vertical":
                return VIEW_VERTICAL;
            default:
                return VIEW_PROG;
        }
    }

    InterAdListener interAdListener = (position, type) -> {
        if (type.equals(context.getString(R.string.category))){
            Callback.city_id = "";
            Callback.scat_id = "";
            Callback.cat_id = arrayList.get(clickPos).getArrayListCategories().get(position).getId();
            Callback.cat_name = arrayList.get(clickPos).getArrayListCategories().get(position).getName();
            Callback.search_item = "";
            context.startActivity(new Intent(context, AllPostActivity.class));
        } else if (type.equals(context.getString(R.string.latest))){
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("post_id", arrayList.get(clickPos).getArrayListPost().get(position).getId());
            intent.putExtra("user_id", arrayList.get(clickPos).getArrayListPost().get(position).getUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, intent, null);
        }
    };
}