package com.reporthelper.config.aop;

import com.alibaba.fastjson.JSONObject;
import com.reporthelper.entity.Report;
import com.reporthelper.entity.ReportUseRecord;
import com.reporthelper.entity.User;
import com.reporthelper.service.ReportService;
import com.reporthelper.service.ReportUseRecordService;
import com.reporthelper.service.UserService;
import com.reporthelper.util.ServletRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 拦截用户访问报表
 * @author Myron
 */
@Slf4j
@Aspect
@Component
public class ReportUseRecordAop {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportUseRecordService reportUseRecordService;

    @Before("execution(public * com.reporthelper.controller.preview.PeportPreviewController.preview(..))")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String> requestParameter = ServletRequestUtil.getRequestParameter(request);
        //指定查询组成报表中的其中一个
        String composeUid = MapUtils.getString(requestParameter, "queryReportComposeUid");

        Object[] args = joinPoint.getArgs();
        if (args.length < 2) {
            return;
        }

        String uid = args[1].toString();

        if (StringUtils.isBlank(uid)) {
            return;
        }

        Report report = reportService.getReportByUid(uid);
        if (report == null) {
            return;
        }

        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();

        String userAccount = principal.toString();
        User user = userService.getUserByAccount(userAccount);
        if (user == null) {
            return;
        }

        ReportUseRecord reportUseRecord = ReportUseRecord.builder().reportId(report.getId())
                .composeUid(composeUid)
                .userId(user.getId()).parameter(JSONObject.toJSONString(requestParameter)).build();
        reportUseRecordService.save(reportUseRecord);
    }


}
