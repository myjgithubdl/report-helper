package com.myron.reporthelper.service.impl;

import com.myron.reporthelper.entity.SysEvent;
import com.myron.reporthelper.mapper.SysEventMapper;
import com.myron.reporthelper.service.SysEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Service
public class SysEventServiceImpl extends ServiceImpl<SysEventMapper, SysEvent> implements SysEventService {


    @Override
    public void add(final String source, final String account, final String message, final String level,
                    final String url) {
        final SysEvent event = SysEvent.builder()
                .source(source)
                .account(account)
                .userId(-1)
                .message(message)
                .level(level)
                .url(url)
                .createDate(new Date())
                .build();
        this.save(event);
    }
}
