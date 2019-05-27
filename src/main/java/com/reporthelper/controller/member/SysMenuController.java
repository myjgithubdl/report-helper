package com.reporthelper.controller.member;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reporthelper.annotation.CurrentUser;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.bo.EasyUITreeNode;
import com.reporthelper.entity.SysMenu;
import com.reporthelper.entity.User;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.SysMenuService;
import com.reporthelper.util.DataGridPager;
import com.reporthelper.util.QueryWrapperOrderUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Validated
@RestController
@RequestMapping("/rest/member/menu/")
public class SysMenuController {

    @Autowired
    SysMenuService service;


    @GetMapping(value = "/getSysMenuTree")
    @OpLog(name = "获取系统模块树型列表")
    @RequiresPermissions("membership.menu:view")
    public ResponseResult getSysMenuTree() {
        final List<SysMenu> menus = this.service.list();
        final List<EasyUITreeNode<SysMenu>> roots = this.service.getSysMenuTree(menus, x -> x.getStatus() < 2);
        return ResponseResult.success(roots);
    }

    @GetMapping(value = "/list")
    @OpLog(name = "获取系统模块树型列表")
    @RequiresPermissions("membership.menu:view")
    public Map<String, Object> list(final DataGridPager dataGridPager, final Integer id) {
        final int pid = (id == null ? 0 : id);
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<SysMenu> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("pid", pid);

        QueryWrapperOrderUtil.setOrderBy(queryWrapper,dataGridPager);

        service.page(page, queryWrapper);
        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @PostMapping(value = "/add")
    @OpLog(name = "新增系统模块")
    @RequiresPermissions("membership.menu:add")
    public ResponseResult add(@CurrentUser final User loginUser, final SysMenu po) {
        po.setHasChild(0);
        po.setPath("");

        po.setCreateDate(new Date());
        po.setCreateUser(loginUser.getId());

        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());

        this.service.save(po);
        return ResponseResult.success("");
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "编辑指定ID的系统模块")
    @RequiresPermissions("membership.menu:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final SysMenu po) {

        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());
        this.service.updateById(po);
        return ResponseResult.success("");
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "邮件指定ID的系统模块")
    @RequiresPermissions("membership.menu:remove")
    public ResponseResult remove(@CurrentUser final User loginUser, final Integer id, final Integer pid) {
        this.service.remove(id, pid);
        return ResponseResult.success("删除成功");
    }

    @GetMapping(value = "/getSysMenu")
    @OpLog(name = "获取指定ID系统模块信息")
    @RequiresPermissions("membership.menu:view")
    public SysMenu getSysMenu(final Integer id) {
        return this.service.getById(id);
    }

    @GetMapping(value = "/getChildSysMenus")
    @OpLog(name = "获取子模块树型列表")
    @RequiresPermissions("membership.menu:view")
    public List<EasyUITreeNode<SysMenu>> getChildSysMenus(final Integer id) {
        final int parentId = (id == null ? 0 : id);
        final List<SysMenu> menus = this.service.getChildren(parentId);
        final List<EasyUITreeNode<SysMenu>> treeNodes = new ArrayList<>(menus.size());
        for (final SysMenu po : menus) {
            final String mid = Integer.toString(po.getId());
            final String pid = Integer.toString(po.getPid());
            final String text = po.getName();
            final EasyUITreeNode<SysMenu> node = new EasyUITreeNode<>(mid, pid, text, "closed", po.getIcon(), false, po);
            treeNodes.add(node);
        }
        return treeNodes;
    }

    @PostMapping(value = "/move")
    @OpLog(name = "移动模块树型关系")
    @RequiresPermissions("membership.menu:edit")
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

    @GetMapping(value = "/rebuildPath")
    @OpLog(name = "重新模块树路径")
    @RequiresPermissions("membership.menu:edit")
    public ResponseResult rebuildPath() {
        this.service.rebuildAllPath();
        return ResponseResult.success("");
    }


}

