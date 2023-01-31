package com.bambi.imclient.session;

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
 * 实现客户端Session
 * 胶水类，两个成员: user 和 channel ;
 * 通过user 可以获取当前用户信息
 * 通过channel , 可以向服务器端发送信息
 * <p>
 * 存在两个状态:
 * 1.是否成功连接isConnected
 * 2.是否成功登录isLogin
 * <p>
 * 因为ClientSession绑定在了channel上，所以可以在入站处理的时候，通过channel反向取得绑定的Session，从而获取user的信息
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 16:16    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
public class ClientSession {
    private static final Logger logger = LoggerFactory.getLogger(ClientSession.class);
    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    //会话管理类核心
    private Channel channel;
    private User user;

    private boolean isConnected = false;
    private boolean isLogin = false;

    // 保存登录成功之后服务器端的SessionID
    private String sessionID;

    public ClientSession(Channel channel) {
        this.channel = channel;
        this.sessionID = UUID.randomUUID().toString();

        // 反向绑定
        // 将当前Session会话存储在Channel中
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    /**
     * 登录成功逻辑
     *
     * @param context
     * @param message
     */
    public static void loginSuccess(ChannelHandlerContext context, ProtoMessage.Message message) {
        logger.debug("loginSuccess is Starting!!!");
        Channel currentChannel = context.channel();
        ClientSession clientSession = currentChannel.attr(ClientSession.SESSION_KEY).get();
        clientSession.setSessionID(message.getSessionId());
        clientSession.setLogin(true);
        logger.info("恭喜登录成功！！！");
    }

    /**
     * 从通道中根据SessionKEY获取对应Session
     * @param context
     * @return
     */
    public static ClientSession getSession(ChannelHandlerContext context) {
        logger.debug("getSession is starting !!!");
        Channel channel = context.channel();
        ClientSession clientSession = channel.attr(ClientSession.SESSION_KEY).get();
        return clientSession;
    }

    /**
     * 关闭通道
     */
    public void close() {
        ChannelFuture close = channel.close();
        close.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    logger.debug("连接顺利断开");
                }
            }
        });
    }


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
