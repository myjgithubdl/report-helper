package com.reporthelper.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easydata.head.TheadColumn;
import com.reporthelper.annotation.CurrentUser;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.entity.*;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.ReportComposeHistoryService;
import com.reporthelper.service.ReportComposeService;
import com.reporthelper.service.ReportHistoryService;
import com.reporthelper.service.ReportService;
import com.reporthelper.util.DataGridPager;
import com.reporthelper.util.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 报表设计器
 * Myron Miao
 */
@RestController
@RequestMapping(value = "/rest/report/designer")
public class ReportController {
    @Resource
    private ReportHistoryService reportHistoryService;

    @Resource
    private ReportService reportService;

    @Resource
    private ReportComposeService reportComposeService;

    @Resource
    private ReportComposeHistoryService reportComposeHistoryService;

    @RequestMapping(value = "/list")
    @OpLog(name = "分页获取报表列表")
    @RequiresPermissions("report.designer:view")
    public Map<String, Object> list(final DataGridPager dataGridPager, final String categoryId, final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<PageInfo> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.isNotEmpty(categoryId)) {
            params.put("categoryId", categoryId);
        }

        if (StringUtils.isNotEmpty(keyword)) {
            params.put("likeKeyword", keyword);
        }

        int count = reportService.getReportCount(params);
        List<Map<String, Object>> list = null;
        if (count > 0) {
            params.put("pageSize", dataGridPager.getRows());
            params.put("startRowIndex", (dataGridPager.getPage() - 1) * dataGridPager.getRows());

            list = reportService.getReportList(params);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (Map<String , Object> report : list) {
                Integer reportId = MapUtils.getInteger(report,"id",0);
                List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(reportId);
                report.put("reportComposeList",reportComposeList);
            }
        }

        modelMap.put("total", count);
        modelMap.put("rows", list == null ? new ArrayList<>() : list);
        return modelMap;
    }

    @GetMapping(value = "/find")
    @OpLog(name = "分页查询报表")
    @RequiresPermissions("report.designer:view")
    public Map<String, Object> find(final PageInfo pager, final String fieldName, final String keyword) {
        //final PageInfo pageInfo = this.getPageInfo(pager);
        // final List<Report> list = this.reportService.getByPage(pageInfo, "t1." + fieldName, "%" + keyword + "%");
        final Map<String, Object> modelMap = new HashMap<>(2);
        //  modelMap.put("total", pageInfo.getTotals());
        //  modelMap.put("rows", list);
        return modelMap;
    }

