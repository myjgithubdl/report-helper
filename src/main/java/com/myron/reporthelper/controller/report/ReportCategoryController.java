package com.myron.reporthelper.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.Datasource;
import com.myron.reporthelper.entity.Report;
import com.myron.reporthelper.entity.ReportCategory;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.ReportCategoryService;
import com.myron.reporthelper.service.ReportService;
import com.myron.reporthelper.util.DataGridPager;
import com.myron.reporthelper.util.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 报表类别表 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@RestController
@RequestMapping("/rest/report/category")
public class ReportCategoryController {

    @Autowired
    ReportCategoryService service;

    @Autowired
    ReportService reportService;

    @GetMapping(value = "/getCategoryTree")
    @OpLog(name = "获取报表分类树")
    @RequiresPermissions("report.designer:view")
    public ResponseResult getCategoryTree() {
        final List<EasyUITreeNode<ReportCategory>> roots = new ArrayList<>();
        final List<ReportCategory> categories = this.service.list();
        categories.stream().filter(category -> category.getPid() == 0).forEach(category -> {
            final String cateId = Integer.toString(category.getId());
            final String text = category.getName();
            final String state = category.getHasChild() ? "closed" : "open";
            final String iconCls = category.getHasChild() ? "icon-categories" : "icon-category";
            final EasyUITreeNode<ReportCategory> parentNode = new EasyUITreeNode<>(cateId, text, state, category);
            parentNode.setIconCls(iconCls);
            this.getChildCategories(categories, parentNode);
            roots.add(parentNode);
        });
        return ResponseResult.success(roots);
    }

    private void getChildCategories(final List<ReportCategory> categories, final EasyUITreeNode<ReportCategory> parentNode) {
        final int id = Integer.valueOf(parentNode.getId());
        categories.stream().filter(category -> category.getPid() == id).forEach(category -> {
            final String cateId = Integer.toString(category.getId());
            final String text = category.getName();
            final String state = category.getHasChild() ? "closed" : "open";
            final String iconCls = category.getHasChild() ? "icon-categories" : "icon-category";
            final EasyUITreeNode<ReportCategory> childNode = new EasyUITreeNode<>(cateId, text, state, category);
            childNode.setIconCls(iconCls);
            this.getChildCategories(categories, childNode);
            parentNode.getChildren().add(childNode);
        });
    }

    @GetMapping(value = "/list")
    @OpLog(name = "分页查找报表分类")
    @RequiresPermissions("report.designer:view")
    public Map<String, Object> list(final DataGridPager dataGridPager, final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<ReportCategory> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("id", keyword);
            queryWrapper.or();
            queryWrapper.like("name", keyword);
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

    @PostMapping(value = "/add")
    @OpLog(name = "增加报表分类")
    @RequiresPermissions("report.designer:add")
    public ResponseResult add(final ReportCategory po) {
        po.setHasChild(false);
        po.setPath("");
        po.setCreateDate(new Date());
        po.setUpdateDate(new Date());
        this.service.save(po);
        return ResponseResult.success(po);
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "编辑报表分类")
    @RequiresPermissions("report.designer:edit")
    public ResponseResult edit(final ReportCategory po) {
        this.service.updateById(po);
        return ResponseResult.success(po);
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除报表分类")
    @RequiresPermissions("report.designer:remove")
    public ResponseResult remove(final Integer id, final Integer pid) {
        this.service.remove(id, pid);
        return ResponseResult.success(pid);
    }

    @PostMapping(value = "/move")
    @OpLog(name = "移动报表分类")
    @RequiresPermissions("report.designer:edit")
    public ResponseResult move(final Integer sourceId, final Integer targetId, final Integer sourcePid,
                               final String sourcePath) {
        this.service.move(sourceId, targetId, sourcePid, sourcePath);
        return ResponseResult.success(new HashMap<String, Integer>(3) {
            {
                put("sourceId", sourceId);
                put("targetId", targetId);
                put("sourcePid", sourcePid);
            }
        });
    }

    @PostMapping(value = "/paste")
    @OpLog(name = "复制与粘贴报表分类")
    @RequiresPermissions("report.designer:edit")
    public ResponseResult paste(final Integer sourceId, final Integer targetId) {
        final List<EasyUITreeNode<ReportCategory>> nodes = new ArrayList<>(1);
        final ReportCategory po = this.service.paste(sourceId, targetId);
        final String id = Integer.toString(po.getId());
        final String pid = Integer.toString(po.getPid());
        final String text = po.getName();
        final String state = po.getHasChild() ? "closed" : "open";
        nodes.add(new EasyUITreeNode<>(id, pid, text, state, "", false, po));
        return ResponseResult.success(nodes);
    }

    @GetMapping(value = "/rebuildPath")
    @OpLog(name = "重新构建分类树路径")
    @RequiresPermissions("report.designer:edit")
    public ResponseResult rebuildPath() {
        this.service.rebuildAllPath();
        return ResponseResult.success("");
    }

    @GetMapping(value = "/listAll")
    @OpLog(name = "查看报表分类")
    @RequiresPermissions("report.designer:view")
    public ResponseResult listAll() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("sequence", "id");
        return ResponseResult.success(this.service.listMaps(queryWrapper));
    }


}

