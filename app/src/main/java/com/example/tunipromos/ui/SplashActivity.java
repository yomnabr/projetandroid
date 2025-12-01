package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 secondes
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        auth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Vérifier si l'utilisateur est déjà connecté
            if (auth.getCurrentUser() != null) {
                // Utilisateur connecté, aller vers MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Utilisateur non connecté, aller vers LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}