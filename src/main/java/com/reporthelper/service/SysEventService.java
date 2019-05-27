package com.reporthelper.service;

import com.reporthelper.entity.SysEvent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Myron
 * @since 2018-12-27
 */
public interface SysEventService extends IService<SysEvent> {


    void add(String source, String account, String message, String level,
             String url);
}
