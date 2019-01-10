package com.myron.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.SysMenu;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.service.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Predicate;

/**
 * 用户权限服务外观类
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
@Service
public class MembershipFacadeServiceImpl implements MembershipFacadeService {
    @Resource
    private UserService userService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysMenuService menuService;
    @Resource
    private PermissionService permissionService;

    public MembershipFacadeServiceImpl() {
    }

    @Override
    public void loadCache() {
        this.permissionService.reloadCache();
    }

    @Override
    public List<EasyUITreeNode<SysMenu>> getSysMenuTree(final List<SysMenu> modules, final Predicate<SysMenu> predicate) {
        return this.menuService.getSysMenuTree(modules, predicate);
    }

    @Override
    public User getUser(final String account) {
        return this.userService.getUserByAccount(account);
    }

    @Override
    public String getRoleNames(final String roleIds) {
        return this.roleService.getNames(roleIds);
    }

    @Override
    public Set<String> getRoleSet(final String roleIds) {
        final String[] roleIdSplit = StringUtils.split(roleIds, ',');
        if (roleIdSplit == null || roleIdSplit.length == 0) {
            return Collections.emptySet();
        }

        final Set<String> roleSet = new HashSet<>(roleIdSplit.length);
        for (final String roleId : roleIdSplit) {
            if (!roleSet.contains(roleId.trim())) {
                roleSet.add(roleId);
            }
        }
        return roleSet;
    }

    @Override
    public Set<String> getPermissionSet(final String roleIds) {
        final String permissionIds = this.roleService.getPermissionIds(roleIds);
        if (StringUtils.isBlank(permissionIds)) {
            return Collections.emptySet();
        }

        final Map<String, String> permissionMap = this.permissionService.getIdCodeMap();
        final String[] permissionIdSplit = StringUtils.split(permissionIds, ',');
        final Set<String> permSet = new HashSet<>();
        for (final String permId : permissionIdSplit) {
            final String perm = permissionMap.get(StringUtils.trim(permId));
            if (StringUtils.isNotBlank(perm)) {
                permSet.add(perm);
            }
        }
        return permSet;
    }

    @Override
    public boolean hasPermission(final String roleIds, final String... codes) {
        if (this.isAdministrator(roleIds)) {
            return true;
        }

        if (StringUtils.isBlank(roleIds) || ArrayUtils.isEmpty(codes)) {
            return false;
        }

        final String permissionIds = this.roleService.getPermissionIds(roleIds);
        if (StringUtils.isBlank(permissionIds)) {
            return false;
        }

        final String[] permissionIdSplit = StringUtils.split(permissionIds, ',');
        final String codePermissionIds = this.permissionService.getPermissionIds(codes);
        final String[] codePermissionIdSplit = StringUtils.split(codePermissionIds, ',');

        return this.hasPermission(codePermissionIdSplit, permissionIdSplit);
    }

    private boolean hasPermission(final String[] codePermissionIdSplit, final String[] permissionIdSplit) {
        if (codePermissionIdSplit == null || permissionIdSplit == null) {
            return false;
        }

        for (final String permId : codePermissionIdSplit) {
            if (!ArrayUtils.contains(permissionIdSplit, permId)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isAdministrator(final String roleIds) {
        if (StringUtils.isBlank(roleIds)) {
            return false;
        }
        return this.roleService.isSuperAdminRole(roleIds);
    }

    @Override
    public List<SysMenu> getSysMenus(final String roleIds) {
        QueryWrapper  wrapper = new QueryWrapper();
        if (this.isAdministrator(roleIds)) {
            wrapper.orderByAsc("pid","sequence","name");
            return this.menuService.list(wrapper);
        }
        final String menuIds = this.roleService.getSysMenuIds(roleIds);
        if (StringUtils.isEmpty(menuIds)) {
            return null;

        }
        List<String> ids = Arrays.asList(menuIds.split(","));
        wrapper.in("id", ids);
        wrapper.orderByAsc("pid","sequence","name");
        return this.menuService.list(wrapper);
    }
}
