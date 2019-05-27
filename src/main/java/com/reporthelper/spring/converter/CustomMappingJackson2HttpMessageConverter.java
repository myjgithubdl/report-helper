package com.reporthelper.spring.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * JsonResult<T> 序列化输出转换类
 *
 * @author Myron Miao
 * @date 2018-12-28
 **/
@Slf4j
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public CustomMappingJackson2HttpMessageConverter() {
        log.debug("load {}", this.getClass().getName());
    }

    @Override
    protected void writeInternal(final Object object, final Type type, final HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        super.writeInternal(object, type, outputMessage);
    }

    @Override
    protected void writeInternal(final Object object, final HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        this.writeInternal(object, null, outputMessage);
    }
}
