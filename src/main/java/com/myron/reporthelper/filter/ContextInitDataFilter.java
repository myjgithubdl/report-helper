package com.myron.reporthelper.filter;


import com.myron.reporthelper.consts.AppEnvConsts;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * ServletContext 初始化数据 Filter
 *
 * @author Myron Miao
 * @date 2018-12-25
 */
public class ContextInitDataFilter implements Filter {
    private String version = AppEnvConsts.VERSION;
    private String envName = AppEnvConsts.ENV_NAME;
    private String appName = AppEnvConsts.APP_NAME;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.appName = filterConfig.getInitParameter(AppEnvConsts.APP_NAME_ITEM);
        this.version = filterConfig.getInitParameter(AppEnvConsts.VERSION_ITEM);
        this.envName = filterConfig.getInitParameter(AppEnvConsts.ENV_NAME_ITEM);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        this.setAppEnvAttributes((HttpServletRequest)request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private void setAppEnvAttributes(final HttpServletRequest request) {
        if (request.getAttribute(AppEnvConsts.CONTEXT_PATH) == null) {
            request.setAttribute(AppEnvConsts.CONTEXT_PATH, request.getContextPath());
        }
        if (request.getAttribute(AppEnvConsts.APP_NAME_ITEM) == null) {
            request.setAttribute(AppEnvConsts.APP_NAME_ITEM, this.appName);
            AppEnvConsts.setAppName(this.appName);
        }
        if (request.getAttribute(AppEnvConsts.VERSION_ITEM) == null) {
            request.setAttribute(AppEnvConsts.VERSION_ITEM, this.version);
            AppEnvConsts.setVersion(this.version);
        }
        if (request.getAttribute(AppEnvConsts.ENV_NAME_ITEM) == null) {
            request.setAttribute(AppEnvConsts.ENV_NAME_ITEM, this.envName);
            AppEnvConsts.setEnvName(this.envName);
        }
    }
}
