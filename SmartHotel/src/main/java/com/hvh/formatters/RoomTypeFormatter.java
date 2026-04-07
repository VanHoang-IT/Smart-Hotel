/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.formatters;

import com.hvh.pojo.RoomTypes;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author 03358
 */
public class RoomTypeFormatter implements Formatter<RoomTypes>{

    @Override
    public String print(RoomTypes type, Locale locale) {
        return String.valueOf(type.getId());
    }

    @Override
    public RoomTypes parse(String typeId, Locale locale) throws ParseException {
        RoomTypes t = new RoomTypes();
        t.setId(Long.valueOf(typeId));
        return t;
    }
}
