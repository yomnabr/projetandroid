package com.example.tunipromos.repository;

import com.example.tunipromos.models.Promotion;
import com.google.firebase.firestore.FirebaseFirestore;

public class PromotionRepository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addPromotion(Promotion promotion) {
        db.collection("promotions").add(promotion);
    }
}
