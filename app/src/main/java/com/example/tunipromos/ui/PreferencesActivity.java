package com.example.tunipromos.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tunipromos.R;
import com.example.tunipromos.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private CheckBox cbElectronique, cbVetements, cbAlimentation;
    private CheckBox cbBeaute, cbSport, cbMaison;
    private Button btnSave;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        loadPreferences();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Préférences");

        switchNotifications = findViewById(R.id.switchNotifications);
        cbElectronique = findViewById(R.id.cbElectronique);
        cbVetements = findViewById(R.id.cbVetements);
        cbAlimentation = findViewById(R.id.cbAlimentation);
        cbBeaute = findViewById(R.id.cbBeaute);
        cbSport = findViewById(R.id.cbSport);
        cbMaison = findViewById(R.id.cbMaison);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadPreferences() {
        showProgress(true);

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showProgress(false);
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        displayPreferences();
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void displayPreferences() {
        if (currentUser != null) {
            switchNotifications.setChecked(currentUser.isNotificationsEnabled());

            List<String> interests = currentUser.getInterests();
            if (interests != null) {
                cbElectronique.setChecked(interests.contains("Électronique"));
                cbVetements.setChecked(interests.contains("Vêtements"));
                cbAlimentation.setChecked(interests.contains("Alimentation"));
                cbBeaute.setChecked(interests.contains("Beauté"));
                cbSport.setChecked(interests.contains("Sport"));
                cbMaison.setChecked(interests.contains("Maison"));
            }
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> savePreferences());

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Notifications activées", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications désactivées", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePreferences() {
        boolean notificationsEnabled = switchNotifications.isChecked();

        List<String> interests = new ArrayList<>();
        if (cbElectronique.isChecked()) interests.add("Électronique");
        if (cbVetements.isChecked()) interests.add("Vêtements");
        if (cbAlimentation.isChecked()) interests.add("Alimentation");
        if (cbBeaute.isChecked()) interests.add("Beauté");
        if (cbSport.isChecked()) interests.add("Sport");
        if (cbMaison.isChecked()) interests.add("Maison");

        showProgress(true);

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .update(
                        "notificationsEnabled", notificationsEnabled,
                        "interests", interests
                )
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(this, "Préférences sauvegardées", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }
}