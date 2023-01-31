package com.bambi.imclient.sender;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述：
 *      sender基类(抽象)
 *      含有sendMsg默认实现，通过Session获取对应Channel , 将数据信息写入到Channel中发送
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/8 13:55    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
public abstract class BaseSender {
    private static final Logger logger = LoggerFactory.getLogger(BaseSender.class);

    private User user;
    private ClientSession clientSession; // 获取用户会话信息， 根据会话信息可以获取当前对应的channel ， 并判断是否成功连接等

    /**
     * 根据Session 的isConnected判断客户端是否与服务器成功连接
     *
     * @return 是否成功连接
     */
    public boolean isConnected(){
        if(null == clientSession){
            logger.error("clientSession 为空，无法获取用户会话信息");
        }
        return clientSession.isConnected();
    }

    /**
     * 是否成功登录判断
     * @return
     */
    public boolean isLogin(){
        if(null == clientSession){
            logger.error("clientSession 为空，无法获取用户会话信息");
            return false;
        }
        return clientSession.isLogin();
    }

    /**
     * 将protobuf数据装入channel中发送
     * 需要对clientSession 进行非空判断
     * @param message
     */
    public void sendMessage(ProtoMessage.Message message){
        if(null == clientSession || !isConnected()){
            logger.error("clientSession 为空，无法获取用户会话信息");
            return;
        }

        Channel channel = getClientSession().getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        // 添加信息发送后的事件监听
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    sendSuccess(message);
                }else {
                    sendFailed(message);
                }
            }
        });
    }

    protected void sendFailed(ProtoMessage.Message message) {
        logger.info("----> 信息发送失败");
    }

    protected void sendSuccess(ProtoMessage.Message message) {
        logger.info("----> 信息发送成功");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClientSession getClientSession() {
        return clientSession;
    }

    public void setClientSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }
}
