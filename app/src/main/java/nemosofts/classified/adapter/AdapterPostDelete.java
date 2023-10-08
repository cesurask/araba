package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.activity.PostDetailsActivity;
import nemosofts.classified.activity.addEdit.EditPostActivity;
import nemosofts.classified.activity.payment.PromotionsActivity;
import nemosofts.classified.dialog.FeedBackDialog;
import nemosofts.classified.item.ItemPostDatabase;

public class AdapterPostDelete extends RecyclerView.Adapter<AdapterPostDelete.ViewHolder> {

    private static ArrayList<ItemPostDatabase> arrayList;
    public final Context context;

    public AdapterPostDelete(Context context, ArrayList<ItemPostDatabase> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post_del, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ItemPostDatabase itemData = arrayList.get(position);

        Picasso.get()
                .load(itemData.getImage1())
                .centerCrop()
                .resize(300,300)
                .placeholder(R.drawable.material_design_default)
                .into(holder.iv_post_small);

        holder.tv_title_small.setText(itemData.getTitle());
        holder.tv_date_small.setText(itemData.getDateTime());
        holder.ll_item_news.setVisibility(Boolean.TRUE.equals(itemData.isExpandable()) ? View.VISIBLE : View.GONE);

        if (Boolean.TRUE.equals(itemData.isActive())){
            holder.tv_post_wait.setVisibility(View.GONE);
            holder.tv_post_active.setVisibility(View.VISIBLE);
        } else {
            holder.tv_post_wait.setVisibility(View.VISIBLE);
            holder.tv_post_active.setVisibility(View.GONE);
        }

        holder.ll_view.setOnClickListener(v -> {
            Intent intentEdit = new Intent(context, EditPostActivity.class);
            intentEdit.putExtra("post_id", arrayList.get(position).getId());
            intentEdit.putExtra("user_id", arrayList.get(position).getUserId());
            intentEdit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, intentEdit, null);
        });
        holder.ll_feedback.setOnClickListener(v -> new FeedBackDialog((Activity) context).showDialog(arrayList.get(position).getId(), arrayList.get(position).getTitle()));
        holder.ll_promote.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(arrayList.get(position).isActive())){
                Intent intentPromote = new Intent(context, PromotionsActivity.class);
                intentPromote.putExtra("id", arrayList.get(position).getId());
                intentPromote.putExtra("name", arrayList.get(position).getTitle());
                intentPromote.putExtra("money", arrayList.get(position).getMoney());
                intentPromote.putExtra("time", arrayList.get(position).getDateTime());
                intentPromote.putExtra("image", arrayList.get(position).getImage1());
                intentPromote.putExtra("cat_name", arrayList.get(position).getCatName());
                intentPromote.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(context, intentPromote, null);
            } else {
                Toast.makeText(context, "Wait until admin approve it", Toast.LENGTH_SHORT).show();
            }
        });
        holder.ll_view_post.setOnClickListener(v -> {
            Intent intentPost = new Intent(context, PostDetailsActivity.class);
            intentPost.putExtra("post_id", arrayList.get(position).getId());
            intentPost.putExtra("user_id", arrayList.get(position).getUserId());
            intentPost.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, intentPost, null);
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView iv_post_small;
        private final TextView tv_title_small, tv_date_small;
        private final LinearLayout ll_item_news;
        private final TextView tv_post_active, tv_post_wait;

        private final LinearLayout  ll_view, ll_feedback, ll_promote, ll_view_post;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_post_small = itemView.findViewById(R.id.iv_post_small);
            tv_title_small = itemView.findViewById(R.id.tv_title_small);
            tv_date_small = itemView.findViewById(R.id.tv_date_small);
            ll_item_news = itemView.findViewById(R.id.ll_item_news);

            ll_view = itemView.findViewById(R.id.ll_view);
            ll_feedback = itemView.findViewById(R.id.ll_feedback);
            ll_promote = itemView.findViewById(R.id.ll_promote);
            ll_view_post = itemView.findViewById(R.id.ll_view_post);

            tv_post_active = itemView.findViewById(R.id.tv_post_active);
            tv_post_wait = itemView.findViewById(R.id.tv_post_wait);

            itemView.findViewById(R.id.rl_news_small).setOnClickListener(view1 -> {
                arrayList.get(getAdapterPosition()).setExpandable(!arrayList.get(getAdapterPosition()).isExpandable());
                notifyItemChanged(getAdapterPosition());
            });

            itemView.findViewById(R.id.rl_news_small).setOnLongClickListener(view1 -> {
                arrayList.get(getAdapterPosition()).setExpandable(!arrayList.get(getAdapterPosition()).isExpandable());
                notifyItemChanged(getAdapterPosition());
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}