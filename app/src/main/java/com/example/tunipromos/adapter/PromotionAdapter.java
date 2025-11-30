package com.example.tunipromos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunipromos.R;
import com.example.tunipromos.models.Promotion;
import com.bumptech.glide.Glide;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {

    private List<Promotion> list;
    private OnPromotionClickListener listener;

    // Interface pour le click
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

        // Afficher la catégorie et le pourcentage de réduction
        if (p.getCategory() != null) {
            h.category.setVisibility(View.VISIBLE);
            h.category.setText(getCategoryEmoji(p.getCategory()) + " " + p.getCategory());
        } else {
            h.category.setVisibility(View.GONE);
        }

        if (p.getDiscountPercentage() > 0) {
            h.discount.setVisibility(View.VISIBLE);
            h.discount.setText("-" + p.getDiscountPercentage() + "%");
        } else {
            h.discount.setVisibility(View.GONE);
        }

        // Charger l'image
        Glide.with(h.img.getContext())
                .load(p.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(h.img);

        // Click listener
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPromotionClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "alimentation": return "🍎";
            case "electronique": return "📱";
            case "mode": return "👔";
            case "maison": return "🏠";
            case "beaute": return "💄";
            case "sport": return "⚽";
            default: return "📦";
        }
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