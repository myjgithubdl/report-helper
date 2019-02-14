package com.myron.reporthelper.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.bo.pair.IdNamePair;
import com.myron.reporthelper.entity.*;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.ReportRoleService;
import com.myron.reporthelper.service.ReportService;
import com.myron.reporthelper.util.DataGridPager;
import com.myron.reporthelper.util.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Myron Miao
 * @since 2019-01-05
 */
@RestController
@RequestMapping("/rest/member/reportRole")
public class ReportRoleController {

    @Autowired
    ReportRoleService reportRoleService;

    @Autowired
    ReportService reportService;


    @RequestMapping(value = "/list")
    @OpLog(name = "报表角色列表")
    @RequiresPermissions("report.role:view")
    public Map<String, Object> list(final DataGridPager dataGridPager, final String categoryId, final String queryKeyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<PageInfo> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.isNotEmpty(categoryId)) {
            params.put("categoryId", categoryId);
        }

        if (StringUtils.isNotEmpty(queryKeyword)) {
            params.put("queryKeyword", queryKeyword);
        }

        int count = reportRoleService.getReportRoleCount(params);
        List<Map<String, Object>> list = null;
        if (count > 0) {

            params.put("pageSize", dataGridPager.getRows());
            params.put("startRowIndex", (dataGridPager.getPage() - 1) * dataGridPager.getRows());

            list = reportRoleService.getReportRoleList(params);
        }

        modelMap.put("total", count);
        modelMap.put("rows", list);
        return modelMap;
    }


    @PostMapping(value = "/add")
    @OpLog(name = "新增报表角色")
    @RequiresPermissions("report.role:add")
    public ResponseResult add(@CurrentUser final User loginUser, final ReportRole po) {
        po.setCreateUser(loginUser.getId());
        po.setCreateDate(new Date());

        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        this.reportRoleService.save(po);
        return ResponseResult.success("保存成功");
    }


    @PostMapping(value = "/edit")
    @OpLog(name = "修改报表")
    @RequiresPermissions("report.role:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final ReportRole po) {
        ReportRole dbReportRole = reportRoleService.getById(po.getId());

        dbReportRole.setName(po.getName());
        dbReportRole.setStatus(po.getStatus());
        dbReportRole.setSequence(po.getSequence());
        dbReportRole.setComment(po.getComment());

        dbReportRole.setUpdateUser(loginUser.getId());
        dbReportRole.setUpdateDate(new Date());

        this.reportRoleService.updateById(dbReportRole);

        return ResponseResult.success("修改成功");
    }

    @PostMapping(value = "/authorize")
    @OpLog(name = "修改报表")
    @RequiresPermissions("report.role:edit")
    public ResponseResult authorize(@CurrentUser final User loginUser, final ReportRole po) {
        ReportRole dbReportRole = reportRoleService.getById(po.getId());

        dbReportRole.setUpdateUser(loginUser.getId());
        dbReportRole.setUpdateDate(new Date());
        dbReportRole.setReportIds(po.getReportIds());

        this.reportRoleService.updateById(dbReportRole);
        return ResponseResult.success("修改成功");
    }


    @PostMapping(value = "/remove")
    @OpLog(name = "删除报表")
    @RequiresPermissions("report.role:remove")
    public ResponseResult remove(final Integer id) {
        this.reportRoleService.removeById(id);
        return ResponseResult.success("删除成功");
    }


    @GetMapping(value = "/getReportRoleList")
    @OpLog(name = "获取报表角色列表")
    public List<IdNamePair> getReportRoleList(@CurrentUser final User loginUser) {
        final List<IdNamePair> list = new ArrayList<>(10);
        QueryWrapper<ReportRole> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sequence", "id");
        List<ReportRole> roleList = this.reportRoleService.list(wrapper);
        if (CollectionUtils.isEmpty(roleList)) {
            return list;
        }
        list.addAll(roleList.stream()
                .map(role -> new IdNamePair(String.valueOf(role.getId()), role.getName()))
                .collect(Collectors.toList()));
        return list;
    }

    @GetMapping(value = "/getAllCategoryAndReport")
    public ResponseResult getAllCategoryAndReport(final Integer reportRoleId) {
        List<Map<String, Object>> categoryAndReports = this.reportService.getAllCategoryAndReport();
        final List<EasyUITreeNode<Map<String, Object>>> roots = new ArrayList<>();

        ReportRole reportRole = reportRoleService.getById(reportRoleId);
        List<String> reportIds = StringUtils.isNotEmpty(reportRole.getReportIds()) ? Arrays.asList(reportRole.getReportIds().split(",")) : new ArrayList<>();
        categoryAndReports.stream().filter(map -> MapUtils.getInteger(map, "pid") == 0).forEach(map -> {
            final String type = MapUtils.getString(map, "type");
            final String name = MapUtils.getString(map, "name");
            final String id = type + "-" + MapUtils.getString(map, "id");
            final String iconCls = "category".equals(type) ? "icon-categories" : "icon-category";

            final EasyUITreeNode<Map<String, Object>> parentNode = new EasyUITreeNode<>(id, name, "open", map);
            parentNode.setIconCls(iconCls);

            this.setChildCategoryAndReportTrees(categoryAndReports, parentNode, reportIds);
            roots.add(parentNode);

        });

        return ResponseResult.success(roots);
    }


    private void setChildCategoryAndReportTrees(final List<Map<String, Object>> categoryAndReports,
                                                final EasyUITreeNode<Map<String, Object>> parentNode, List<String> selectedReportIds) {
        final String parentNodeId = parentNode.getId();
        System.out.println(parentNodeId);
        categoryAndReports.stream().filter(map -> parentNodeId.equals("category-" + MapUtils.getString(map, "pid")) || parentNodeId.equals("report-" + MapUtils.getString(map, "pid"))).forEach(map -> {

            final String type = MapUtils.getString(map, "type");
            final String name = MapUtils.getString(map, "name");
            final String pid = MapUtils.getString(map, "pid");
            final String id = type + "-" + MapUtils.getString(map, "id");
            final String iconCls = "category".equals(type) ? "icon-categories" : "icon-category";

            final EasyUITreeNode<Map<String, Object>> childNode = new EasyUITreeNode<>(id, name, "open", map);
            childNode.setIconCls(iconCls);
            if ("report".equals(type) && selectedReportIds.indexOf(MapUtils.getString(map, "id")) >= 0) {
                childNode.setChecked(true);
            }
            this.setChildCategoryAndReportTrees(categoryAndReports, childNode, selectedReportIds);
            parentNode.getChildren().add(childNode);

        });


    }

}

