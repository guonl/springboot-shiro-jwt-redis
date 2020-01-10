package com.guonl.shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class ShiroConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisProperties.getHost() + ":" + redisProperties.getPort());
        redisManager.setDatabase(redisProperties.getDatabase());
        redisManager.setTimeout(500);
        return redisManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO(RedisManager redisManager){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setExpire(30 * 60);
        sessionDAO.setKeyPrefix("shiro:session:");
        sessionDAO.setRedisManager(redisManager);
        return sessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO);
        return sessionManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisManager redisManager){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setPrincipalIdFieldName("id");
        redisCacheManager.setKeyPrefix("shiro:cache:");
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    /**
     * 自定义Realm
     */
    public UserRealm userRealm(RedisCacheManager redisCacheManager) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCacheManager(redisCacheManager);
        return userRealm;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(DefaultWebSessionManager sessionManager,
                                                     RedisCacheManager redisCacheManager,
                                                     UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * Shiro过滤器配置
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 身份认证失败，则跳转到登录页面的配置
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 权限认证失败，则跳转到指定页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");
        // Shiro连接约束配置，即过滤链的定义
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 对静态资源设置匿名访问
        // 退出 logout地址，shiro去清除session
//        filterChainDefinitionMap.put("/logout", "logout");
        // 不需要拦截的访问
        filterChainDefinitionMap.put("/api-login", "anon");
        //swagger-ui不需要拦截
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/swagger/**","anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**", "anon");
        //admin-api接口不需要拦截
        filterChainDefinitionMap.put("/admin/**","anon");//系统对外暴露接口
        filterChainDefinitionMap.put("/expand/**","anon");//渠道拓展
        filterChainDefinitionMap.put("/health/**","anon");//健康检查

        // 系统权限列表
        // filterChainDefinitionMap.putAll(SpringUtils.getBean(IMenuService.class).selectPermsAll());

//        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
//        // 注销成功，则跳转到指定页面
//        filters.put("logout", logoutFilter());
//        shiroFilterFactoryBean.setFilters(filters);

        // 所有请求需要认证
        filterChainDefinitionMap.put("/**", "user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 退出过滤器
     */
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setRedirectUrl("/logout");
        return logoutFilter;
    }




}
