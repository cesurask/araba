package nemosofts.classified.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemSubCategory;


public class AdapterSuperCategorySub extends RecyclerView.Adapter<AdapterSuperCategorySub.ViewHolder> {

    private final RecyclerItemClickListener listener;
    private final ArrayList<ItemSubCategory> arrayList;

    public AdapterSuperCategorySub(ArrayList<ItemSubCategory> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_super_sub,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.tv_cat.setText(arrayList.get(position).getName());

        Picasso.get()
                .load(arrayList.get(position).getImage())
                .resize(300,300)
                .into(holder.iv_cat);

       holder.bind(arrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_cat;
        public ImageView iv_cat;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_cat = itemView.findViewById(R.id.tv_cat);
            iv_cat = itemView.findViewById(R.id.iv_cat);
        }

        public void bind(final ItemSubCategory itemCat, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onClickListener(itemCat, getLayoutPosition()));
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemSubCategory itemCat, int position);
    }
}
