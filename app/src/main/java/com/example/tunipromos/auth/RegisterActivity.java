package com.example.tunipromos.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.ui.AddPromotionActivity;
import com.example.tunipromos.ui.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private RadioButton rbConsumer, rbMerchant;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        rbConsumer = findViewById(R.id.rbConsumer);
        rbMerchant = findViewById(R.id.rbMerchant);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> register());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation des champs
        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }

        // Vérifier qu'un rôle est sélectionné
        int checkedId = rgRole.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Veuillez choisir un rôle", Toast.LENGTH_SHORT).show();
            return;
        }

        String role;
        if (checkedId == R.id.rbConsumer) {
            role = "consumer";
        } else if (checkedId == R.id.rbMerchant) {
            role = "merchant";
        } else {
            role = "consumer"; // valeur de secours
        }

        showLoading(true);

        // Créer l'utilisateur dans Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Utilisateur créé avec succès dans Auth
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Sauvegarder les informations supplémentaires dans Firestore
                            saveUserToFirestore(user.getUid(), name, email, role);
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                "Erreur d'inscription: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Le nom est requis");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("L'email est requis");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Le mot de passe est requis");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Les mots de passe ne correspondent pas");
            return false;
        }

        return true;
    }

    private void saveUserToFirestore(String userId, String name, String email, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", role);
        user.put("createdAt", System.currentTimeMillis());
        user.put("notificationsEnabled", true);
        user.put("interests", new java.util.ArrayList<String>()); // Liste vide par défaut

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(RegisterActivity.this,
                            "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    navigateToHomeByRole(role);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(RegisterActivity.this,
                            "Erreur lors de l'enregistrement des données: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        btnRegister.setEnabled(!isLoading);
        tvLogin.setEnabled(!isLoading);
    }

    private void navigateToHomeByRole(String role) {
        Intent intent;
        if ("merchant".equals(role)) {
            // écran réservé aux marchands (ajout de promotion, dashboard, etc.)
            intent = new Intent(this, AddPromotionActivity.class);
        } else {
            // écran consommateur
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
