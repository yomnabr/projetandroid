package com.example.tunipromos.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tunipromos.R;
import com.example.tunipromos.models.Promotion;
import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {

    private List<Promotion> list;
    private OnPromotionClickListener listener;

    public interface OnPromotionClickListener {
        void onPromotionClick(Promotion promotion);
    }

    public PromotionAdapter(List<Promotion> list, OnPromotionClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Promotion p = list.get(position);

        h.title.setText(p.getTitle());
        h.desc.setText(p.getDescription());

        // Catégorie
        if (p.getCategory() != null) {
            h.category.setVisibility(View.VISIBLE);
            h.category.setText(p.getCategory());
        } else {
            h.category.setVisibility(View.GONE);
        }

        // Réduction
        if (p.getDiscount() > 0) {
            h.discount.setVisibility(View.VISIBLE);
            h.discount.setText("-" + p.getDiscount() + "%");
        } else {
            h.discount.setVisibility(View.GONE);
        }

        // Image
        Glide.with(h.img.getContext())
                .load(p.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(h.img);

        // Click
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPromotionClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, category, discount;
        ImageView img;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            desc = v.findViewById(R.id.tvDesc);
            category = v.findViewById(R.id.tvCategory);
            discount = v.findViewById(R.id.tvDiscount);
            img = v.findViewById(R.id.imgPromo);
        }
    }
}