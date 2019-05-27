package com.reporthelper.spring.shiro.security;

import com.reporthelper.entity.User;
import com.reporthelper.spring.security.MembershipFacade;
import com.reporthelper.spring.security.MembershipFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 系统模块服务类
 *
 * @author Myron
 * @date 2019-03-25
 */
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private MembershipFacade<User> membershipFacade;


    public MyShiroRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        final String account = (String)principals.getPrimaryPrincipal();
        final User user = this.membershipFacade.getUser(account);

        final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roleSet = this.membershipFacade.getRoleSet(user.getSysRoles());
        Set<String> permissionSet = this.membershipFacade.getPermissionSet(user.getSysRoles());
        log.info(account+"角色："+roleSet);
        log.info(account+"权限："+permissionSet);
        authorizationInfo.setRoles(roleSet);
        authorizationInfo.setStringPermissions(permissionSet);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token)
        throws AuthenticationException {
        final String account = (String)token.getPrincipal();
        final User user = this.membershipFacade.getUser(account);

        if (user == null) {
            throw new UnknownAccountException();
        }
        if (user.getStatus() == 0) {
            throw new LockedAccountException();
        }

        // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        return new SimpleAuthenticationInfo(
            user.getAccount(), user.getEncryptPassword(),
            ByteSource.Util.bytes(user.getCredentialsSalt()),
            getName());
    }

    @Override
    public void clearCachedAuthorizationInfo(final PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(final PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(final PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
