package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemChatPost;
import nemosofts.classified.utils.SharedPref;


public class AdapterChatPost extends RecyclerView.Adapter {

    private final SharedPref sharedPref;
    private final RecyclerItemClickListener listener;
    private final ArrayList<ItemChatPost> arrayList;
    public Context context;
    private final int VIEW_ITEM = 1;

    public AdapterChatPost(Context context, ArrayList<ItemChatPost> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
        sharedPref = new SharedPref(context);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rl_chat_post;
        private final ImageView iv_chat_post;
        private final TextView tv_chat_post, tv_chat_money;

        MyViewHolder(View view) {
            super(view);
            rl_chat_post = view.findViewById(R.id.rl_chat_post);
            tv_chat_post = view.findViewById(R.id.tv_chat_post);
            tv_chat_money = view.findViewById(R.id.tv_chat_money);
            iv_chat_post = view.findViewById(R.id.iv_chat_post);
        }
    }

    @SuppressLint("StaticFieldLeak")
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
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_post, parent, false);
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
            ItemChatPost itemChatPost = arrayList.get(position);

            ((MyViewHolder) holder).tv_chat_post.setText(itemChatPost.getPostTitle());

            if (sharedPref.getUserId().equals(itemChatPost.getChatUserId())){
                ((MyViewHolder) holder).tv_chat_money.setText(itemChatPost.getPostUserName());
                Picasso.get()
                        .load(arrayList.get(position).getPostUserImage())
                        .centerCrop()
                        .resize(300,300)
                        .placeholder(R.drawable.user_photo)
                        .into(((MyViewHolder) holder).iv_chat_post);
            } else {
                ((MyViewHolder) holder).tv_chat_money.setText(itemChatPost.getChatUserName());
                Picasso.get()
                        .load(arrayList.get(position).getChatUserImage())
                        .centerCrop()
                        .resize(300,300)
                        .placeholder(R.drawable.user_photo)
                        .into(((MyViewHolder) holder).iv_chat_post);
            }

            ((MyViewHolder) holder).rl_chat_post.setOnClickListener(v -> listener.onClickListener(itemChatPost, holder.getAbsoluteAdapterPosition()));

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
        return isProgressPos(position) ? 0 : VIEW_ITEM;
    }

    private boolean isProgressPos(int position) {
        return position == arrayList.size();
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemChatPost itemChatPost, int position);
    }
}