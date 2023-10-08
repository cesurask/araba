package nemosofts.classified.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemChat;
import nemosofts.classified.utils.SharedPref;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {

    private final ArrayList<ItemChat> arrayList;
    private final SharedPref sharedPref;

    public AdapterChat(Context context, ArrayList<ItemChat> arrayList) {
        this.arrayList = arrayList;
        sharedPref = new SharedPref(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ItemChat itemChat = arrayList.get(position);

        if (sharedPref.getUserId().equals(itemChat.getThisChatUse())){
            holder.ll_chat_admin.setVisibility(View.GONE);
            holder.ll_chat_user.setVisibility(View.VISIBLE);

            if (Boolean.TRUE.equals(itemChat.getIsImage())){
                Picasso.get()
                        .load(itemChat.getChatText())
                        .centerCrop()
                        .resize(300,300)
                        .placeholder(R.drawable.material_design_default)
                        .into(holder.iv_comment_user);
                holder.tv_comment_admin.setText("");
                holder.tv_comment_user.setVisibility(View.GONE);
            } else {
                holder.tv_comment_admin.setText("");
                holder.tv_comment_user.setText(itemChat.getChatText());
                holder.iv_comment_user.setVisibility(View.GONE);
            }

            holder.tv_date_admin.setText("");
            holder.tv_date_user.setText(itemChat.getChatOn());

        } else {

            holder.ll_chat_admin.setVisibility(View.VISIBLE);
            holder.ll_chat_user.setVisibility(View.GONE);

            if (Boolean.TRUE.equals(itemChat.getIsImage())){
                Picasso.get()
                        .load(itemChat.getChatText())
                        .centerCrop()
                        .resize(300,300)
                        .placeholder(R.drawable.material_design_default)
                        .into(holder.iv_comment_admin);

                holder.tv_comment_admin.setVisibility(View.GONE);
                holder.tv_comment_user.setText("");
            } else {
                holder.tv_comment_user.setText("");
                holder.tv_comment_admin.setText(itemChat.getChatText());
                holder.iv_comment_admin.setVisibility(View.GONE);
            }

            holder.tv_date_admin.setText(itemChat.getChatOn());
            holder.tv_date_user.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout ll_chat_user, ll_chat_admin;
        private final TextView tv_comment_user, tv_comment_admin, tv_date_user, tv_date_admin;
        private final ImageView iv_comment_user, iv_comment_admin;


        public ViewHolder(View itemView) {
            super(itemView);

            ll_chat_user = itemView.findViewById(R.id.ll_chat_user);
            ll_chat_admin = itemView.findViewById(R.id.ll_chat_admin);

            tv_comment_user = itemView.findViewById(R.id.tv_comment_user);
            tv_comment_admin = itemView.findViewById(R.id.tv_comment_admin);

            iv_comment_user = itemView.findViewById(R.id.iv_comment_user);
            iv_comment_admin = itemView.findViewById(R.id.iv_comment_admin);

            tv_date_user = itemView.findViewById(R.id.tv_date_user);
            tv_date_admin = itemView.findViewById(R.id.tv_date_admin);
        }
    }

}
