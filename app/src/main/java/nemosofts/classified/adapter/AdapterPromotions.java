package nemosofts.classified.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemPromotions;

public class AdapterPromotions extends RecyclerView.Adapter<AdapterPromotions.ViewHolder> {

    private final List<ItemPromotions> itemPromotionsList;
    private final Boolean dailyBumpUp;
    private final Boolean topAd;
    private final Boolean spotLight;
    private final String USD;

    public AdapterPromotions(String USD, Boolean dailyBumpUp, Boolean topAd, Boolean spotLight, List<ItemPromotions> itemPromotions) {
        this.itemPromotionsList = itemPromotions;
        this.dailyBumpUp = dailyBumpUp;
        this.topAd = topAd;
        this.spotLight = spotLight;
        this.USD = USD;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_promostions,parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ItemPromotions item = itemPromotionsList.get(position);

        holder.tv_speed.setText(item.getPlanName());
        holder.tv_speed_text.setText(item.getPlanDetails());
        holder.tv_speed_pri.setText("From " + USD + " "+item.getPlanPrice());
        holder.tv_group_1.setText("From " + USD + " "+item.getPlanPrice());
        holder.tv_group_2.setText("From " + USD + " "+item.getPlanPrice_2());
        holder.tv_group_3.setText("From " + USD + " "+item.getPlanPrice_3());

        Boolean isExpandable = item.isExpandable();
        holder.ll_pro_opn.setVisibility(Boolean.TRUE.equals(isExpandable) ? View.VISIBLE : View.GONE);
        holder.ll_bg_daily.setBackgroundResource(Boolean.TRUE.equals(isExpandable) ? R.drawable.bg_pay_active : R.drawable.bg_pay_normal);
        holder.cb_speed.setChecked(isExpandable);

        holder.rb_group_1.setText(item.getPlanDuration()+" "+item.getDays());
        holder.rb_group_2.setText(item.getPlanDuration_2()+" "+item.getDays2());
        holder.rb_group_3.setText(item.getPlanDuration_3()+" "+item.getDays3());

        holder.rb_group_1.setChecked(item.isGroup_1());
        holder.rb_group_2.setChecked(item.isGroup_2());
        holder.rb_group_3.setChecked(item.isGroup_3());

        if ( holder.ll_pro_opn.getVisibility()==View.VISIBLE){
            if(Boolean.TRUE.equals(!item.isGroup_1() && !item.isGroup_2() && !item.isGroup_3())){
                item.setGroup_1(false);
                item.setGroup_2(true);
                item.setGroup_3(false);
                item.setPrice(item.getPlanPrice_2());
                item.setDay(item.getPlanDuration_2());
                holder.rb_group_2.setChecked(true);
            }
        } else {
            item.setGroup_1(false);
            item.setGroup_2(false);
            item.setGroup_3(false);
            item.setPrice("00.0");
        }

        holder.rb_group_1.setOnClickListener(view -> {
            item.setGroup_1(true);
            item.setGroup_2(false);
            item.setGroup_3(false);
            item.setPrice(item.getPlanPrice());
            item.setDay(item.getPlanDuration());
            notifyItemChanged(position);
        });

        holder.rb_group_2.setOnClickListener(view -> {
            item.setGroup_1(false);
            item.setGroup_2(true);
            item.setGroup_3(false);
            item.setPrice(item.getPlanPrice_2());
            item.setDay(item.getPlanDuration_2());
            notifyItemChanged(position);
        });

        holder.rb_group_3.setOnClickListener(view -> {
            item.setGroup_1(false);
            item.setGroup_2(false);
            item.setGroup_3(true);
            item.setPrice(item.getPlanPrice_3());
            item.setDay(item.getPlanDuration_3());
            notifyItemChanged(position);
        });

        Picasso.get()
                .load(item.getPromote_image())
                .placeholder(R.color.load_color_5)
                .into(holder.iv_speed);

        switch (position) {
            case 0:
                if (Boolean.TRUE.equals(dailyBumpUp)){
                    holder.rl_daily_up_bg.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (Boolean.TRUE.equals(topAd)){
                    holder.rl_daily_up_bg.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (Boolean.TRUE.equals(spotLight)){
                    holder.rl_daily_up_bg.setVisibility(View.GONE);
                }
                break;
            default:
                holder.rl_daily_up_bg.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemPromotionsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout rl_daily_up_bg;
        public LinearLayout ll_pro_opn, ll_bg_daily;
        public ImageView iv_speed;
        public TextView tv_speed, tv_speed_text, tv_speed_pri, tv_group_1, tv_group_2, tv_group_3;
        public CheckBox cb_speed;
        public RadioButton rb_group_1, rb_group_2, rb_group_3;


        public ViewHolder(View itemView) {
            super(itemView);

            iv_speed = itemView.findViewById(R.id.iv_speed);
            tv_speed = itemView.findViewById(R.id.tv_speed);
            tv_speed_text = itemView.findViewById(R.id.tv_speed_text);
            tv_speed_pri = itemView.findViewById(R.id.tv_speed_pri);

            tv_group_1 = itemView.findViewById(R.id.tv_group_1);
            tv_group_2 = itemView.findViewById(R.id.tv_group_2);
            tv_group_3 = itemView.findViewById(R.id.tv_group_3);

            rb_group_1 = itemView.findViewById(R.id.rb_group_1);
            rb_group_2 = itemView.findViewById(R.id.rb_group_2);
            rb_group_3 = itemView.findViewById(R.id.rb_group_3);

            cb_speed = itemView.findViewById(R.id.cb_speed);
            ll_bg_daily = itemView.findViewById(R.id.ll_bg_daily);

            rl_daily_up_bg = itemView.findViewById(R.id.rl_daily_up_bg);
            rl_daily_up_bg.setOnClickListener(view -> {
                ItemPromotions faq = itemPromotionsList.get(getAdapterPosition());
                faq.setExpandable(!faq.isExpandable());
                notifyItemChanged(getAdapterPosition());
            });
            cb_speed.setOnClickListener(view -> {
                ItemPromotions faq = itemPromotionsList.get(getAdapterPosition());
                faq.setExpandable(!faq.isExpandable());
                notifyItemChanged(getAdapterPosition());
            });

            ll_pro_opn = itemView.findViewById(R.id.ll_pro_opn);
            ll_pro_opn.setOnClickListener(view -> {

            });
        }
    }
}
