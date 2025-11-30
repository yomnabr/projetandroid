package com.example.tunipromos.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPass;
    Button btnRegister;
    TextView btnGoLogin;
    ProgressBar progressBar;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> registerUser());

        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Le nom est requis");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("L'email est requis");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPass.setError("Le mot de passe est requis");
            etPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPass.setError("Le mot de passe doit contenir au moins 6 caractères");
            etPass.requestFocus();
            return;
        }

        // Afficher le progressBar
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnRegister.setEnabled(false);

        // Créer le compte Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Récupérer l'UID de l'utilisateur créé
                    String uid = authResult.getUser().getUid();

                    // Créer le profil utilisateur dans Firestore
                    User newUser = new User(uid, email, name, "consumer");

                    db.collection("users").document(uid)
                            .set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(ProgressBar.GONE);
                                Toast.makeText(RegisterActivity.this,
                                        "Compte créé avec succès!", Toast.LENGTH_SHORT).show();

                                // Rediriger vers LoginActivity
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(ProgressBar.GONE);
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this,
                                        "Erreur lors de la création du profil: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnRegister.setEnabled(true);

                    String errorMessage = "Erreur lors de l'inscription";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("already in use")) {
                            errorMessage = "Cet email est déjà utilisé";
                        } else if (e.getMessage().contains("network error")) {
                            errorMessage = "Erreur de connexion Internet";
                        } else if (e.getMessage().contains("weak password")) {
                            errorMessage = "Mot de passe trop faible";
                        }
                    }

                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }
}