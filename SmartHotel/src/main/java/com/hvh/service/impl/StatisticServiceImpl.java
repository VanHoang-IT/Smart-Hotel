/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.repository.StatisticRepository;
import com.hvh.service.StatisticService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Override
    public List<Object[]> getMonthlyRevenue() {
        return this.statisticRepository.getMonthlyRevenue();
    }

    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        return this.statisticRepository.getMonthlyRevenue(year);
    }

    @Override
    public Map<String, Object> getSummary() {
        return this.statisticRepository.getSummary();
    }

    @Override
    public Map<String, Object> getSummary(int year) {
        return this.statisticRepository.getSummary(year);
    }

    @Override
    public List<Object[]> getTopRoomsByRevenue(int limit) {
        return this.statisticRepository.getTopRoomsByRevenue(limit);
    }

    @Override
    public List<Object[]> getTopRoomsByRevenue(int limit, int year) {
        return this.statisticRepository.getTopRoomsByRevenue(limit, year);
    }

    @Override
    public List<Object[]> getTopServicesByRevenue(int limit) {
        return this.statisticRepository.getTopServicesByRevenue(limit);
    }

    @Override
    public List<Object[]> getTopServicesByRevenue(int limit, int year) {
        return this.statisticRepository.getTopServicesByRevenue(limit, year);
    }

    @Override
    public List<Object[]> getReservationsByStatus() {
        return this.statisticRepository.getReservationsByStatus();
    }

    @Override
    public List<Object[]> getReservationsByStatus(int year) {
        return this.statisticRepository.getReservationsByStatus(year);
    }

    @Override
    public List<Integer> getAvailableYears() {
        return this.statisticRepository.getAvailableYears();
    }
}