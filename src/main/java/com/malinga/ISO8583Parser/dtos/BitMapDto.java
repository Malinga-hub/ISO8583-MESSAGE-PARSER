package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BitMapDto {
    private String primaryBitMap;
    private String secondaryBitMap;
    private String bitmap;
    private boolean isSecondaryBitMapPresent;
    private String bitmapBinaryBCD;
    private List<Integer> dataElementsPresent;
}
