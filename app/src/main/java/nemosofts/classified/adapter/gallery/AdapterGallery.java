package nemosofts.classified.adapter.gallery;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import nemosofts.classified.R;


public class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.MyViewHolder> {

    private final ArrayList<File> arrayList;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView iv_gallery, iv_close;

        MyViewHolder(View view) {
            super(view);
            iv_gallery = view.findViewById(R.id.iv_gallery);
            iv_close = view.findViewById(R.id.iv_gallery_close);
        }
    }

    public AdapterGallery(ArrayList<File> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_img, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.get()
                .load("file://" + arrayList.get(position))
                .placeholder(R.drawable.material_design_default)
                .into(holder.iv_gallery);

        holder.iv_close.setOnClickListener(view -> {
            if(position <= arrayList.size()) {
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arrayList.size());
            }
        });
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