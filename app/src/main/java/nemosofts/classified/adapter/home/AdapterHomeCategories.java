package nemosofts.classified.adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemCategory;

public class AdapterHomeCategories extends RecyclerView.Adapter<AdapterHomeCategories.MyViewHolder> {

    private final List<ItemCategory> arrayList;

    public AdapterHomeCategories(List<ItemCategory> arrayList) {
        this.arrayList = arrayList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_cat;
        private final TextView tv_title;

        MyViewHolder(View view) {
            super(view);
            iv_cat = view.findViewById(R.id.iv_cat_home);
            tv_title = view.findViewById(R.id.tv_cat_home);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_cat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_title.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .resize(300,300)
                .placeholder(R.drawable.material_design_default)
                .into(holder.iv_cat);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}