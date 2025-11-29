package com.example.tunipromos.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tunipromos.models.Promotion;
import com.example.tunipromos.repository.PromotionRepository;

public class PromotionViewModel extends ViewModel {

    PromotionRepository repo = new PromotionRepository();

    public void createPromotion(String title, String desc, String imageUrl) {
        Promotion p = new Promotion(null, title, desc, imageUrl);
        repo.addPromotion(p);
    }
}
