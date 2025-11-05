package com.ruoyi.flowable.core;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 引擎基础配置
 */
@Configuration
@ConditionalOnClass(SpringProcessEngineConfiguration.class)
public class FlowableConfiguration
{
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> globalProcessEngineConfigurer()
    {
        return configuration -> {
            configuration.setActivityFontName("宋体");
            configuration.setAnnotationFontName("宋体");
            configuration.setLabelFontName("宋体");
        };
    }
}
