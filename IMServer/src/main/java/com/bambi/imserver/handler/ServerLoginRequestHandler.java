package com.bambi.imserver.handler;

import com.bambi.imcommon.cocurrent.CallbackTask;
import com.bambi.imcommon.cocurrent.CallbackTaskScheduler;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.processer.impl.LoginRequestProcesser;
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
 *      服务器登录请求处理器
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/11 16:04    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("ServerLoginRequestHandler")
public class ServerLoginRequestHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ServerProtobufDecoder.class);
    @Autowired
    private LoginRequestProcesser loginRequestProcesser;
    @Autowired
    private ChatRedirectHandler chatRedirectHandler;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("检测到了一个连接，但是还未成功登录 --- 》 {},id -- > {},",ctx.channel().remoteAddress(),ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 数据校验 : 判空
        if(msg == null || !(msg instanceof ProtoMessage.Message)){
            super.channelRead(ctx, msg);
            logger.info("不是Message类型的信息");
            return;
        }

        read0(ctx,msg);
    }

    /**
     * 获取请求类型，判断请求类型是否为登录请求
     * 使用context获取channel，创建对应的serverSession
     * 异步任务逻辑
     * @param context
     * @param msg
     */
    private void read0(ChannelHandlerContext context, Object msg) throws Exception {
        ProtoMessage.Message message = (ProtoMessage.Message)msg;

        ProtoMessage.HeadType headType = message.getType();
        if(!headType.equals(loginRequestProcesser.type())) {
            super.channelRead(context, msg);
            return;
        }

        ServerSession serverSession = new ServerSession(context.channel());

        // 使用自建线程池来处理耗时操作
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                boolean action = loginRequestProcesser.action(serverSession, message);
                return action;
            }

            @Override
            public void onBack(Boolean aBoolean) {
                if(aBoolean){
                    context.pipeline().addAfter("login","chat",chatRedirectHandler);
                    context.pipeline().addAfter("login","heartBeat",new HeartBeatHandler());
                    context.pipeline().remove("login");
                    logger.info("登录成功！！！");
                    logger.debug("remove loginHandler");
                }else{
                    ServerSession.closeSession(context);
                    logger.error("登录失败 {}",serverSession.getUser());
                }
            }

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(context);
                logger.error("登录失败{}",serverSession.getUser());
            }
        });
    }
}
