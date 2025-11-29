package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tunipromos.R;
import com.example.tunipromos.models.Promotion;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPromotionActivity extends AppCompatActivity {

    EditText etTitle, etDesc, etImage;
    Button btnAdd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etImage = findViewById(R.id.etImageUrl);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            Promotion p = new Promotion(
                    null,
                    etTitle.getText().toString(),
                    etDesc.getText().toString(),
                    etImage.getText().toString()
            );

            db.collection("promotions").add(p);
            finish();
        });
    }
}
