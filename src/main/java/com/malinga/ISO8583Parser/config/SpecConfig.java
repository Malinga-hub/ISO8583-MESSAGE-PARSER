package com.malinga.ISO8583Parser.config;

import com.malinga.ISO8583Parser.dtos.IsoSpecDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "iso8583")
@Data
public class SpecConfig {
    private List<IsoSpecDto> isoSpecDefinition;
}
