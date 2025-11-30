package com.example.tunipromos.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnLogin;
    TextView btnGoRegister;
    ProgressBar progressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());

        btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("L'email est requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPass.setError("Le mot de passe est requis");
            return;
        }

        if (password.length() < 6) {
            etPass.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        // Afficher le progressBar
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnLogin.setEnabled(false);

        // Connexion Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(LoginActivity.this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnLogin.setEnabled(true);

                    String errorMessage = "Erreur de connexion";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("no user record")) {
                            errorMessage = "Aucun compte trouvé avec cet email";
                        } else if (e.getMessage().contains("password is invalid")) {
                            errorMessage = "Mot de passe incorrect";
                        } else if (e.getMessage().contains("network error")) {
                            errorMessage = "Erreur de connexion Internet";
                        }
                    }

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }
}