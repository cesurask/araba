package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemFilter;

public class AdapterFilter extends RecyclerView.Adapter<AdapterFilter.ViewHolder> {

    private final Context context;
    private final List<ItemFilter> dataList;
    private RvOnClickListener clickListener;
    private int row_index = -1;

    public AdapterFilter(Context context, List<ItemFilter> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ItemFilter item = dataList.get(position);
        holder.tv_title.setText(item.getName());

        holder.rl_bg.setOnClickListener(v -> clickListener.onItemClick(holder.getAbsoluteAdapterPosition()));
        holder.radioBtn.setOnClickListener(view -> clickListener.onItemClick(holder.getAbsoluteAdapterPosition()));

        if (row_index > -1) {
            if (row_index == position) {
                holder.rl_bg.setBackgroundResource(R.drawable.filter_select);
                holder.radioBtn.setChecked(true);
                holder.tv_title.setTextColor(Color.parseColor("#2196F3"));
            } else {
                holder.rl_bg.setBackgroundResource(R.drawable.filter_normal);
                holder.radioBtn.setChecked(false);
                if (Boolean.TRUE.equals(new ThemeEngine(context).getIsThemeMode())){
                    holder.tv_title.setTextColor(Color.parseColor("#DBDBDB"));
                } else {
                    holder.tv_title.setTextColor(Color.parseColor("#161616"));
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_title;
        public RelativeLayout rl_bg;
        public RadioButton radioBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_filter_title);
            rl_bg = itemView.findViewById(R.id.rl_filter_bg);
            radioBtn = itemView.findViewById(R.id.radioButton);
        }
    }

    public interface RvOnClickListener {
        void onItemClick(int position);
    }
}
