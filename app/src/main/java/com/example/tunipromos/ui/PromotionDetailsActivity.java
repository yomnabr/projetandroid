package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tunipromos.R;
import com.example.tunipromos.models.Promotion;
import com.google.firebase.firestore.FirebaseFirestore;

public class PromotionDetailsActivity extends AppCompatActivity {

    private ImageView ivPromoImage;
    private TextView tvTitle, tvDescription, tvCategory, tvMerchant, tvDiscount, tvViews;

    private FirebaseFirestore db;
    private String promotionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_details);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Récupérer les données de l'intent
        promotionId = getIntent().getStringExtra("id");

        // Initialiser les vues
        initViews();

        // Afficher les données
        displayPromotionDetails();

        // Incrémenter les vues
        incrementViews();
    }

    private void initViews() {
        ivPromoImage = findViewById(R.id.ivPromoImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvCategory = findViewById(R.id.tvCategory);
        tvMerchant = findViewById(R.id.tvMerchant);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvViews = findViewById(R.id.tvViews);
    }

    private void displayPromotionDetails() {
        // Récupérer les données depuis l'intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("desc");
        String imageUrl = getIntent().getStringExtra("image");
        String category = getIntent().getStringExtra("category");
        String merchant = getIntent().getStringExtra("merchant");
        int discount = getIntent().getIntExtra("discount", 0);

        // Afficher les données
        tvTitle.setText(title);
        tvDescription.setText(description);
        tvCategory.setText("Catégorie: " + category);
        tvMerchant.setText("Marchand: " + merchant);
        tvDiscount.setText("Réduction: " + discount + "%");

        // Charger l'image avec Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivPromoImage);
        }
    }

    private void incrementViews() {
        if (promotionId != null) {
            // Incrémenter le compteur de vues dans Firestore
            db.collection("promotions").document(promotionId)
                    .update("views", com.google.firebase.firestore.FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        // Succès - pas besoin d'action
                    })
                    .addOnFailureListener(e -> {
                        // Échec silencieux
                    });
        }
    }
}