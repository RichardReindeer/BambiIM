package com.bambi.imclient.handler;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.ProtoInstant;
import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 * 登录逻辑处理器<br>
 * 检测数据是否为Message，并检验HeadType类型<br>
 * 符合条件后将clientSession设置为isSuccess= true<br>
 * 并利用Handler的热插拔特性，将登录业务处理器删除，并加入心跳处理器<br>
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 15:33    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("LoginResponseHandler")
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoginResponseHandler.class);

    @Autowired
    private HeartBeatClientHandler heartBeatClientHandler;
    @Autowired
    private ChatMessageHandler chatMessageHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.error("LoginResponseHandler is starting !!!!!!!");

        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMessage.Message)) {
            logger.debug("数据msg为空");
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMessage.Message message = (ProtoMessage.Message) msg;
        ProtoMessage.HeadType type = message.getType();
        if (!type.equals(ProtoMessage.HeadType.LOGIN_RESPONSE)) {
            // 向下传递
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMessage.LoginResponse loginResponse = message.getLoginResponse();
        // 通过ProtoInstant中定义的枚举值，与登录响应核对，确定登录结果

        ProtoInstant.ResultCodeEnum value =
                ProtoInstant.ResultCodeEnum.values()[loginResponse.getCode()];
        if (!value.equals(ProtoInstant.ResultCodeEnum.SUCCESS)) {
            // 登录失败
            logger.error(value.getDesc());
        } else {
            //登录成功，移除登录处理器，并添加心跳处理器
            // 利用pipeline的处理器热插拔逻辑
            ClientSession.loginSuccess(ctx, message);
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.remove(this);  // 移除登录响应处理器
            pipeline.addAfter("encoder", "heartBeat", heartBeatClientHandler);
            pipeline.addAfter("encoder","chat",chatMessageHandler);
            heartBeatClientHandler.channelActive(ctx);
        }
    }
}
