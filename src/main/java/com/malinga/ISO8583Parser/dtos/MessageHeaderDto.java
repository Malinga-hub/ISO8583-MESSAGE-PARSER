package com.malinga.ISO8583Parser.dtos;

import lombok.Data;

@Data
public class MessageHeaderDto {
    private String messageHeader;
    private String protocolVersionIdentifier;
    private String messageSource;
    private String versionNumber;
    private String fieldInError;
    private String notUsed;
}
