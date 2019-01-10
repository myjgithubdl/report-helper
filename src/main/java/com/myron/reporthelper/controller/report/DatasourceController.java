package com.myron.reporthelper.controller.report;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.entity.Datasource;
import com.myron.reporthelper.entity.Permission;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.DatasourceService;
import com.myron.reporthelper.util.DataGridPager;
import com.myron.reporthelper.util.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据源配置信息表 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@RestController
@RequestMapping(value = "/rest/report/ds")
public class DatasourceController {

    @Autowired
    DatasourceService service;

    @GetMapping(value = "/listAll")
    @OpLog(name = "获取所有数据源")
    @RequiresPermissions("report.ds:view")
    public List<Datasource> listAll() {
        return this.service.list().stream()
                .map(x -> Datasource.builder()
                        .id(x.getId())
                        .uid(x.getUid())
                        .name(x.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/list")
    @OpLog(name = "分页获取数据源列表")
    @RequiresPermissions("report.ds:view")
    public Map<String, Object> list(@CurrentUser final User loginUser, final DataGridPager dataGridPager ,final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<Datasource> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        if(StringUtils.isNotEmpty(keyword)){
            queryWrapper.like("name",keyword);
            queryWrapper.or();
            queryWrapper.like("driver_class",keyword);
            queryWrapper.or();
            queryWrapper.like("jdbc_url",keyword);
            queryWrapper.or();
            queryWrapper.like("user",keyword);
            queryWrapper.or();
            queryWrapper.like("queryer_class",keyword);
            queryWrapper.or();
            queryWrapper.like("pool_class",keyword);
        }

        if ("asc".equals(dataGridPager.getSort())) {
            queryWrapper.orderByAsc(dataGridPager.getOrder());
        }

        if ("desc".equals(dataGridPager.getSort())) {
            queryWrapper.orderByDesc(dataGridPager.getOrder());
        }

        service.page(page, queryWrapper);
        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @RequestMapping(value = "/add")
    @OpLog(name = "新增数据源")
    @RequiresPermissions("report.ds:add")
    public ResponseResult add(@CurrentUser final User loginUser, final Datasource po) {
        po.setCreateUser(loginUser.getId());
        po.setCreateDate(new Date());
        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        po.setUid(UUID.randomUUID().toString());
        this.service.save(po);
        return ResponseResult.success("");
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "编辑数据源")
    @RequiresPermissions("report.ds:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final Datasource po) {

        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());
        this.service.updateById(po);
        return ResponseResult.success("");
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除数据源")
    @RequiresPermissions("report.ds:remove")
    public ResponseResult remove(final Integer id) {
        this.service.removeById(id);
        return ResponseResult.success("");
    }

    @PostMapping(value = "/testConnection")
    @OpLog(name = "测试数据源")
    @RequiresPermissions("report.ds:view")
    public ResponseResult testConnection(final String driverClass, final String url, final String pass,
                                         final String user) {
        if (this.service.testConnection(driverClass, url, user, pass)) {
            return ResponseResult.success("");
        }
        return ResponseResult.error("数据源测试失败", null);
    }

    @PostMapping(value = "/testConnectionById")
    @OpLog(name = "测试数据源")
    @RequiresPermissions("report.ds:view")
    public ResponseResult testConnection(final Integer id) {
        final Datasource dsPo = this.service.getById(id);
        final boolean testResult = this.service.testConnection(
                dsPo.getDriverClass(),
                dsPo.getJdbcUrl(),
                dsPo.getUser(), dsPo.getPassword());

        if (testResult) {
            return ResponseResult.success("");
        }
        return ResponseResult.error("数据源测试失败", null);
    }
}