/*    @GetMapping(value = "/getAll")
    @OpLog(name = "获取所有报表")
    @RequiresPermissions("report.designer:view")
    public List<IdNamePair> getAll(@CurrentUser final User loginUser) {
        final List<Report> reportList = this.reportService.list();
        if (CollectionUtils.isEmpty(reportList)) {
            return new ArrayList<>(0);
        }

        final List<IdNamePair> list = new ArrayList<>(reportList.size());
        list.addAll(reportList.stream()
                .map(report -> new IdNamePair(String.valueOf(report.getId()), report.getName()))
                .collect(Collectors.toList()));
        return list;
    }*/

    @PostMapping(value = "/add")
    @OpLog(name = "新增报表")
    @RequiresPermissions("report.designer:add")
    public ResponseResult add(@CurrentUser final User loginUser, final Report po) {
        po.setUid(UUID.randomUUID().toString());
        po.setCreateUser(loginUser.getId());
        po.setCreateDate(new Date());

        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        this.reportService.save(po);
        Integer reportId = po.getId();
        List<ReportCompose> reportComposeList = po.getReportComposeList();
        reportComposeList.stream().forEach(r -> r.setReportId(reportId));

        reportComposeService.saveBatch(reportComposeList);

        this.saveReportHis(loginUser, po);

        return ResponseResult.success("添加成功");
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "修改报表")
    @RequiresPermissions("report.designer:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final Report po) {
        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        this.reportService.updateById(po);

        Integer reportId = po.getId();
        reportComposeService.deleteByReportId(reportId);

        List<ReportCompose> reportComposeList = po.getReportComposeList();
        reportComposeList.stream().forEach(r -> r.setReportId(reportId));

        reportComposeService.saveBatch(reportComposeList);

        this.saveReportHis(loginUser, po);

        return ResponseResult.success("修改成功");
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除报表")
    @RequiresPermissions("report.designer:remove")
    public ResponseResult remove(final Integer id) {
        this.reportService.removeById(id);
        this.reportComposeService.deleteByReportId(id);
        return ResponseResult.success("");
    }

    /**
     * 保存历史
     *
     * @param loginUser
     * @param po
     */
    private void saveReportHis(User loginUser, Report po) {
        List<ReportCompose> reportComposeList = po.getReportComposeList();
        //保存报表历史
        ReportHistory reportHistory = this.getReportHistory(loginUser, po);
        this.reportHistoryService.save(reportHistory);
        Integer hisId = reportHistory.getId();

        List<ReportComposeHistory> reportComposeHistoryList = new ArrayList<>(reportComposeList.size());
        for (ReportCompose reportCompose : reportComposeList) {
            ReportComposeHistory reportComposeHistory = this.getReportComposeHistory(loginUser, hisId, reportCompose);
            reportComposeHistoryList.add(reportComposeHistory);
        }
        //保存报表组成历史
        reportComposeHistoryService.saveBatch(reportComposeHistoryList);
    }

    private ReportComposeHistory getReportComposeHistory(@CurrentUser User loginUser, Integer reportHistoryId, ReportCompose po) {
        ReportComposeHistory reportComposeHistory = new ReportComposeHistory();
        BeanUtils.copyProperties(po, reportComposeHistory, "id");
        reportComposeHistory.setReportHisId(reportHistoryId);
        return reportComposeHistory;
    }


    @PostMapping(value = "/execSqlText")
    @OpLog(name = "获取报表元数据列集合")
    @RequiresPermissions("report.designer:view")
    public ResponseResult execSqlText(final Integer dsId, String sqlText, Integer dataRange,
                                      final String queryParams, final HttpServletRequest request) {
        if (dsId != null) {
            if (dataRange == null) {
                dataRange = 7;
            }
            //sqlText = this.getSqlText(sqlText, dataRange, queryParams, request);
            try {
                List<TheadColumn> list = this.reportService.getMetaDataColumns(dsId, sqlText);
                return ResponseResult.success(list);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.error("查询SQL报错");
            }
        }
        return ResponseResult.error("没有选择数据源");
    }

    @PostMapping(value = "/previewSqlText")
    @OpLog(name = "预览报表SQL语句")
    @RequiresPermissions("report.designer:view")
    public ResponseResult previewSqlText(final Integer dsId, String sqlText, Integer dataRange,
                                         final String queryParams, final HttpServletRequest request) {
        if (dsId != null) {
            if (dataRange == null) {
                dataRange = 7;
            }
            //sqlText = this.getSqlText(sqlText, dataRange, queryParams, request);
            // this.reportService.explainSqlText(dsId, sqlText);
            return ResponseResult.success(sqlText);
        }
        return ResponseResult.error("没有选择数据源");
    }

    @GetMapping(value = "/getMetaColumnScheme")
    @OpLog(name = "获取报表元数据列结构")
    @RequiresPermissions("report.designer:view")
    public TheadColumn getMetaColumnScheme() {
        final TheadColumn column = new TheadColumn();
        column.setName("");
        column.setDataType("");
        return column;
    }


    private ReportHistory getReportHistory(@CurrentUser final User loginUser, final Report po) {
        return ReportHistory.builder()
                .reportId(po.getId())
                .categoryId(po.getCategoryId())
                .dsId(po.getDsId())
                .createUser(loginUser.getId())
                .updateUser(loginUser.getId())
                .comment(po.getComment())
                .name(po.getName())
                .uid(po.getUid())
                .metaColumns(po.getMetaColumns())
                .queryParams(po.getQueryParams())
                .options(po.getOptions())
                .reportExplain(po.getReportExplain())
                .sqlText(po.getSqlText())
                .status(po.getStatus())
                .sequence(po.getSequence())
                .createDate(new Date())
                .updateDate(new Date())
                .build();
    }
}