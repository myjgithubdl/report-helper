package com.myron.reporthelper.service;


import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.SysMenu;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.spring.security.MembershipFacade;

import java.util.List;
import java.util.function.Predicate;

/**
 * 用户权限服务外观类
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
public interface MembershipFacadeService extends MembershipFacade<User> {
    void loadCache();

    List<EasyUITreeNode<SysMenu>> getSysMenuTree(final List<SysMenu> modules, final Predicate<SysMenu> predicate);

    List<SysMenu> getSysMenus(final String roleIds);
}
