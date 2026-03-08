## 实现功能
1.搭建shiro框架，实现简单认证授权功能  
2.自定义过滤器`RolesOrAuthorizationFilter` 实现灵活的授权校验
```java
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
```
3.默认登录session信息存储在cookie中，对于单机服务没有问题，如果服务部署在多台机器上会出现问题。因此实现`RedisSessionDao`
```java
@Component
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    @Resource
    private RedisTemplate redisTemplate;

    private final String SHIOR_SESSION = "session:";

//    private ConcurrentMap<Serializable, Session> sessions = new ConcurrentHashMap();

    public RedisSessionDAO() {
    }

    protected Serializable doCreate(Session session) {}

    protected Session storeSession(Serializable id, Session session) {}

    protected Session doReadSession(Serializable sessionId) {}

    public void update(Session session) throws UnknownSessionException {}

    public void delete(Session session) {}

    public Collection<Session> getActiveSessions() {}
}
```
4.授权操作频繁查询数据库，mysql压力大，因此使用redis缓存优化
```java
    @Bean
    public DefaultWebSecurityManager securityManager(MyRealm myRealm,SessionManager sessionManager,RedisCacheManager redisCacheManager){
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        log.info("====securityManager注册完成====");
        defaultSecurityManager.setRealm(myRealm);
        defaultSecurityManager.setCacheManager(redisCacheManager);
        defaultSecurityManager.setSessionManager(sessionManager);
        return defaultSecurityManager;
    }
```

5.密码不能明文存储，数据库加盐。因此需要重写密码匹配逻辑
```java
public class MyRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    /**
     * 设置密码匹配器
     */
    {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher("MD5");
        matcher.setHashIterations(1);
        matcher.setStoredCredentialsHexEncoded(true);
        this.setCredentialsMatcher(matcher);
    }
    //==============================================
}
```

6.至于单点登录。分为两种方式
中心化方式：CAS + shiro
去中心化方式 JWT + shiro