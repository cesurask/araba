package nemosofts.classified.adapter.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.dialog.FeedBackDialog;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemData;
import nemosofts.classified.utils.ApplicationUtil;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;


public class AdapterSimilar extends RecyclerView.Adapter<AdapterSimilar.ViewHolder> {

    private final SharedPref sharedPref;
    private final Helper helper;
    private final Context context;
    private final RecyclerItemClickListener listener;
    private final ArrayList<ItemData> arrayList;

    public AdapterSimilar(Context context, ArrayList<ItemData> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        this.context = context;
        sharedPref = new SharedPref(context);
        helper = new Helper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post_similar,parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ItemData itemPost = arrayList.get(position);

        Picasso.get()
                .load(itemPost.getImage())
                .centerCrop()
                .resize(300,300)
                .placeholder(R.color.load_color_1)
                .into(holder.iv_img);

        holder.tv_text.setText(itemPost.getTitle());
        holder.tv_city.setText(itemPost.getCatName());
        if (Boolean.TRUE.equals(Callback.currency_position)){
            holder.tv_pri.setText(Callback.currency_code+" "+ ApplicationUtil.formatCurrency(itemPost.getMoney()));
        } else {
            holder.tv_pri.setText(ApplicationUtil.formatCurrency(itemPost.getMoney())+" "+Callback.currency_code);
        }
        holder.tv_date.setText(itemPost.getDateTime());

        holder.rl_sold_out.setVisibility(itemPost.getSoldOut().equals("0") ? View.VISIBLE : View.GONE);

        holder.iv_more.setOnClickListener(v -> {
            try {
                openBottomSheet(holder.getAdapterPosition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

       holder.bind(itemPost, listener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_img;
        private final TextView tv_text;
        private final TextView tv_city;
        private final TextView tv_pri;
        private final TextView tv_date;
        private final RelativeLayout rl_sold_out;
        private final ImageView iv_more;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_similar_img);
            tv_text = itemView.findViewById(R.id.tv_similar_text);
            tv_city = itemView.findViewById(R.id.tv_similar_city);
            tv_pri = itemView.findViewById(R.id.tv_similar_pri);
            tv_date = itemView.findViewById(R.id.tv_similar_date);
            rl_sold_out = itemView.findViewById(R.id.rl_sold_out);

            iv_more = itemView.findViewById(R.id.iv_more);
        }

        public void bind(final ItemData itemPost, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onClickListener(itemPost, getLayoutPosition()));
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemData itemPost, int position);
    }

    private void openBottomSheet(int adapterPosition) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.bottom_sheet_post, null);

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);

        TextView tv_sheet_text = dialog.findViewById(R.id.tv_sheet_text);
        TextView tv_sheet_date = dialog.findViewById(R.id.tv_sheet_date);
        RoundedImageView iv_sheet_post = dialog.findViewById(R.id.iv_sheet_post);
        LinearLayout ll_sheet_blocked = dialog.findViewById(R.id.ll_sheet_blocked);
        LinearLayout ll_sheet_report = dialog.findViewById(R.id.ll_sheet_report);
        LinearLayout ll_sheet_share = dialog.findViewById(R.id.ll_sheet_share);
        LinearLayout ll_sheet_fav = dialog.findViewById(R.id.ll_sheet_fav);

        if (iv_sheet_post != null){
            Picasso.get()
                    .load(arrayList.get(adapterPosition).getImage())
                    .centerCrop()
                    .resize(300,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_sheet_post);
        }
        Objects.requireNonNull(tv_sheet_text).setText(arrayList.get(adapterPosition).getTitle());
        Objects.requireNonNull(tv_sheet_date).setText(arrayList.get(adapterPosition).getDateTime());
        Objects.requireNonNull(ll_sheet_blocked).setOnClickListener(view1 -> {
            loadBlockList(adapterPosition);
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_report).setOnClickListener(view2 -> {
            new FeedBackDialog((Activity) context).showDialog(arrayList.get(adapterPosition).getId(),arrayList.get(adapterPosition).getTitle());
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_share).setOnClickListener(view3 -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, arrayList.get(adapterPosition).getTitle() + " - " + context.getString(R.string.get_more) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));
            dialog.dismiss();
        });
        Objects.requireNonNull(ll_sheet_fav).setOnClickListener(view4 -> {
            loadFav(adapterPosition);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void loadBlockList(int pos) {
        if (sharedPref.isLogged()) {
            if (helper.isNetworkAvailable()) {
                LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                    @Override
                    public void onStart() {
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            try {
                                notifyDataSetChanged();
                                arrayList.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, helper.getAPIRequest(Callback.METHOD_DO_BLOCK, 0, arrayList.get(pos).getId(), "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
                loadFav.execute();
            } else {
                Toast.makeText(context, context.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            helper.clickLogin();
        }
    }

    private void loadFav(int pos) {
        if (sharedPref.isLogged()) {
            if (helper.isNetworkAvailable()) {
                LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, helper.getAPIRequest(Callback.METHOD_DO_FAV, 0,  arrayList.get(pos).getId(), "", "", "", sharedPref.getUserId(), "", "", "", "", "", "", "", null, null));
                loadFav.execute();
            } else {
                Toast.makeText(context, context.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            helper.clickLogin();
        }
    }
}
