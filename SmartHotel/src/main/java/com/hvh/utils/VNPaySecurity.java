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
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo chữ ký VNPay", ex);
        }
    }

    public static String buildQueryString(Map<String, String> params, String hashSecret) throws Exception {
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        StringBuilder hashData = new StringBuilder();
        sortedParams.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) hashData.append('&');
                hashData.append(key).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        });

        System.out.println("=== HashData ===\n" + hashData);

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        System.out.println("=== SecureHash: " + secureHash + " ===");

        String query = sortedParams.entrySet().stream()
                .map(e -> {
                    try {
                        return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8).replace("+", "%20");
                    } catch (Exception ex) {
                        return e.getKey() + "=" + e.getValue();
                    }
                })
                .collect(Collectors.joining("&"));

        return query + "&vnp_SecureHash=" + secureHash;
    }

    public static TreeMap<String, String> filterAndSortVnpParams(Map<String, String> source) {
        TreeMap<String, String> result = new TreeMap<>();
        source.forEach((key, value) -> {
            if (key.startsWith("vnp_") && !"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)) {
                result.put(key, value);
            }
        });
        return result;
    }
}