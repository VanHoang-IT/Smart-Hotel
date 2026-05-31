/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.configs;
/**
 *
 * @author 03358
 */
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        InputStream serviceAccount;
        String envJson = System.getenv("FIREBASE_CREDENTIALS");
        if (envJson != null && !envJson.isBlank()) {
            serviceAccount = new java.io.ByteArrayInputStream(envJson.getBytes());
        } else {
            serviceAccount = getClass().getClassLoader().getResourceAsStream(
                "smarthotel-fc975-firebase-adminsdk-fbsvc-63386df18b.json"
            );
            if (serviceAccount == null) {
                throw new IOException("Không tìm thấy file service account trong classpath");
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://smarthotel-fc975-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

        return FirebaseApp.initializeApp(options);
    }
}