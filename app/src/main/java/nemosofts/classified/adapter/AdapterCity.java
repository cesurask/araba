package nemosofts.classified.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nemosofts.classified.R;
import nemosofts.classified.item.ItemCity;


public class AdapterCity extends RecyclerView.Adapter<AdapterCity.ViewHolder> {

    private final RecyclerItemClickListener listener;
    private final List<ItemCity> arrayList;

    public AdapterCity(List<ItemCity> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_city, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.tv_city.setText(arrayList.get(position).getName());

        holder.bind(arrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_city;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_city = itemView.findViewById(R.id.tv_city);
        }

        public void bind(final ItemCity itemCity, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onClickListener(itemCity, getLayoutPosition()));
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemCity itemCity, int position);
    }
}
