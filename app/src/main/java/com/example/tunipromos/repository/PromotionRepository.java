package com.example.tunipromos.repository;

import com.example.tunipromos.models.Promotion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PromotionRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<DocumentReference> addPromotion(Promotion promotion) {
        return db.collection("promotions").add(promotion);
    }

    public Task<QuerySnapshot> getAllPromotions() {
        return db.collection("promotions")
                .whereEqualTo("active", true)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get();
    }
}
