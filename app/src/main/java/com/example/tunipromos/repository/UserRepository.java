package com.example.tunipromos.repository;

import com.example.tunipromos.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class UserRepository {

    private final FirebaseFirestore db;
    private static final String COLLECTION_USERS = "users";

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createUser(User user) {
        return db.collection(COLLECTION_USERS).document(user.getUid()).set(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(COLLECTION_USERS).document(uid).get();
    }

    public Task<Void> updateUserPreferences(String uid, List<String> categories, boolean notificationsEnabled) {
        return db.collection(COLLECTION_USERS).document(uid)
                .update(
                        "categories", categories,
                        "notificationsEnabled", notificationsEnabled);
    }
}
