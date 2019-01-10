package com.myron.reporthelper.controller.report;

import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.util.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 报表修改历史表 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Controller
@RequestMapping("/reportHistory")
public class ReportHistoryController {
    @RequestMapping(value = "/list")
    @OpLog(name = "查看报表版本历史")
    @RequiresPermissions("report.designer:view")
    public Map<String, Object> list(final PageInfo pageInfo, final Integer reportId) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        //final List<ReportHistory> list = this.service.getByPage(pageInfo, reportId == null ? 0 : reportId, null, null);
        //modelMap.put("total", pageInfo.getTotals());
        //modelMap.put("rows", list);
        return modelMap;
    }
}