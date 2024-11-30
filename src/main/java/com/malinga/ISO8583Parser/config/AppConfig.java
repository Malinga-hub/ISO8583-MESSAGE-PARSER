package com.malinga.ISO8583Parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean("prototype")
    public Logger logger(){
        return LoggerFactory.getLogger(AopProxyUtils.ultimateTargetClass(this));
    }
}
