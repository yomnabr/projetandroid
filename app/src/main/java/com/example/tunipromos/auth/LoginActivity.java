package com.example.tunipromos.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.ui.AddPromotionActivity;
import com.example.tunipromos.ui.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPass;
    Button btnLogin;
    TextView tvRegister;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar); // assure-toi que l'id existe dans le layout

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> login());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        btnLogin.setEnabled(false);
        tvRegister.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Aller chercher le rôle dans Firestore
                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(doc -> {
                                progressBar.setVisibility(android.view.View.GONE);
                                btnLogin.setEnabled(true);
                                tvRegister.setEnabled(true);

                                if (doc.exists()) {
                                    String role = doc.getString("role");
                                    navigateToHomeByRole(role);
                                } else {
                                    Toast.makeText(this,
                                            "Profil utilisateur introuvable",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(android.view.View.GONE);
                                btnLogin.setEnabled(true);
                                tvRegister.setEnabled(true);
                                Toast.makeText(this,
                                        "Erreur lors de la récupération du profil: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnLogin.setEnabled(true);
                    tvRegister.setEnabled(true);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHomeByRole(String role) {
        Intent intent;
        if ("merchant".equals(role)) {
            intent = new Intent(this, AddPromotionActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
