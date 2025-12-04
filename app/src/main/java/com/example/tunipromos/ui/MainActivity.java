package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunipromos.R;
import com.example.tunipromos.adapter.PromotionAdapter;
import com.example.tunipromos.auth.LoginActivity;
import com.example.tunipromos.models.Promotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    PromotionAdapter adapter;
    List<Promotion> allPromoList = new ArrayList<>();
    List<Promotion> filteredPromoList = new ArrayList<>();

    SearchView searchView;
    Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        // Vérifier le rôle de l'utilisateur et rediriger si marchand
        checkUserRoleAndRedirect();

        // Init Views
        recycler = findViewById(R.id.recyclerPromos);
        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionAdapter(filteredPromoList, this::openDetails);
        recycler.setAdapter(adapter);

        // Spinner Setup
        String[] categories = {"Tout", "Électronique", "Vêtements", "Alimentation",
                "Beauté", "Sport", "Maison"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // Listeners
        setupListeners();

        // Load Data
        loadPromotions();
    }

    /**
     * Si l'utilisateur connecté est un marchand, on l'envoie vers AddPromotionActivity.
     */
    private void checkUserRoleAndRedirect() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");
                        if ("merchant".equals(role)) {
                            // Redirection vers la page d'ajout de promotion
                            Intent i = new Intent(MainActivity.this, AddPromotionActivity.class);
                            startActivity(i);
                            finish(); // on ferme MainActivity
                        }
                    }
                });
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPromotions();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPromotions();
                return false;
            }
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterPromotions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadPromotions() {
        FirebaseFirestore.getInstance().collection("promotions")
                .whereEqualTo("active", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        allPromoList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Promotion p = doc.toObject(Promotion.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                allPromoList.add(p);
                            }
                        }
                        filterPromotions();
                    }
                });
    }

    private void filterPromotions() {
        String query = searchView.getQuery().toString().toLowerCase().trim();
        String category = spinnerFilter.getSelectedItem().toString();

        filteredPromoList.clear();

        for (Promotion p : allPromoList) {
            boolean matchesSearch =
                    p.getTitle().toLowerCase().contains(query) ||
                            p.getDescription().toLowerCase().contains(query);

            boolean matchesCategory =
                    category.equals("Tout") ||
                            (p.getCategory() != null && p.getCategory().equals(category));

            if (matchesSearch && matchesCategory) {
                filteredPromoList.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void openDetails(Promotion promo) {
        Intent intent = new Intent(this, PromotionDetailsActivity.class);
        intent.putExtra("id", promo.getId());
        intent.putExtra("title", promo.getTitle());
        intent.putExtra("desc", promo.getDescription());
        intent.putExtra("image", promo.getImageUrl());
        intent.putExtra("category", promo.getCategory());
        intent.putExtra("merchant", promo.getMerchantName());
        intent.putExtra("discount", promo.getDiscount());
        intent.putExtra("providerId", promo.getProviderId());
        startActivity(intent);
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
        } else if (id == R.id.action_preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
