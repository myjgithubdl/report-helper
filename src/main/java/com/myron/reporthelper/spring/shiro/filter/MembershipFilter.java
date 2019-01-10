package com.myron.reporthelper.spring.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.myron.reporthelper.consts.UserAuthConsts;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.spring.security.MembershipFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义的Shiro Filter,用于处理用户是否登录
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
@Slf4j
public class MembershipFilter extends PathMatchingFilter {
    @Resource
    private MembershipFacade<User> membershipFacade;

    @Override
    protected boolean onPreHandle(final ServletRequest request, final ServletResponse response,
                                  final Object mappedValue)
            throws Exception {
        final String account = (String) SecurityUtils.getSubject().getPrincipal();
        User user = this.membershipFacade.getUser(account);
        log.info("user：" + JSONObject.toJSONString(user));
        request.setAttribute(UserAuthConsts.CURRENT_USER, user);
        return true;
    }
}
