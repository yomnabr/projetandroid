package com.example.tunipromos.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.tunipromos.R;
import com.example.tunipromos.adapter.PromotionAdapter;
import com.example.tunipromos.models.Promotion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    FloatingActionButton fab;
    PromotionAdapter adapter;
    List<Promotion> list = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerPromos);
        fab = findViewById(R.id.fabAdd);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionAdapter(list);
        recycler.setAdapter(adapter);

        db.collection("promotions").addSnapshotListener((value, error) -> {
            list.clear();
            for (DocumentSnapshot d : value.getDocuments()) {
                Promotion p = d.toObject(Promotion.class);
                list.add(p);
            }
            adapter.notifyDataSetChanged();
        });

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddPromotionActivity.class)));
    }
}
