package com.example.tunipromos.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

        // 🔹 Image : priorité à base64Image, sinon imageUrl, sinon placeholder
        boolean imageSet = false;

        if (p.getBase64Image() != null && !p.getBase64Image().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(p.getBase64Image(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                h.img.setImageBitmap(bitmap);
                imageSet = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!imageSet) {
            String url = p.getImageUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(h.img.getContext())
                        .load(url)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(h.img);
            } else {
                // Aucun visuel dispo → placeholder local
                h.img.setImageResource(R.mipmap.ic_launcher);
            }
        }

        // Click
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPromotionClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
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
