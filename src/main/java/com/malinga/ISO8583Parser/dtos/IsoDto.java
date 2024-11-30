package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

@Data
public class IsoDto {
    private String hexDump;
    private MessageHeaderDto messageHeaderDto;
    private MtiDto mtiDto;
    private BitMapDto bitMapDto;
}
