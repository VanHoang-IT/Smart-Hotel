package com.hvh.payment;

import com.hvh.utils.VNPaySecurity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TimeZone;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VNPayPaymentHandler extends PaymentHandler {

    private final String vnp_TmnCode = "9ZWQ78YX";
    private final String vnp_HashSecret = "TGBN67687XR3I30TJQO148EB3CW20E0I";
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_ReturnUrl = "http://localhost:8080/SmartHotel/api/public/payments/vnpay-return";
    
    @Override
    public String getMethod() { return "VNPAY"; }

    @Override
    public Map<String, Object> createPaymentUrl(Long reservationId) throws Exception {
        BigDecimal amount = serviceOrderService.getTotalAmountByReservation(reservationId);
        long vnpAmount = amount.longValue() * 100;
        String vnp_TxnRef = reservationId + "_" + System.currentTimeMillis();
        String vnp_OrderInfo = "Thanh toan don " + reservationId;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String vnp_CreateDate = sdf.format(new Date());

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", vnp_OrderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", vnp_CreateDate);

        String queryString = VNPaySecurity.buildQueryString(params, vnp_HashSecret);
        String paymentUrl = vnp_Url + "?" + queryString;

        Map<String, Object> result = new HashMap<>();
        result.put("payUrl", paymentUrl);
        return result;
    }

    @Override
    @Transactional
    public void processReturn(Map<String, String> params) {
        if ("00".equals(params.get("vnp_ResponseCode"))) {
            String txnRef = params.get("vnp_TxnRef");
            Long resId = Long.parseLong(txnRef.split("_")[0]);
            BigDecimal amount = new BigDecimal(params.get("vnp_Amount"))
                    .divide(BigDecimal.valueOf(100));
            String transId = params.get("vnp_TransactionNo");
            confirmReservation(resId, amount, "TRANSFER", transId);
        }
    }

    @Override
    @Transactional
    public void processCallback(Map<String, Object> payload) {
        Long reservationId = Long.valueOf(payload.get("reservationId").toString());
        BigDecimal amount = serviceOrderService.getTotalAmountByReservation(reservationId);
        confirmReservation(reservationId, amount, "TRANSFER", "VNPAY_" + System.currentTimeMillis());
    }
}