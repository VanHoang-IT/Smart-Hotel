/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hvh.repository;

import java.util.List;
import java.util.Map;

public interface StatisticRepository {
    List<Object[]> getMonthlyRevenue();
    List<Object[]> getMonthlyRevenue(int year);
    Map<String, Object> getSummary();
    Map<String, Object> getSummary(int year);
    List<Object[]> getTopRoomsByRevenue(int limit);
    List<Object[]> getTopRoomsByRevenue(int limit, int year);
    List<Object[]> getTopServicesByRevenue(int limit);
    List<Object[]> getTopServicesByRevenue(int limit, int year);
    List<Object[]> getReservationsByStatus();
    List<Object[]> getReservationsByStatus(int year);
    List<Integer> getAvailableYears();
}