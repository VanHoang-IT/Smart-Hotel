package com.hvh.payment;

import com.hvh.utils.MoMoSecurity;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Component
public class MoMoPaymentHandler extends PaymentHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${momo.partner_code}")
    private String partnerCode;

    @Value("${momo.access_key}")
    private String accessKey;

    @Value("${momo.secret_key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect_url}")
    private String redirectUrl;

    @Value("${momo.ipn_url}")
    private String ipnUrl;

    @Override
    public String getMethod() {
        return "MOMO";
    }

    @Override
    public Map<String, Object> createPaymentUrl(Long reservationId) throws Exception {
        BigDecimal calculatedAmount = serviceOrderService.getTotalAmountByReservation(reservationId);
        Long payableAmount = calculatedAmount.longValue();
        String orderId = reservationId + "_" + System.currentTimeMillis();
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderInfo = "Thanh toan SmartHotel. Don: " + reservationId;
        String requestType = "captureWallet";
        String extraData = "";

        String rawHash = "accessKey=" + accessKey
                + "&amount=" + payableAmount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = MoMoSecurity.signHmacSHA256(rawHash, secretKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("requestId", requestId);
        requestBody.put("amount", payableAmount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "vi");

        return restTemplate.postForObject(endpoint, requestBody, Map.class);
    }

    @Override
    @Transactional
    public void processCallback(Map<String, Object> callbackData) {
        if ("0".equals(String.valueOf(callbackData.get("resultCode")))) {
            String orderId = callbackData.get("orderId").toString();
            Long resId = Long.parseLong(orderId.split("_")[0]);
            BigDecimal amount = new BigDecimal(callbackData.get("amount").toString());
            String transId = callbackData.get("transId").toString();
            confirmReservation(resId, amount, "E_WALLET", transId);
        }
    }
}
