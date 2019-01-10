package com.myron.reporthelper.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.Conf;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.ConfService;
import com.myron.reporthelper.util.DataGridPager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 报表配置控制器
 *
 * @author Myron Miao
 * @date 2017-03-25
 */
@RestController
@RequestMapping(value = "/rest/report/conf")
public class ConfController {

    @Autowired
    ConfService service;

    @RequestMapping(value = "/list")
    @OpLog(name = "获取指定ID的报表元数据配置项")
    @RequiresPermissions("report.conf:view")
    public Map<String, Object> list(@CurrentUser final User loginUser,
                                    final DataGridPager dataGridPager,
                                    final Integer id) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<Conf> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        queryWrapper.select(("id,pid,name,`key`,value,sequence,comment,create_user,create_date,update_user,update_date").replaceAll(" ", "").split(","));

        if (id != null) {
            queryWrapper.eq("pid", id);
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

    @RequestMapping(value = "/listChildren")
    @OpLog(name = "获取指定ID的所有子报表元数据配置项")
    @RequiresPermissions("report.conf:view")
    public List<EasyUITreeNode<Conf>> listChildren(final Integer id) {
        final List<Conf> list = this.service.getByParentId(id == null ? 0 : id);
        final List<EasyUITreeNode<Conf>> easyUITreeNodes = new ArrayList<>(list.size());
        for (final Conf po : list) {
            final String confId = Integer.toString(po.getId());
            final String pid = Integer.toString(po.getPid());
            final String text = po.getName();
            final String state = po.isHasChild() ? "closed" : "open";
            final String icon = po.isHasChild() ? "icon-dict2" : "icon-item1";
            EasyUITreeNode<Conf> node = new EasyUITreeNode<>(confId, pid, text, state, icon, false, po);

            node.getChildren().addAll(listChildren(po.getId()));

            easyUITreeNodes.add(node);
        }
        return easyUITreeNodes;
    }

    @RequestMapping(value = "/find")
    @OpLog(name = "分页查找指定ID的报表元数据配置项")
    @RequiresPermissions("report.conf:view")
    public Map<String, Object> find(final DataGridPager dataGridPager, final String keyword) {
        Page<Conf> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());
        QueryWrapper queryWrapper = new QueryWrapper();

        this.service.page(page);
        final Map<String, Object> modelMap = new HashMap<>(2);

        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @RequestMapping(value = "/add")
    @OpLog(name = "新增报表元数据配置项")
    @RequiresPermissions("report.conf:add")
    public ResponseResult add(@CurrentUser final User loginUser, final Conf po) {
        po.setCreateUser(loginUser.getId());
        po.setCreateDate(new Date());
        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        this.service.save(po);
        return ResponseResult.success("");
    }

    @RequestMapping(value = "/edit")
    @OpLog(name = "编辑报表元数据配置项")
    @RequiresPermissions("report.conf:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final Conf po) {
        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());
        this.service.updateById(po);
        return ResponseResult.success("");
    }

    @RequestMapping(value = "/remove")
    @OpLog(name = "删除报表元数据配置项")
    @RequiresPermissions("report.conf:remove")
    public ResponseResult remove(final Integer id) {
        this.service.removeById(id);
        return ResponseResult.success("");
    }

    @GetMapping(value = "/getConfItems")
    @OpLog(name = "获取指定父key下的所有配置项")
    @RequiresPermissions("report.conf:view")
    public ResponseResult getConfItems(final String key) {
        return ResponseResult.success(this.service.selectByParentKey(key));
    }
}