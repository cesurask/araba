package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.nemosofts.theme.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.asyncTask.LoadStatus;
import nemosofts.classified.callback.Callback;
import nemosofts.classified.interfaces.SuccessListener;
import nemosofts.classified.item.ItemComment;
import nemosofts.classified.utils.SharedPref;
import nemosofts.classified.utils.helper.Helper;


public class AdapterComment extends RecyclerView.Adapter {

    private final Helper helper;
    private final SharedPref sharedPref;
    private final ArrayList<ItemComment> arrayList;
    public Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private String user_id="";

    public AdapterComment(String user_id, Context context, ArrayList<ItemComment> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.user_id = user_id;
        helper = new Helper(context);
        sharedPref = new SharedPref(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rl_comment_user, rl_comment_admin;
        private final TextView tv_comment_user, tv_comment_admin, tv_msg_user,
                tv_msg_admin, tv_comment_user_name, tv_comment_admin_name;
        private final ImageView iv_comment_user, iv_comment_admin;

        MyViewHolder(View view) {
            super(view);

            rl_comment_user = view.findViewById(R.id.rl_comment_user);
            rl_comment_admin = view.findViewById(R.id.rl_comment_admin);

            tv_comment_user = view.findViewById(R.id.tv_comment_user);
            tv_comment_admin = view.findViewById(R.id.tv_comment_admin);

            tv_msg_user = view.findViewById(R.id.tv_msg_user);
            tv_msg_admin = view.findViewById(R.id.tv_msg_admin);

            tv_comment_user_name = view.findViewById(R.id.tv_comment_user_name);
            tv_comment_admin_name = view.findViewById(R.id.tv_comment_admin_name);

            iv_comment_user = view.findViewById(R.id.iv_comment_user);
            iv_comment_admin = view.findViewById(R.id.iv_comment_admin);
        }

        public void bind(final ItemComment itemComment){
            itemView.setOnLongClickListener(v -> {
                if (sharedPref.getUserId().equals(itemComment.getUserId())){
                    showRemovedDialog(getLayoutPosition());
                }
                return true;
            });
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            ItemComment itemComment = arrayList.get(position);
            if (itemComment.getUserId().equals(user_id)){
                ((MyViewHolder) holder).tv_comment_admin_name.setTextColor(ColorUtils.colorAccent(context));
                ((MyViewHolder) holder).tv_comment_user_name.setTextColor(ColorUtils.colorAccent(context));
            } else {
                ((MyViewHolder) holder).tv_comment_admin_name.setTextColor(ColorUtils.colorTitle(context));
                ((MyViewHolder) holder).tv_comment_user_name.setTextColor(ColorUtils.colorTitle(context));
            }

            if (sharedPref.getUserId().equals(itemComment.getUserId())){
                ((MyViewHolder) holder).rl_comment_admin.setVisibility(View.GONE);
                ((MyViewHolder) holder).rl_comment_user.setVisibility(View.VISIBLE);

                ((MyViewHolder) holder).tv_comment_admin.setText("");
                ((MyViewHolder) holder).tv_comment_user.setText(itemComment.getCommentText());

                ((MyViewHolder) holder).tv_msg_admin.setText("");
                ((MyViewHolder) holder).tv_msg_user.setText(itemComment.getDate());

                ((MyViewHolder) holder).tv_comment_admin_name.setText("");
                if (itemComment.getUserId().equals(user_id)){
                    ((MyViewHolder) holder).tv_comment_user_name.setText(itemComment.getUserName()+" . admin");
                } else {
                    ((MyViewHolder) holder).tv_comment_user_name.setText(itemComment.getUserName());
                }

                Picasso.get()
                        .load(arrayList.get(position).getDp())
                        .placeholder(R.drawable.user_photo)
                        .into(((MyViewHolder) holder).iv_comment_user);

            } else {

                ((MyViewHolder) holder).rl_comment_admin.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).rl_comment_user.setVisibility(View.GONE);

                ((MyViewHolder) holder).tv_comment_admin.setText(itemComment.getCommentText());
                ((MyViewHolder) holder).tv_comment_user.setText("");

                ((MyViewHolder) holder).tv_msg_admin.setText(itemComment.getDate());
                ((MyViewHolder) holder).tv_msg_user.setText("");

                if (itemComment.getUserId().equals(user_id)){
                    ((MyViewHolder) holder).tv_comment_admin_name.setText(itemComment.getUserName()+" . admin");
                } else {
                    ((MyViewHolder) holder).tv_comment_admin_name.setText(itemComment.getUserName());
                }
                ((MyViewHolder) holder).tv_comment_user_name.setText("");

                Picasso.get()
                        .load(arrayList.get(position).getDp())
                        .placeholder(R.drawable.user_photo)
                        .into(((MyViewHolder) holder).iv_comment_admin);
            }
            ((MyViewHolder) holder).bind(arrayList.get(position));

        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isProgressPos(position) ? VIEW_PROG : VIEW_ITEM;
    }

    private boolean isProgressPos(int position) {
        return position == arrayList.size();
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private void showRemovedDialog(int position_) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alert.setTitle("Deleted");
        alert.setMessage(context.getString(R.string.sure_deleted));
        alert.setPositiveButton(context.getString(R.string.ok), (dialogInterface, i) -> removeComment(position_));
        alert.setNegativeButton(context.getString(R.string.cancel), (dialogInterface, i) -> {
        });
        alert.show();
    }

    private void removeComment(int position_) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context, R.style.ThemeDialog);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        LoadStatus loadFav = new LoadStatus(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String favSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    arrayList.remove(position_);
                    notifyItemRemoved(position_);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                }
            }
        }, helper.getAPIRequest(Callback.METHOD_DELETE_COMMENTS, 0, arrayList.get(position_).getId(), "", "", "", "", "", "", "", "", "", "", "", null, null));
        loadFav.execute();
    }
}