package com.hvh.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentFactory {

    private final Map<String, PaymentHandler> handlers = new HashMap<>();

    @Autowired
    public PaymentFactory(List<PaymentHandler> handlers) {
        for (PaymentHandler handler : handlers) {
            this.handlers.put(handler.getMethod().toUpperCase(), handler);
        }
    }

    public PaymentHandler getHandler(String method) {
        PaymentHandler handler = handlers.get(method.toUpperCase());
        if (handler == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + method);
        }
        return handler;
    }
}