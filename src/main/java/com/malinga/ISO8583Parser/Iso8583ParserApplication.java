package com.malinga.ISO8583Parser;

import com.malinga.ISO8583Parser.config.SpecConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = SpecConfig.class)
public class Iso8583ParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(Iso8583ParserApplication.class, args);
	}

}
