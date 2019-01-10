package com.myron.reporthelper.spring.shiro.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户登录重试凭证限制
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
// public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher implements InitializingBean {
   /* private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        AtomicInteger retryCount = passwordRetryCache.get(username);

        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }

        if (retryCount.incrementAndGet() > 10) {
            throw new ExcessiveAttemptsException("您重试密码超过10次,账号已被锁定!");
        }

        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            passwordRetryCache.remove(username);
        }
        return matches;
    }*/


    private final static Logger logger = LogManager.getLogger(RetryLimitHashedCredentialsMatcher.class);
    private final static String DEFAULT_CHACHE_NAME = "retryLimitCache";

    private final CacheManager cacheManager;
    private String retryLimitCacheName;
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.retryLimitCacheName = DEFAULT_CHACHE_NAME;
    }

    public String getRetryLimitCacheName() {
        return retryLimitCacheName;
    }
    public void setRetryLimitCacheName(String retryLimitCacheName) {
        this.retryLimitCacheName = retryLimitCacheName;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        String username = (String) authcToken.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if(retryCount.incrementAndGet() > 10) {
            //if retry count > 5 throw
            logger.warn("username: " + username + " tried to login more than 10 times in period");
            throw new ExcessiveAttemptsException("用户名: " + username + " 密码连续输入错误超过10次，锁定半小时！");
        }

        boolean matches = super.doCredentialsMatch(authcToken, info);
        if(matches) {
            //clear retry data
            passwordRetryCache.remove(username);
        }
        return matches;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.passwordRetryCache = cacheManager.getCache(retryLimitCacheName);
    }
}
