package com.jobportal.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        // Ignore null values while updating
        mapper.getConfiguration().setSkipNullEnabled(true);

        return mapper;
    }
}