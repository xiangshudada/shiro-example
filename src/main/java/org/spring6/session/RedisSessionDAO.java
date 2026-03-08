package org.spring6.session;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */
@Component
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    @Resource
    private RedisTemplate redisTemplate;

    private final String SHIOR_SESSION = "session:";

//    private ConcurrentMap<Serializable, Session> sessions = new ConcurrentHashMap();

    public RedisSessionDAO() {
    }

    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.storeSession(sessionId, session);
        log.info("创建session:{}",sessionId);
        return sessionId;
    }

    protected Session storeSession(Serializable id, Session session) {
        if (id == null) {
            throw new NullPointerException("id argument cannot be null.");
        } else {
            redisTemplate.opsForValue().set(SHIOR_SESSION + id, session, 30, TimeUnit.MINUTES);
            log.info("存储session:{}",id);
            return session;
        }
    }

    protected Session doReadSession(Serializable sessionId) {
        //1. 基于sessionId获取Session （与Redis交互）
        if (sessionId == null) {
            log.info("读取session失败：sessionId不能为空！");
            return null;
        }
        Session session = (Session) redisTemplate.opsForValue().get(SHIOR_SESSION + sessionId);
        if (session != null) {
            redisTemplate.expire(SHIOR_SESSION + sessionId,30,TimeUnit.MINUTES);
        }
        log.info("读取session成功：{}",sessionId);
        return session;
    }

    public void update(Session session) throws UnknownSessionException {
        //1. 修改Redis中session
        if (session == null) {
            log.info("更新session失败：session不能为空！");
            return;
        }
        redisTemplate.opsForValue().set(SHIOR_SESSION + session.getId(), session, 30, TimeUnit.MINUTES);
        log.info("更新session成功：{}",session.getId());
    }

    public void delete(Session session) {
        // 删除Redis中的Session
        if (session == null) {
            log.info("删除session失败：session不能为空！");
            return;
        }
        redisTemplate.delete(SHIOR_SESSION + session.getId());
        log.info("删除session成功：{}",session.getId());
    }

    public Collection<Session> getActiveSessions() {
        Set keys = redisTemplate.keys(SHIOR_SESSION + "*");

        Set<Session> sessionSet = new HashSet<>();
        // 尝试修改为管道操作，pipeline（Redis的知识）
        for (Object key : keys) {
            Session session = (Session) redisTemplate.opsForValue().get(key);
            sessionSet.add(session);
        }
        return sessionSet;
    }
}