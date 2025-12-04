package com.example.tunipromos.services;

import android.util.Log;

import com.example.tunipromos.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message from: " + remoteMessage.getFrom());

        // Notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            if (title == null) title = "Notification";
            if (body == null) body = "";

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Body: " + body);

            NotificationHelper.showNotification(this, title, body);
        }

        // Data payload
        if (remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Data payload: " + remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);

        // Tu peux l’enregistrer dans Firestore plus tard si tu veux
        // Exemple :
        // FirebaseFirestore.getInstance().collection("tokens").document(token).set(...)
    }
}
