package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tunipromos.R;
import com.example.tunipromos.auth.LoginActivity;
import com.example.tunipromos.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    EditText etDisplayName, etLocation;
    TextView tvEmail;
    Button btnSave, btnLogout, btnPreferences;
    CheckBox cbAlimentation, cbElectronique, cbMode, cbMaison, cbBeaute, cbSport;
    CheckBox cbNotifications;
    SeekBar seekRadius;
    TextView tvRadius;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvEmail = findViewById(R.id.tvEmail);
        etDisplayName = findViewById(R.id.etDisplayName);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        btnPreferences = findViewById(R.id.btnPreferences);

        // Catégories
        cbAlimentation = findViewById(R.id.cbAlimentation);
        cbElectronique = findViewById(R.id.cbElectronique);
        cbMode = findViewById(R.id.cbMode);
        cbMaison = findViewById(R.id.cbMaison);
        cbBeaute = findViewById(R.id.cbBeaute);
        cbSport = findViewById(R.id.cbSport);

        // Notifications
        cbNotifications = findViewById(R.id.cbNotifications);
        seekRadius = findViewById(R.id.seekRadius);
        tvRadius = findViewById(R.id.tvRadius);
    }

    private void loadUserData() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentUser = doc.toObject(User.class);
                        displayUserData();
                    } else {
                        // Créer un nouveau profil
                        currentUser = new User(
                                uid,
                                auth.getCurrentUser().getEmail(),
                                "Utilisateur",
                                "consumer"
                        );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void displayUserData() {
        tvEmail.setText(currentUser.getEmail());
        etDisplayName.setText(currentUser.getDisplayName());

        if (currentUser.getPreferences() != null) {
            User.UserPreferences prefs = currentUser.getPreferences();

            etLocation.setText(prefs.getLocation());
            cbNotifications.setChecked(prefs.isNotificationsEnabled());
            seekRadius.setProgress(prefs.getNotificationRadius());
            tvRadius.setText(prefs.getNotificationRadius() + " km");

            // Charger les catégories préférées
            List<String> categories = prefs.getCategories();
            if (categories != null) {
                cbAlimentation.setChecked(categories.contains("alimentation"));
                cbElectronique.setChecked(categories.contains("electronique"));
                cbMode.setChecked(categories.contains("mode"));
                cbMaison.setChecked(categories.contains("maison"));
                cbBeaute.setChecked(categories.contains("beaute"));
                cbSport.setChecked(categories.contains("sport"));
            }
        }
    }

    private void setupListeners() {
        seekRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText(progress + " km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void saveProfile() {
        // Mettre à jour les informations de base
        currentUser.setDisplayName(etDisplayName.getText().toString());

        // Mettre à jour les préférences
        User.UserPreferences prefs = currentUser.getPreferences();
        if (prefs == null) prefs = new User.UserPreferences();

        prefs.setLocation(etLocation.getText().toString());
        prefs.setNotificationsEnabled(cbNotifications.isChecked());
        prefs.setNotificationRadius(seekRadius.getProgress());

        // Catégories sélectionnées
        List<String> selectedCategories = new ArrayList<>();
        if (cbAlimentation.isChecked()) selectedCategories.add("alimentation");
        if (cbElectronique.isChecked()) selectedCategories.add("electronique");
        if (cbMode.isChecked()) selectedCategories.add("mode");
        if (cbMaison.isChecked()) selectedCategories.add("maison");
        if (cbBeaute.isChecked()) selectedCategories.add("beaute");
        if (cbSport.isChecked()) selectedCategories.add("sport");

        prefs.setCategories(selectedCategories);
        currentUser.setPreferences(prefs);

        // Sauvegarder dans Firestore
        db.collection("users").document(currentUser.getUid())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profil mis à jour!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}