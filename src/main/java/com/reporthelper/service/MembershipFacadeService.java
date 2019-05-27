package com.reporthelper.service;


import com.reporthelper.bo.EasyUITreeNode;
import com.reporthelper.entity.SysMenu;
import com.reporthelper.entity.User;
import com.reporthelper.spring.security.MembershipFacade;

import java.util.List;
import java.util.function.Predicate;

/**
 * 用户权限服务外观类
 *
 * @author Myron
 * @date 2019-03-25
 */
public interface MembershipFacadeService extends MembershipFacade<User> {
    void loadCache();

    List<EasyUITreeNode<SysMenu>> getSysMenuTree(final List<SysMenu> modules, final Predicate<SysMenu> predicate);

    List<SysMenu> getSysMenus(final String roleIds);
}
