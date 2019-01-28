package com.myron.reporthelper.config.shiro;

import com.google.common.collect.Maps;
import com.myron.reporthelper.config.properties.ConfigProperties;
import com.myron.reporthelper.spring.shiro.filter.AjaxFormAuthenticationFilter;
import com.myron.reporthelper.spring.shiro.filter.MembershipFilter;
import com.myron.reporthelper.spring.shiro.security.MyShiroRealm;
import com.myron.reporthelper.spring.shiro.security.RetryLimitHashedCredentialsMatcher;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.Filter;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Myron Miao
 * @date 2018-12-27
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class ShiroConfig {
    @Autowired
    private ConfigProperties configProperties;

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        final ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        shiroFilterFactoryBean.setLoginUrl("/member/login");
        shiroFilterFactoryBean.setSuccessUrl("/home/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/error/401");

        final Map<String, Filter> filters = Maps.newHashMap();
        filters.put("authc", this.authcFilter());
        filters.put("membership", this.membershipFilter());
        shiroFilterFactoryBean.setFilters(filters);

        final Map<String, String> chains = Maps.newLinkedHashMap();
        chains.put("/member/logout", "logout");
        chains.put("/", this.configProperties.getShiro().getFilters());
        chains.put("/home/**", this.configProperties.getShiro().getFilters());
        chains.put("/views/**", this.configProperties.getShiro().getFilters());
        chains.put("/rest/**", this.configProperties.getShiro().getFilters());
        chains.put("/**", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chains);

        return shiroFilterFactoryBean;
    }

    @Bean
    public AjaxFormAuthenticationFilter authcFilter() {
        final AjaxFormAuthenticationFilter authcFilter = new AjaxFormAuthenticationFilter();
        authcFilter.setUsernameParam("username");
        authcFilter.setPasswordParam("password");
        authcFilter.setRememberMeParam("rememberMe");
        authcFilter.setFailureKeyAttribute("shiroLoginFailure");
        return authcFilter;
    }

    @Bean
    public MembershipFilter membershipFilter() {
        return new MembershipFilter();
    }

    @Bean
    public SecurityManager securityManager() {
        log.info("初始化bean******securityManager******");
        final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager());
        securityManager.setRealm(shiroRealm());

        //将缓存管理器，交给安全管理器
        // securityManager.setCacheManager(cacheManager());
        securityManager.setCacheManager(shiroSpringCacheManager());
        //securityManager.setCacheManager(ehCacheManager());
        //记住密码管理
        securityManager.setRememberMeManager(rememberMeManager());
        //会话管理器

        return securityManager;
    }

    @Bean
    public RetryLimitHashedCredentialsMatcher credentialsMatcher() {
        /*final RetryLimitHashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher(
            cacheManager());
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(2);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;*/


        log.info("初始化bean******credentialsMatcher******");
        final RetryLimitHashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher(shiroSpringCacheManager());
        credentialsMatcher.setRetryLimitCacheName("halfHour");
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(2);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    @Bean
    public MyShiroRealm shiroRealm() {
        /*final MyShiroRealm realm = new MyShiroRealm();
        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;*/

        log.info("初始化bean******shiroRealm******");
        final MyShiroRealm realm = new MyShiroRealm(shiroSpringCacheManager(), credentialsMatcher());

        //realm.setCachingEnabled(true);
        //启用身份验证缓存，即缓存AuthenticationInfo信息，默认false
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthorizationCachingEnabled(true);
        //缓存AuthenticationInfo信息的缓存名称
        realm.setAuthenticationCacheName("authenticationCache");
        //缓存AuthorizationInfo信息的缓存名称
        realm.setAuthorizationCacheName("authorizationCache");

        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;
    }

    @Bean
    public SessionManager sessionManager() {
        final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.getSessionIdCookie().setName("ReportHelper_JSESSIONID");
        return sessionManager;
    }

    @Bean
    public MemoryConstrainedCacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        final DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        final AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager());
        return aasa;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        final SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        final CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCipherKey(Base64.decode("ZUdsaGJuSmxibVI2ZHc9PQ=="));
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }


    //********************************************

    /**
     * 用户授权信息Cache, 采用spring-cache, 具体请查看spring-ehcache.xml
     *
     * @return
     */
    @Bean
    public ShiroSpringCacheManager shiroSpringCacheManager() {
        log.info("初始化bean******shiroSpringCacheManager******");
        ShiroSpringCacheManager shiroSpringCacheManager = new ShiroSpringCacheManager();
        shiroSpringCacheManager.setCacheManager(ehCacheCacheManager());
        return shiroSpringCacheManager;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        log.info("初始化bean******EhCacheCacheManager cacheManager******");
        EhCacheCacheManager ehcacheManager = new EhCacheCacheManager();

        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = ehCacheManagerFactory();
        CacheManager cacheManager = ehCacheManagerFactoryBean.getObject();
        ehcacheManager.setCacheManager(cacheManager);

        ehcacheManager.setTransactionAware(true);

        return ehcacheManager;
    }


    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactory() {
        log.info("初始化bean******ehCacheManagerFactory******");
        ClassPathResource resource = new ClassPathResource("conf/spring/ehcache-shiro.xml");
        //log.info("ehcache-shiro.xml文件是否存在：" + resource.exists());

        InputStream configurationInputStream = null;
        try {
            configurationInputStream = resource.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // CacheManager cacheManager = new CacheManager(configurationInputStream);
        // return cacheManager;
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(resource);

        return ehCacheManagerFactoryBean;

    }


    /**
     * 在方法中 注入  securityManager ，进行代理控制
     *
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        log.info("初始化bean******methodInvokingFactoryBean******");
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(securityManager());
        return methodInvokingFactoryBean;
    }


}
