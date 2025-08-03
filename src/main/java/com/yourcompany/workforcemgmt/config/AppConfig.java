package com.yourcompany.workforcemgmt.config;

import com.yourcompany.workforcemgmt.mapper.ITaskManagementMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ITaskManagementMapper taskManagementMapper() {
        return Mappers.getMapper(ITaskManagementMapper.class);
    }
}
