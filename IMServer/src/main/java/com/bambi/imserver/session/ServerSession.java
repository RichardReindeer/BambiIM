package com.bambi.imserver.session;

import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 描述：
 *     胶水类，负责衔接User与Channel，从而实现Server Socket对话<br>
 *     使用 <b>SessionID</b> 来当作唯一标识<br>
 *     因为项目设计需要考虑到用户使用多端登录 , 如 win端、mac、安卓、IOS、Linux等，相同用户不同端也会持有 <b>相同的UserID</b><br>
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/22 1:15    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class ServerSession {
    private static final Logger logger = LoggerFactory.getLogger(ServerSession.class);

    // 存储Session关系于Channel中，需要使用AttributeKey - Attribute的结构
    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("server_session_key");

    // 充当session唯一标识
    private String sessionID;

    // 彼此衔接的数据
    private User user;
    private Channel channel;

    private boolean isLogin  = false;
    /**
     *  正向绑定
     * @param channel
     */
    public ServerSession(Channel channel) {
        this.channel = channel;
        sessionID = buildSessionID();
    }

    /**
     * 关闭连接
     *
     * @param ctx
     */
    public static void closeSession(ChannelHandlerContext ctx) {
        logger.debug("closeSession is starting !!!");

        ServerSession serverSession = ctx.channel().attr(ServerSession.SESSION_KEY).get();
        if (serverSession!=null && serverSession.isValid()) {
            serverSession.close();
            SessionMap.getInstance().removeSessionID(serverSession.getSessionID());
        }
    }

    private boolean isValid() {
        return getUser() != null? true:false;
    }

    /**
     * 反向绑定 | 核心逻辑
     * TODO 编写SessionMap 用来存储各个客户端的Session连接
     * @return
     */
    public ServerSession reverseBind(){
        logger.debug("reverseBind is starting !!!");
        logger.debug("当前Session绑定会话: ,{}",channel.remoteAddress());
        channel.attr(ServerSession.SESSION_KEY).set(this);
        SessionMap.getInstance().addSession(this);
        isLogin = true;
        return this;
    }

    /**
     * 解除绑定
     * 将登录状态置为false<br>
     * 并在SessionMap 中移除当前的SessionID<br>
     * 调用close函数关闭连接
     * @return 当前的Session
     */
    public ServerSession unBind(){
        isLogin = false;
        // 在SessionMap 中移除当前的SessionID
        SessionMap.getInstance().removeSessionID(getSessionID());
        this.close();
        return this;
    }

    private void close() {
        logger.debug("close(ServerSession) is starting !!!");

        ChannelFuture close = channel.close();
        close.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(!channelFuture.isSuccess()){
                    logger.error("CHANNEL_CLOSED  ERROR !!!");
                }
            }
        });
    }

    /**
     * 向通道内写入Protobuf的数据帧
     * 当系统水位过高的时候不应该继续发送信息，防止出现 <b>队列积压</b>
     * 使用Netty中 channel.isWritable() 函数来判断当前通道是否可以写入当前信息
     * @param message
     */
    public void writeAndFlush(Object message){
        logger.debug("writeAndFlush is starting !!!");

        if(channel.isWritable()){
            channel.writeAndFlush(message);
        }else{
            logger.debug("出现消息挤压，信息被暂存");
            // TODO 可以使用分布式数据库 ， mongoDB等进行消息的存储 , 等channel 空闲之后再进行写入
        }

    }

    /**
     * 至于如何制作一个SessionID充当唯一标识 , 暂时使用UUID的random逻辑获取，并将其内部的所有"-" 删去
     */
    private String buildSessionID(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-","");
    }


    public AttributeKey<ServerSession> getSESSION_KEY() {
        return SESSION_KEY;
    }


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public User getUser() {
        return user;
    }

    /**
     * 在对User进行数据设置的时候，需要绑定当前的sessionID
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        user.setSessionID(sessionID);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
