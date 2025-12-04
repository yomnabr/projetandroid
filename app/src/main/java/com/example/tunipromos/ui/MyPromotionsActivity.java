package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunipromos.R;
import com.example.tunipromos.adapter.PromotionAdapter;
import com.example.tunipromos.models.Promotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyPromotionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PromotionAdapter adapter;
    List<Promotion> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_promotions);

        recyclerView = findViewById(R.id.recyclerMyPromos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔹 On passe la méthode onPromotionClick au constructeur de l'adapter
        adapter = new PromotionAdapter(list, this::onPromotionClick);
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMyPromotions();
    }

    private void loadMyPromotions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("promotions")
                .whereEqualTo("providerId", uid)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Promotion p = doc.toObject(Promotion.class);
                        if (p != null) {
                            // 🔴 TRÈS IMPORTANT : garder l'id du document pour la suppression
                            p.setId(doc.getId());
                            list.add(p);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    // 🔹 Appelé quand on clique sur une promo dans la liste
    private void onPromotionClick(Promotion promo) {
        if (promo.getId() == null || promo.getId().isEmpty()) {
            Toast.makeText(this, "ID promotion manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous supprimer cette promotion ?")
                .setPositiveButton("Oui", (dialog, which) -> {

                    FirebaseFirestore.getInstance()
                            .collection("promotions")
                            .document(promo.getId())
                            .delete()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Promotion supprimée", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Erreur suppression: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
