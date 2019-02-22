package com.myron.reporthelper.spring.aop;

import com.myron.reporthelper.consts.UserAuthConsts;
import com.myron.reporthelper.entity.SysEvent;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.service.SysEventService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 *
 * @author Myron Miao
 * @date 2018-12-28
 **/
@Slf4j
@Aspect
@Component
public class CustomOpLogAspect extends OpLogAspect {
    @Resource
    private SysEventService eventService;

    @Override
    protected void logEvent(final EventParameter eventParameter) {
        final HttpServletRequest req =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        final User user = (User)req.getAttribute(UserAuthConsts.CURRENT_USER);
        if (user != null) {
            final SysEvent event = SysEvent.builder()
                .source(eventParameter.getSource())
                .account(user.getAccount())
                .userId(user.getId())
                .message(eventParameter.toString())
                .level(eventParameter.getLevel())
                .url(req.getRequestURL().toString())
                .createDate(new Date())
                .build();
            this.eventService.save(event);
        }
    }
}

