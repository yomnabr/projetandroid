package com.example.tunipromos.ui;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tunipromos.R;

public class PromotionDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc;
    ImageView imgPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_details);

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        imgPromo = findViewById(R.id.imgPromo);

        tvTitle.setText(getIntent().getStringExtra("title"));
        tvDesc.setText(getIntent().getStringExtra("desc"));

        Glide.with(this).load(getIntent().getStringExtra("image")).into(imgPromo);
    }
}
