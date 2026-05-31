package com.hvh.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class VNPaySecurity {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String buildQueryString(Map<String, String> params, String hashSecret) throws Exception {
        Map<String, String> sortedParams = new TreeMap<>(params);

        String hashData = sortedParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        System.out.println("=== HashData ===\n" + hashData);

        String query = sortedParams.entrySet().stream()
                .map(e -> {
                    try {
                        return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8).replace("+", "%20");
                    } catch (Exception ex) {
                        return e.getKey() + "=" + e.getValue();
                    }
                })
                .collect(Collectors.joining("&"));

        String secureHash = hmacSHA512(hashSecret, hashData);
        System.out.println("=== SecureHash: " + secureHash + " ===");

        return query + "&vnp_SecureHash=" + secureHash;
    }
}
