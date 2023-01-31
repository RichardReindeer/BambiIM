package com.bambi.imserver.handler;

import com.bambi.imcommon.cocurrent.FutureTaskScheduler;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.session.ServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *      <b> 空闲检测 </b><br>
 *      每间隔一段时间检测子通道是否有数据读写，如果没有则判断IO通道处于假死状态<br>
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/11 15:01    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class HeartBeatHandler extends IdleStateHandler  {
    private static Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    // 暂时写死一个最大空闲时间
    // private static final int READ_IDLE_TIME = 120;
    private static final int READ_IDLE_TIME = 3000;
    public HeartBeatHandler() {
        super(READ_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("HeartBeatHandler is channelReading !!!");
        // 判空
        if(!(msg instanceof ProtoMessage.Message) || msg == null){
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMessage.Message proMsg = (ProtoMessage.Message) msg;
        ProtoMessage.HeadType type = proMsg.getType();
        if(type.equals(ProtoMessage.HeadType.HEART_BEAT)){
            // 如果类型是心跳类型，则将心跳信息发送给客户端
            FutureTaskScheduler.add(()->{
                if(ctx.channel().isActive()){
                    ctx.writeAndFlush(msg);
                }
            });
        }

        // 一定要调用父类的channelRead , 否则idle的入栈检测会失效
        super.channelRead(ctx, msg);
    }

    /**
     * 发生假死的处理逻辑
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.debug("channelIdle is starting !!!");
        logger.info(READ_IDLE_TIME +" 秒内没有收到信息，正在关闭链接");
        ServerSession.closeSession(ctx);
    }
}
