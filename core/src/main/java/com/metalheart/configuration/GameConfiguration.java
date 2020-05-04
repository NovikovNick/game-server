package com.metalheart.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.Set;

@Configuration
@ComponentScan(basePackages = {
        "com.metalheart.converter",
        "com.metalheart.repository",
        "com.metalheart.service",
        "com.metalheart.server",
        "com.metalheart.client"
})
public class GameConfiguration {

    @Autowired
    private Set<Converter> converters;

    @Bean
    public ConversionService getConversionService() {
        GenericConversionService conversionService = new DefaultConversionService();
        converters.forEach(conversionService::addConverter);
        return conversionService;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public GameProperties getApplicationProperties() {
        return new GameProperties();
    }
}
