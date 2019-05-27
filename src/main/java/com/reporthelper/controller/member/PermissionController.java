package com.reporthelper.controller.member;


import com.reporthelper.annotation.CurrentUser;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.entity.Permission;
import com.reporthelper.entity.User;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.PermissionService;
import com.reporthelper.util.DataGridPager;
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
 * @author Myron
 * @since 2018-12-27
 */
@Validated
@RestController
@RequestMapping("/rest/member/permission/")
public class PermissionController {

    @Autowired
    PermissionService service;

    @GetMapping(value = "/list")
    @OpLog(name = "获取权限列表")
    @RequiresPermissions("membership.permission:view")
    public Map<String, Object> list(@CurrentUser final User loginUser, final DataGridPager dataGridPager,
                                    final Integer id) {
        final int menuId = (id == null ? 0 : id);

        final Map<String, Object> modelMap = new HashMap<>(2);
        Map<String, Object> params = new HashMap<>();
        params.put("menuId", menuId);

        int count = service.getReportCount(params);
        List<Map<String, Object>> list = null;
        if (count > 0) {

            params.put("pageSize", dataGridPager.getRows());
            params.put("startRowIndex", (dataGridPager.getPage() - 1) * dataGridPager.getRows());

            list = service.getReportList(params);
        }

        modelMap.put("total", count);
        modelMap.put("rows", list == null ? new ArrayList<>() : list);
        return modelMap;
    }

    @PostMapping(value = "/add")
    @OpLog(name = "增加权限")
    @RequiresPermissions("membership.permission:add")
    public ResponseResult add(@CurrentUser final User loginUser, final Permission permission) {
        permission.setCreateDate(new Date());
        permission.setCreateUser(loginUser.getId());

        permission.setUpdateUser(loginUser.getId());
        permission.setUpdateDate(new Date());
        this.service.save(permission);
        this.service.reloadCache();
        return ResponseResult.success("保存成功");
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "修改权限")
    @RequiresPermissions("membership.permission:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final Permission permission) {
        permission.setUpdateUser(loginUser.getId());
        permission.setUpdateDate(new Date());
        this.service.updateById(permission);
        this.service.reloadCache();
        return ResponseResult.success("保存成功");
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除权限")
    @RequiresPermissions("membership.permission:remove")
    public ResponseResult remove(final Integer id) {
        this.service.removeById(id);
        this.service.reloadCache();
        return ResponseResult.success("");
    }

}

