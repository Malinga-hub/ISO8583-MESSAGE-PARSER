package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

import java.util.List;

@Data
public class IsoDto {
    private String hexDump;
    private MessageHeaderDto messageHeaderDto;
    private MtiDto mtiDto;
    private BitMapDto bitMapDto;
    private List<IsoSpecDto> dataElements;
}
