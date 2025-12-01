package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.tunipromos.R;
import com.example.tunipromos.adapter.PromotionAdapter;
import com.example.tunipromos.auth.LoginActivity;
import com.example.tunipromos.models.Promotion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    PromotionAdapter adapter;
    List<Promotion> promoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView
        recycler = findViewById(R.id.recyclerPromos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionAdapter(promoList, this::openDetails);
        recycler.setAdapter(adapter);

        // FAB Ajouter
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddPromotionActivity.class))
        );

        // Charger promotions
        loadPromotions();
    }

    private void loadPromotions() {
        FirebaseFirestore.getInstance().collection("promotions")
                .whereEqualTo("active", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        promoList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Promotion p = doc.toObject(Promotion.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                promoList.add(p);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
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