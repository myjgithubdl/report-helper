package com.myron.reporthelper.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 自定义配置类
 *
 * @author Myron Miao
 * @date 2018-12-27
 **/
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "application")
public class ConfigProperties {

    @Valid
    private final Shiro shiro = new Shiro();

    public static class Shiro {

        private String filters;

        public String getFilters() {
            return this.filters;
        }

        public void setFilters(final String filters) {
            this.filters = filters;
        }
    }
}
