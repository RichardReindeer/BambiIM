package com.bambi.imclient.handler;

import com.bambi.imclient.protoConverter.HeartBeatConverter;
import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 * <b>设计思路</b><br>
 * 客户端在该Handler加入pipeline的时候就开始发送心跳，在channelActive方法中进行过<br>
 * 心跳采用定时器<b>schedule</b> , 每隔50s发送一次心跳包<br>
 * 该数值可以后期读取配置，但应与服务器端同时更改
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 15:34    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("heartBeatClientHandler")
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(HeartBeatClientHandler.class);
    // 定时时间50s
    private static long HEARTBEAT_DELAYTIME = 50;

    /**
     * 在handler进入pipeline时触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        User user = session.getUser();
        HeartBeatConverter heartBeatConverter = new HeartBeatConverter(user, session);
        ProtoMessage.Message build = heartBeatConverter.build();
        heartBeat(ctx, build);
        super.channelActive(ctx);
    }

    /**
     * 发送心跳数据包<br>
     * 采用定时任务中递归调用的方式，将数据包定时发送给服务器
     * @param ctx
     * @param build
     */
    private void heartBeat(ChannelHandlerContext ctx, ProtoMessage.Message build) {
        logger.debug("client's heartBeat is starting!!!");
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                //将心跳包发送给服务器
                logger.debug("开始发送心跳包");
                ctx.writeAndFlush(build);
                // 在此递归调用，从而实现定时发送
                heartBeat(ctx, build);
            }
        }, HEARTBEAT_DELAYTIME, TimeUnit.SECONDS);
    }

    /**
     * 对服务器回写的数据包进行对应处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null || !(msg instanceof ProtoMessage.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMessage.Message protoMsg = (ProtoMessage.Message) msg;
        ProtoMessage.HeadType type = protoMsg.getType();
        if (type.equals(ProtoMessage.HeadType.HEART_BEAT)) {
            logger.info("收到服务器发送的心跳检测信息");
            return;
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
