package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemCategory;


public class AdapterSuperCategory extends RecyclerView.Adapter<AdapterSuperCategory.ViewHolder> {

    private final RecyclerItemClickListener listener;
    private final ArrayList<ItemCategory> arrayList;
    private int row_index = -1;

    public AdapterSuperCategory(ArrayList<ItemCategory> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_super, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.tv_cat.setText(arrayList.get(position).getName());

        Picasso.get()
                .load(arrayList.get(position).getImage())
                .resize(300,300)
                .into(holder.iv_cat);

        holder.rl_super_cat.setOnClickListener(v -> listener.onClickListener(arrayList.get(position), holder.getLayoutPosition()));

        if (row_index > -1) {
            if (row_index == position) {
                holder.rl_super_cat.setBackgroundResource(R.drawable.bg_super_cat_select);
            } else {
                holder.rl_super_cat.setBackgroundResource(R.drawable.bg_super_cat_normal);
            }
        } else {
            holder.rl_super_cat.setBackgroundResource(R.drawable.bg_super_cat_normal);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_cat;
        private final ImageView iv_cat;
        private final RelativeLayout rl_super_cat;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_cat = itemView.findViewById(R.id.tv_cat);
            iv_cat = itemView.findViewById(R.id.iv_cat);
            rl_super_cat = itemView.findViewById(R.id.rl_super_cat);
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemCategory itemCat, int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }
}
