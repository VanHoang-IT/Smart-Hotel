package com.hvh.service;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;

public interface MailService {
    void sendInvoiceEmail(Reservation reservation, Payment payment);
}
