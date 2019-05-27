package com.reporthelper.spring.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.reporthelper.consts.UserAuthConsts;
import com.reporthelper.entity.User;
import com.reporthelper.spring.security.MembershipFacade;
import com.reporthelper.spring.security.MembershipFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义的Shiro Filter,用于处理用户是否登录
 *
 * @author MyromMiao
 * @date 2019-03-25
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
