package com.reporthelper.config.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reporthelper.spring.converter.CustomMappingJackson2HttpMessageConverter;
import com.reporthelper.spring.resolver.CurrentUserMethodArgumentResolver;
import com.reporthelper.spring.resolver.ResponseBodyWrapFactoryBean;
import com.reporthelper.spring.converter.CustomMappingJackson2HttpMessageConverter;
import com.reporthelper.spring.resolver.CurrentUserMethodArgumentResolver;
import com.reporthelper.spring.resolver.ResponseBodyWrapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Myron Miao
 * @date 2018-12-27
 **/
@Configuration
//public class WebMvcConfig extends WebMvcConfigurerAdapter {
//public class WebMvcConfig extends WebMvcAutoConfiguration {
public class WebMvcConfig implements WebMvcConfigurer {
//public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(messageConverter());
    }

    /**
     * 解析参数
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver());
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Bean
    public CustomMappingJackson2HttpMessageConverter messageConverter() {
        final CustomMappingJackson2HttpMessageConverter converter = new CustomMappingJackson2HttpMessageConverter();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        //max file size 10M
        multipartResolver.setMaxUploadSize(10 * 1024 * 1024);
        return multipartResolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        //保存7天有效
        localeResolver.setCookieMaxAge(604800);
        localeResolver.setDefaultLocale(Locale.CHINA);
        localeResolver.setCookieName("locale");
        localeResolver.setCookiePath("/");
        return localeResolver;
    }

    @Bean
    public HandlerMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }

    @Bean
    public ResponseBodyWrapFactoryBean getResponseBodyWrap() {
        return new ResponseBodyWrapFactoryBean();
    }
}

