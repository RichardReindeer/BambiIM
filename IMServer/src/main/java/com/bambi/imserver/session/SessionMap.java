package com.bambi.imserver.session;

import com.bambi.imcommon.common.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 描述：
 *      Session存储集合
 *      服务器需要存储多个客户端的不同Session，使用SessionID当作唯一标识
 *      因为整个服务器只需要一个SessionMap，且未来可能会成为大类，所以采用单例模式进行创建
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/26 22:35    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class SessionMap {
    private static Logger logger = LoggerFactory.getLogger(SessionMap.class);

    /**
     * 单例开始
     */
    private SessionMap(){}
    private static SessionMap INSTANCE = new SessionMap();
    public static SessionMap getInstance(){
        return INSTANCE;
    }
    /**
     * 单例结束
     */

    private ConcurrentHashMap<String,ServerSession> sessionMap = new ConcurrentHashMap<String,ServerSession>();
    /**
     * 接受一个ServerSession并在Map中增加对应Session对象
     * @param serverSession
     */
    public void addSession(ServerSession serverSession){
        logger.debug("addSession is starting !!!");
        sessionMap.put(serverSession.getSessionID(),serverSession);
        logger.info("用户 {} 登录   | 当前在线总数 : {}",serverSession.getUser().getUid(),sessionMap.size());
    }

    /**
     * 根据SessionID删除容器中对应的Session
     * @param sessionID
     */
    public void removeSessionID(String sessionID){
        logger.debug("removeSessionID is starting !!!");

        if(!sessionMap.containsKey(sessionID)){
            return;
        }
        ServerSession serverSession = sessionMap.get(sessionID);
        sessionMap.remove(sessionID);
        logger.info("用户 {} 下线 | 当前在线人数 {}",serverSession.getUser().getUid(),sessionMap.size());
    }

    /**
     * 根据用户ID获取其所有的SessionID <br>
     * (因为同一个用户ID可能存在不同平台的SessionID，所以返回List)<br>
     * @param userId
     * @return
     */
    public List<ServerSession> getSessionByUserId(String userId){
        logger.info("getSessionByUserID is starting!!!");

        List<ServerSession> sessionList = sessionMap.values()
                .stream()
                .filter(serverSession -> serverSession.getUser().getUid().equals(userId))
                .collect(Collectors.toList());
        return sessionList;
    }

    public ServerSession getSession(String sessionID){
        logger.info("getSession is starting !!!");

        if(sessionMap.containsKey(sessionID)){
            return sessionMap.get(sessionID);
        }else {
            return null;
        }
    }

    /**
     * 判断当前用户是否已经登录
     *
     * @return
     */
    public boolean hasLogin(User user) {
        logger.debug("hasLogin is starting !!!!");
        Iterator<Map.Entry<String, ServerSession>> iterator = sessionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ServerSession> next = iterator.next();
            User u = next.getValue().getUser();
            if (u.getUid().equals(user.getUid())
                    && u.getPlatForm().equals(user.getPlatForm())) {
                return true;
            }
        }

        return false;
    }




    /**
     * getter & setter
     */
    public ConcurrentHashMap<String, ServerSession> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(ConcurrentHashMap<String, ServerSession> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
