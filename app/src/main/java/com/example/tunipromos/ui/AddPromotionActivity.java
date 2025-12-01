package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tunipromos.R;
import com.example.tunipromos.models.Promotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPromotionActivity extends AppCompatActivity {

    EditText etTitle, etDesc, etImage, etMerchant, etDiscount;
    Spinner spinnerCategory;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etImage = findViewById(R.id.etImageUrl);
        etMerchant = findViewById(R.id.etMerchant);
        etDiscount = findViewById(R.id.etDiscount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAdd = findViewById(R.id.btnAdd);

        // Spinner catégories
        String[] categories = {"Électronique", "Vêtements", "Alimentation", "Beauté", "Sport", "Maison"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addPromotion());
    }

    private void addPromotion() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String image = etImage.getText().toString().trim();
        String merchant = etMerchant.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        int discount = 0;

        try {
            discount = Integer.parseInt(etDiscount.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(this, "Réduction invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || desc.isEmpty() || merchant.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Promotion promo = new Promotion(title, desc, image);
        promo.setCategory(category);
        promo.setMerchantName(merchant);
        promo.setDiscount(discount);

        FirebaseFirestore.getInstance().collection("promotions")
                .add(promo)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Promotion ajoutée !", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}