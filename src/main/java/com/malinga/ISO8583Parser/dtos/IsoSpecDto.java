package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

@Data
public class IsoSpecDto {
    private String fieldName;
    private int fieldNumber;
    private String fieldType;
    private int fieldLength;
    private String fieldLengthType;
    private String fieldDescription;
    private String fieldValue;
}
