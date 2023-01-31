package com.bambi.imserver.handler;

import com.bambi.imcommon.cocurrent.FutureTaskScheduler;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.processer.impl.ChatRedirectProcesser;
import com.bambi.imserver.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 描述：
 *      聊天信息转发处理器
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/27 15:14    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("chatRedirectHandler")
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ChatRedirectHandler.class);

    @Autowired
    private ChatRedirectProcesser chatRedirectProcesser;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.error("ChatRedirectHandler is reading!!!");
        if(msg == null || !(msg instanceof ProtoMessage.Message)){
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMessage.Message protoMsg = (ProtoMessage.Message) msg;
        ProtoMessage.HeadType type = ((ProtoMessage.Message) msg).getType();
        if(!type.equals(chatRedirectProcesser.type())){
            super.channelRead(ctx, msg);
            return;
        }

        ServerSession serverSession = ctx.channel().attr(ServerSession.SESSION_KEY).get();
        if(serverSession == null || !serverSession.isLogin()){
            logger.error("用户没有登录，请重新登录");
            return;
        }

        //使用异步来处理IM的转发逻辑
        FutureTaskScheduler.add(()->{
            chatRedirectProcesser.action(serverSession,protoMsg);
        });

    }
}
