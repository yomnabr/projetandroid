package com.example.tunipromos.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.auth.LoginActivity;
import com.example.tunipromos.models.Promotion;
import com.example.tunipromos.repository.PromotionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddPromotionActivity extends AppCompatActivity {

    EditText etTitle, etDesc, etMerchant, etDiscount;
    Spinner spinnerCategory;
    Button btnAdd, btnBack, btnLogout, btnSelectImage, btnMyPromos;
    ImageView ivPreview;
    ProgressBar progressBar;

    private PromotionRepository promotionRepository;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Uri imageUri;
    private String base64Image = "";

    // Sélecteur d'image depuis la galerie
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            convertImageToBase64(uri);
                            ivPreview.setImageURI(uri);
                            ivPreview.setVisibility(android.view.View.VISIBLE);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vérifier si l'utilisateur est connecté
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vous devez être connecté", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Vérifier le rôle (seulement marchand)
        checkUserRole();
    }

    private void checkUserRole() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("merchant".equals(role)) {
                            // OK → afficher l'écran
                            setContentView(R.layout.activity_add_promotion);
                            initViews();
                            setupSpinner();
                            setupListeners();
                        } else {
                            Toast.makeText(this,
                                    "Seuls les marchands peuvent ajouter des promotions",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this,
                                "Utilisateur non trouvé",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etMerchant = findViewById(R.id.etMerchant);
        etDiscount = findViewById(R.id.etDiscount);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnMyPromos = findViewById(R.id.btnMyPromos);
        ivPreview = findViewById(R.id.ivPreview);
        progressBar = findViewById(R.id.progressBar);

        promotionRepository = new PromotionRepository();
    }

    private void setupSpinner() {
        String[] categories = {"Électronique", "Vêtements", "Alimentation",
                "Beauté", "Sport", "Maison"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        // choisir une image
        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // publier
        btnAdd.setOnClickListener(v -> validateAndSave());

        // retour
        btnBack.setOnClickListener(v -> finish());

        // déconnexion
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        // aller vers Mes promotions
        btnMyPromos.setOnClickListener(v -> {
            startActivity(new Intent(this, MyPromotionsActivity.class));
        });
    }

    private void validateAndSave() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String merchant = etMerchant.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || merchant.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (base64Image.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        btnAdd.setEnabled(false);

        savePromotion(title, desc, merchant);
    }

    private void savePromotion(String title, String desc, String merchant) {
        String category = spinnerCategory.getSelectedItem().toString();
        int discount = 0;
        try {
            discount = Integer.parseInt(etDiscount.getText().toString().trim());
        } catch (Exception ignored) {}

        Promotion promo = new Promotion();
        promo.setTitle(title);
        promo.setDescription(desc);
        promo.setImageUrl(null);           // on n'utilise pas imageUrl ici
        promo.setBase64Image(base64Image); // 🔹 image en Base64
        promo.setCategory(category);
        promo.setMerchantName(merchant);
        promo.setDiscount(discount);
        promo.setTimestamp(System.currentTimeMillis());
        promo.setActive(true);

        if (mAuth.getCurrentUser() != null) {
            promo.setProviderId(mAuth.getCurrentUser().getUid());
        }

        promotionRepository.addPromotion(promo)
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnAdd.setEnabled(true);
                    Toast.makeText(this, "Promotion ajoutée !", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnAdd.setEnabled(true);
                    Toast.makeText(this, "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Convertir l'image en Base64 (en la compressant pour rester sous la limite Firestore)
    private void convertImageToBase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Qualité 30% pour éviter un document trop gros
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();

            base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Toast.makeText(this, "Image prête", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur conversion image", Toast.LENGTH_SHORT).show();
        }
    }
}
