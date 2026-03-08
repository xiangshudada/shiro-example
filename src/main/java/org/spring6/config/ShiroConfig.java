package org.spring6.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.spring6.cache.RedisCacheManager;
import org.spring6.filter.RolesOrAuthorizationFilter;
import org.spring6.realm.MyRealm;
import org.spring6.session.DefaultRedisWebSessionManager;
import org.spring6.session.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 配置自定义realm、securityManager、过滤器
 * @Version: 1.0
 */

@Configuration
@Slf4j
public class ShiroConfig {


    @Value("#{ @environment['shiro.loginUrl'] ?: '/login.jsp' }")
    protected String loginUrl;

    @Value("#{ @environment['shiro.successUrl'] ?: '/' }")
    protected String successUrl;

    @Value("#{ @environment['shiro.unauthorizedUrl'] ?: null }")
    protected String unauthorizedUrl;

    @Bean
    public SessionManager sessionManager(RedisSessionDAO sessionDAO) {
        DefaultRedisWebSessionManager sessionManager = new DefaultRedisWebSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        return sessionManager;
    }


    @Bean
    public DefaultWebSecurityManager securityManager(MyRealm myRealm,SessionManager sessionManager,RedisCacheManager redisCacheManager){
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        log.info("====securityManager注册完成====");
        defaultSecurityManager.setRealm(myRealm);
        defaultSecurityManager.setCacheManager(redisCacheManager);
        defaultSecurityManager.setSessionManager(sessionManager);
        return defaultSecurityManager;
    }

    @Bean
    public DefaultShiroFilterChainDefinition shiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition shiroFilterChainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String,String> filterChainMap = new LinkedHashMap<>();
        // 配置可以匿名访问的地址，可以根据实际情况自己添加，放行一些静态资源等，anon表示放行
        filterChainMap.put("/login.html","anon");
        filterChainMap.put("/register.html","anon");
        filterChainMap.put("/user/logout","logout");
        filterChainMap.put("/user/**","anon");
        filterChainMap.put("/item/rememberMe","user");
        filterChainMap.put("/item/authentication","authc");
        filterChainMap.put("/item/select","rolesOr[超级管理员,运营]");
        filterChainMap.put("/item/delete","perms[item:delete,item:insert]");
        filterChainMap.put("/**","authc");
        shiroFilterChainDefinition.addPathDefinitions(filterChainMap);
        return shiroFilterChainDefinition;
    }

    /**
     * 注入Shiro过滤器
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,DefaultShiroFilterChainDefinition shiroFilterChainDefinition) {
        // 定义shiroFactoryBean
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
        // 设置自定义的securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 设置默认登录的url，身份认证失败会访问该url
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        // 设置成功之后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl(successUrl);
        // 设置未授权界面，权限认证失败会访问该url
        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());

        //5. 设置自定义过滤器 ， 这里一定要手动的new出来这个自定义过滤器，如果使用Spring管理自定义过滤器，会造成无法获取到Subject
        shiroFilterFactoryBean.getFilters().put("rolesOr",new RolesOrAuthorizationFilter());

        log.info("====shiroFilterFactoryBean注册完成====");
        return shiroFilterFactoryBean;
    }
}
