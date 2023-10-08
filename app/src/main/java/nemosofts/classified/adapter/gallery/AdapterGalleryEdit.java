package nemosofts.classified.adapter.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.nemosofts.view.RoundedImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemGallery;
import nemosofts.classified.utils.helper.Helper;

public class AdapterGalleryEdit extends RecyclerView.Adapter<AdapterGalleryEdit.MyViewHolder> {

    private final Context context;
    private final Helper helper;
    private final String postID;
    private final ArrayList<ItemGallery> arrayList;
    private final ArrayList<File> arrayListFile;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView iv_gallery, iv_close;

        MyViewHolder(View view) {
            super(view);
            iv_gallery = view.findViewById(R.id.iv_gallery);
            iv_close = view.findViewById(R.id.iv_gallery_close);
        }
    }

    public AdapterGalleryEdit(Context context, String postID, ArrayList<ItemGallery> arrayList, ArrayList<File> arrayListFile) {
        this.context = context;
        this.postID = postID;
        this.arrayList = arrayList;
        this.arrayListFile = arrayListFile;
        helper = new Helper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_img, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (arrayList.get(position).getFile() != null) {
            Picasso.get()
                    .load("file://" + arrayList.get(position).getFile())
                    .placeholder(R.drawable.material_design_default)
                    .into(holder.iv_gallery);
        } else {
            Picasso.get()
                    .load(arrayList.get(position).getImage())
                    .centerCrop()
                    .resize(400,300)
                    .placeholder(R.drawable.material_design_default)
                    .into(holder.iv_gallery);
        }

        holder.iv_close.setOnClickListener(view -> {
            if (position <= arrayList.size()) {
                if (arrayList.get(position).getFile() != null) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        for (int j = 0; j < arrayListFile.size(); j++) {

                            if (arrayList.get(i).getFile() != null && (arrayList.get(i).getFile() == arrayListFile.get(j))) {
                                arrayListFile.remove(j);
                                break;
                            }
                        }
                    }
                    arrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, arrayList.size());
                } else {
                    openRemoveDialog(holder.getAdapterPosition());
                }
            }
        });
    }

    private void openRemoveDialog(int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(context.getString(R.string.delete));
        alertDialog.setMessage(context.getString(R.string.sure_delete_uploaded_image));
        alertDialog.setPositiveButton(context.getString(R.string.delete), (dialogInterface, i) -> loadRemoveImage(pos));
        alertDialog.setNegativeButton(context.getString(R.string.cancel), (dialogInterface, i) -> {
        });
        alertDialog.show();
    }

    private void loadRemoveImage(int pos) {
        if (helper.isNetworkAvailable()) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.loading));
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            arrayList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, arrayList.size());
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }, helper.getAPIRequest(Callback.METHOD_REMOVE_GALLERY_IMAGE, 0, postID, arrayList.get(pos).getId(), "", "", "", "", "", "", "", "", "", "", null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(context, context.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
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
}