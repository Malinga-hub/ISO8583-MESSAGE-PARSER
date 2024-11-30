package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

@Data
public class MtiDto {
    private String versionNumber;
    private String messageClass;
    private String messageFunction;
    private String transactionOriginator;

}
