package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 secondes
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity); // ton layout existant

        auth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Utilisateur non connecté → Login
            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
                return;
            }

            // Utilisateur connecté → on vérifie son rôle dans Firestore
            String uid = auth.getCurrentUser().getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        Intent intent;

                        if (doc.exists()) {
                            String role = doc.getString("role");

                            if ("merchant".equals(role)) {
                                // Marchand → page d’ajout de promotion
                                intent = new Intent(SplashActivity.this, AddPromotionActivity.class);
                            } else {
                                // Consumer (ou autre) → MainActivity
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                            }
                        } else {
                            // Pas de doc user → on renvoie vers Login
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                        }

                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // En cas d’erreur Firestore → Login
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    });

        }, SPLASH_DELAY);
    }
}
