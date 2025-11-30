package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.tunipromos.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PromotionDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc, tvCategory, tvMerchant, tvDiscount, tvLocation;
    ImageView imgPromo;
    FloatingActionButton fabFavorite;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    String promotionId;
    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_details);

        initViews();
        loadPromotionDetails();
        checkIfFavorite();
        setupFavoriteButton();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvCategory = findViewById(R.id.tvCategory);
        tvMerchant = findViewById(R.id.tvMerchant);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvLocation = findViewById(R.id.tvLocation);
        imgPromo = findViewById(R.id.imgPromo);
        fabFavorite = findViewById(R.id.fabFavorite);
    }

    private void loadPromotionDetails() {
        promotionId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");
        String image = getIntent().getStringExtra("image");
        String category = getIntent().getStringExtra("category");
        String merchant = getIntent().getStringExtra("merchant");
        int discount = getIntent().getIntExtra("discount", 0);
        String location = getIntent().getStringExtra("location");

        tvTitle.setText(title);
        tvDesc.setText(desc);

        if (category != null && !category.isEmpty()) {
            tvCategory.setText("📦 Catégorie: " + category);
            tvCategory.setVisibility(TextView.VISIBLE);
        }

        if (merchant != null && !merchant.isEmpty()) {
            tvMerchant.setText("🏪 " + merchant);
            tvMerchant.setVisibility(TextView.VISIBLE);
        }

        if (discount > 0) {
            tvDiscount.setText("💰 -" + discount + "% de réduction");
            tvDiscount.setVisibility(TextView.VISIBLE);
        }

        if (location != null && !location.isEmpty()) {
            tvLocation.setText("📍 " + location);
            tvLocation.setVisibility(TextView.VISIBLE);
        }

        Glide.with(this)
                .load(image)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgPromo);
    }

    private void checkIfFavorite() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("promotionId", promotionId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    isFavorite = !querySnapshot.isEmpty();
                    updateFavoriteIcon();
                });
    }

    private void setupFavoriteButton() {
        fabFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void toggleFavorite() {
        String userId = auth.getCurrentUser().getUid();

        if (isFavorite) {
            // Supprimer des favoris
            db.collection("favorites")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("promotionId", promotionId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            querySnapshot.getDocuments().get(0).getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        isFavorite = false;
                                        updateFavoriteIcon();
                                        Toast.makeText(this, "Retiré des favoris",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        } else {
            // Ajouter aux favoris
            Map<String, Object> favorite = new HashMap<>();
            favorite.put("userId", userId);
            favorite.put("promotionId", promotionId);
            favorite.put("createdAt", System.currentTimeMillis());

            db.collection("favorites").add(favorite)
                    .addOnSuccessListener(documentReference -> {
                        isFavorite = true;
                        updateFavoriteIcon();
                        Toast.makeText(this, "Ajouté aux favoris",
                                Toast.LENGTH_SHORT).show();

                        // Incrémenter le compteur de favoris
                        db.collection("promotions").document(promotionId)
                                .update("favoriteCount",
                                        com.google.firebase.firestore.FieldValue.increment(1));
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.star_big_off);
        }
    }
}