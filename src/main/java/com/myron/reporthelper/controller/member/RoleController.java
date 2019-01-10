package com.myron.reporthelper.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.bo.pair.IdNamePair;
import com.myron.reporthelper.entity.SysMenu;
import com.myron.reporthelper.entity.Permission;
import com.myron.reporthelper.entity.SysRole;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.SysMenuService;
import com.myron.reporthelper.service.PermissionService;
import com.myron.reporthelper.service.SysRoleService;
import com.myron.reporthelper.util.DataGridPager;
import com.myron.reporthelper.util.QueryWrapperOrderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom Deng
 * @date 2017-03-25
 */
@RestController
@RequestMapping(value = "/rest/member/role")
public class RoleController {

    @Autowired
    SysRoleService roleService;

    @Resource
    private SysMenuService menuService;
    @Resource
    private PermissionService permissionService;

    @GetMapping(value = "/isSuperAdmin")
    @OpLog(name = "是否为超级管理角色")
    @RequiresPermissions("membership.role:view")
    public ResponseResult isSuperAdmin(@CurrentUser final User loginUser) {
        return ResponseResult.success(this.roleService.isSuperAdminRole(loginUser.getRoles()));
    }

    @GetMapping(value = "/list")
    @OpLog(name = "分页获取角色列表")
    @RequiresPermissions("membership.role:view")
    public Map<String, Object> list(@CurrentUser final User loginUser, final DataGridPager dataGridPager,
                                    final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<SysRole> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("id", keyword);
            queryWrapper.or();
            queryWrapper.like("name", keyword);
            queryWrapper.or();
            queryWrapper.like("code", keyword);
        }

        QueryWrapperOrderUtil.setOrderBy(queryWrapper,dataGridPager);

        roleService.page(page, queryWrapper);
        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @PostMapping(value = "/add")
    @OpLog(name = "增加角色")
    @RequiresPermissions("membership.role:add")
    public ResponseResult add(@CurrentUser final User loginUser, final SysRole po) {
        po.setMenuIds("");
        po.setPermissionIds("");
        po.setCreateUser(loginUser.getId());
        po.setUpdateUser(loginUser.getId());
        po.setCreateDate(new Date());
        po.setUpdateDate(new Date());
        this.roleService.save(po);
        return ResponseResult.success("保存成功");
    }

    @PostMapping(value = "/edit")
    @OpLog(name = "修改角色")
    @RequiresPermissions("membership.role:edit")
    public ResponseResult edit(@CurrentUser final User loginUser, final SysRole po) {
        po.setUpdateUser(loginUser.getId());
        po.setUpdateDate(new Date());
        this.roleService.updateById(po);
        return ResponseResult.success("修改成功");
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除角色")
    @RequiresPermissions("membership.role:remove")
    public ResponseResult remove(final Integer id) {
        this.roleService.removeById(id);
        return ResponseResult.success("删除成功");
    }

    @GetMapping(value = "/getRoleList")
    @OpLog(name = "获取当前的角色列表")
    @RequiresPermissions("membership.role:view")
    public List<IdNamePair> getRoleList(@CurrentUser final User loginUser) {
        final List<IdNamePair> list = new ArrayList<>(10);
        final List<SysRole> roleList = this.roleService.getRolesList(loginUser);
        if (CollectionUtils.isEmpty(roleList)) {
            return list;
        }
        list.addAll(roleList.stream()
                .map(role -> new IdNamePair(String.valueOf(role.getId()), role.getName()))
                .collect(Collectors.toList()));
        return list;
    }

    @PostMapping(value = "/authorize")
    @OpLog(name = "给角色授权")
    @RequiresPermissions("membership.role:authorize")
    public ResponseResult authorize(final SysRole po) {
        po.setPermissionIds(StringUtils.stripEnd(po.getPermissionIds(), ","));
        //po.setModules(this.permissionService.getModuleIds(po.getPermissions()));
        po.setMenuIds(this.permissionService.getSysMenuIds(po.getPermissionIds()));
        this.roleService.updateById(po);
        return ResponseResult.success("");
    }

    @GetMapping(value = "/getRoleById")
    @OpLog(name = "获取指定id的角色信息")
    @RequiresPermissions("membership.role:view")
    public SysRole getRoleById(final Integer id) {
        return this.roleService.getById(id);
    }

    @GetMapping(value = "/listPermissionTree")
    @OpLog(name = "获取当前用户所拥有的权限列表")
    @RequiresPermissions("membership.role:view")
    public List<EasyUITreeNode<String>> listPermissionTree(@CurrentUser final User loginUser,
                                                           final Integer roleId) {
        final Map<String, String[]> roleModuleAndPermissionMap = this.roleService.getRoleSysMenusAndPermissions(roleId);
        if (roleModuleAndPermissionMap == null) {
            return new ArrayList<>(0);
        }
        return this.buildTree(this.getModulePermissions(
                loginUser.getRoles(),
                roleModuleAndPermissionMap.get("modules"),
                roleModuleAndPermissionMap.get("permissions")));
    }

    private List<EasyUITreeNode<String>> getModulePermissions(final String userRoles,
                                                              final String[] moduleSplit,
                                                              final String[] operationSplit) {
        final boolean isSuperAdminRole = this.roleService.isSuperAdminRole(userRoles);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in("id", this.roleService.getSysMenuIds(userRoles));
        final List<SysMenu> modules = isSuperAdminRole
                ? this.menuService.list()
                : this.menuService.list(wrapper);

        final List<EasyUITreeNode<String>> moduleNodes = new ArrayList<>(modules.size());
        moduleNodes.addAll(modules.stream()
                .map(module -> new EasyUITreeNode<>(
                        String.valueOf(-module.getId()),
                        String.valueOf(-module.getPid()),
                        module.getName(), "open", "", false,
                        String.valueOf(module.getId())
                )).collect(Collectors.toList()));

        final List<Permission> permissions = this.permissionService.list();
        final List<EasyUITreeNode<String>> permNodes = new ArrayList<>(permissions.size());
        permNodes.addAll(permissions.stream()
                .map(perm -> new EasyUITreeNode<>(
                        String.valueOf(perm.getId()),
                        String.valueOf(-perm.getMenuId()),
                        perm.getName(), "open", "",
                        ArrayUtils.contains(operationSplit, perm.getId().toString()),
                        String.valueOf(perm.getId())
                )).collect(Collectors.toList()));
        moduleNodes.addAll(permNodes);
        return moduleNodes;
    }

    private List<EasyUITreeNode<String>> buildTree(final Collection<EasyUITreeNode<String>> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return new ArrayList<>(0);
        }

        final List<EasyUITreeNode<String>> rootNodes = new ArrayList<>(20);
        rootNodes.addAll(nodes.stream()
                .filter(treeNode -> "0".equals(treeNode.getPId()))
                .collect(Collectors.toList()));
        for (final EasyUITreeNode<String> rootNode : rootNodes) {
            getChildNodes(nodes, rootNode);
        }
        return rootNodes;
    }

    private void getChildNodes(final Collection<EasyUITreeNode<String>> nodes, final EasyUITreeNode<String> node) {
        final List<EasyUITreeNode<String>> childNodes = new ArrayList<>(nodes.size());
        childNodes.addAll(nodes.stream()
                .filter(treeNode -> treeNode.getPId().equals(node.getId()))
                .collect(Collectors.toList()));
        for (final EasyUITreeNode<String> childNode : childNodes) {
            node.setState("closed");
            node.getChildren().add(childNode);
            getChildNodes(nodes, childNode);
        }
    }

}
