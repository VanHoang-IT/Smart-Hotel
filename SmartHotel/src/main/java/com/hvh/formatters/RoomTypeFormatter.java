/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.formatters;

import com.hvh.pojo.RoomType;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author 03358
 */
public class RoomTypeFormatter implements Formatter<RoomType>{

    @Override
    public String print(RoomType type, Locale locale) {
        return String.valueOf(type.getId());
    }

    @Override
    public RoomType parse(String typeId, Locale locale) throws ParseException {
        RoomType t = new RoomType();
        t.setId(Long.valueOf(typeId));
        return t;
    }
}
