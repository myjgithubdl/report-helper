package com.reporthelper.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.ReportHistoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表修改历史表 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@RestController
@RequestMapping(value = "/rest/report/history")
public class ReportHistoryController {


    @Resource
    private ReportHistoryService reportHistoryService;

    @RequestMapping(value = "/list")
    @OpLog(name = "查看报表版本历史")
    @RequiresPermissions("report.designer:view")
    public ResponseResult list(final Integer reportId) {
        final Map<String, Object> modelMap = new HashMap<>(2);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("report_id", reportId);
        queryWrapper.orderByDesc("create_date");
        List list = reportHistoryService.list(queryWrapper);

        return ResponseResult.success(list);
    }
}