package com.reporthelper.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Myron Miao
 * @date 2018-12-27
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.env")
public class EnvProperties {
    private String appName;
    private String name;
    private String version;
}
