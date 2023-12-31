package com.api.parkingcontrol.configs;

import com.api.parkingcontrol.services.MyBean;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }

    @Bean
    public ModelMapper modelMapper(){
        return modelMapper();
    }
}
