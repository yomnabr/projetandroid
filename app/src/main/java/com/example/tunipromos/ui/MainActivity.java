package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;

import com.example.tunipromos.R;
import com.example.tunipromos.adapter.PromotionAdapter;
import com.example.tunipromos.models.Promotion;
import com.example.tunipromos.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    FloatingActionButton fab;
    Toolbar toolbar;
    PromotionAdapter adapter;
    List<Promotion> allPromotions = new ArrayList<>();
    List<Promotion> filteredPromotions = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadUserPreferences();
        setupRecyclerView();
        loadPromotions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recycler = findViewById(R.id.recyclerPromos);
        fab = findViewById(R.id.fabAdd);

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddPromotionActivity.class)));
    }

    private void loadUserPreferences() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentUser = doc.toObject(User.class);
                        // Recharger les promotions avec le filtre
                        filterPromotions();
                    }
                });
    }

    private void setupRecyclerView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionAdapter(filteredPromotions, promotion -> {
            // Click listener - ouvrir les détails
            Intent intent = new Intent(MainActivity.this, PromotionDetailsActivity.class);
            intent.putExtra("id", promotion.getId());
            intent.putExtra("title", promotion.getTitle());
            intent.putExtra("desc", promotion.getDescription());
            intent.putExtra("image", promotion.getImageUrl());
            intent.putExtra("category", promotion.getCategory());
            intent.putExtra("merchant", promotion.getMerchantName());
            intent.putExtra("discount", promotion.getDiscountPercentage());
            intent.putExtra("location", promotion.getLocation());
            startActivity(intent);

            // Incrémenter le compteur de vues
            incrementViewCount(promotion.getId());
        });
        recycler.setAdapter(adapter);
    }

    private void loadPromotions() {
        db.collection("promotions")
                .whereEqualTo("isActive", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erreur: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allPromotions.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Promotion p = doc.toObject(Promotion.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                allPromotions.add(p);
                            }
                        }
                    }

                    filterPromotions();
                });
    }

    private void filterPromotions() {
        filteredPromotions.clear();

        if (currentUser == null || currentUser.getPreferences() == null ||
                currentUser.getPreferences().getCategories() == null ||
                currentUser.getPreferences().getCategories().isEmpty()) {
            // Pas de filtre, afficher tout
            filteredPromotions.addAll(allPromotions);
        } else {
            // Filtrer par catégories préférées
            List<String> preferredCategories = currentUser.getPreferences().getCategories();

            for (Promotion promo : allPromotions) {
                if (promo.getCategory() != null &&
                        preferredCategories.contains(promo.getCategory())) {
                    filteredPromotions.add(promo);
                }
            }

            // Si aucune promotion ne correspond, afficher tout
            if (filteredPromotions.isEmpty()) {
                filteredPromotions.addAll(allPromotions);
                Toast.makeText(this, "Aucune promotion correspondant à vos préférences",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Trier par date (plus récent en premier)
        Collections.sort(filteredPromotions, (p1, p2) -> {
            if (p1.getCreatedAt() == null) return 1;
            if (p2.getCreatedAt() == null) return -1;
            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
        });

        adapter.notifyDataSetChanged();
    }

    private void incrementViewCount(String promoId) {
        db.collection("promotions").document(promoId)
                .update("viewCount", FieldValue.increment(1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            loadPromotions();
            Toast.makeText(this, "Actualisation...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_all) {
            // Afficher toutes les promotions
            filteredPromotions.clear();
            filteredPromotions.addAll(allPromotions);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserPreferences();
    }
}