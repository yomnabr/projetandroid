package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tunipromos.R;
import com.example.tunipromos.models.User;
import com.example.tunipromos.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    Switch switchNotif;
    CheckBox cbElectronique, cbVetements, cbAlimentation, cbBeaute, cbSport, cbMaison;
    Button btnSave;
    ProgressBar progressBar;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        userRepository = new UserRepository();

        switchNotif = findViewById(R.id.switchNotifications);
        cbElectronique = findViewById(R.id.cbElectronique);
        cbVetements = findViewById(R.id.cbVetements);
        cbAlimentation = findViewById(R.id.cbAlimentation);
        cbBeaute = findViewById(R.id.cbBeaute);
        cbSport = findViewById(R.id.cbSport);
        cbMaison = findViewById(R.id.cbMaison);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        loadPreferences();

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadPreferences() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRepository.getUser(uid)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            switchNotif.setChecked(user.isNotificationsEnabled());

                            List<String> cats = user.getCategories();
                            if (cats != null) {
                                cbElectronique.setChecked(cats.contains("Électronique"));
                                cbVetements.setChecked(cats.contains("Vêtements"));
                                cbAlimentation.setChecked(cats.contains("Alimentation"));
                                cbBeaute.setChecked(cats.contains("Beauté"));
                                cbSport.setChecked(cats.contains("Sport"));
                                cbMaison.setChecked(cats.contains("Maison"));
                            }
                        }
                    }
                });
    }

    private void savePreferences() {
        List<String> categories = new ArrayList<>();
        if (cbElectronique.isChecked())
            categories.add("Électronique");
        if (cbVetements.isChecked())
            categories.add("Vêtements");
        if (cbAlimentation.isChecked())
            categories.add("Alimentation");
        if (cbBeaute.isChecked())
            categories.add("Beauté");
        if (cbSport.isChecked())
            categories.add("Sport");
        if (cbMaison.isChecked())
            categories.add("Maison");

        progressBar.setVisibility(ProgressBar.VISIBLE);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRepository.updateUserPreferences(uid, categories, switchNotif.isChecked())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Préférences sauvegardées", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}