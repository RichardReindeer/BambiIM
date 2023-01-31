package com.bambi.imclient.handler;

import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 描述:
 * 客户端 聊天逻辑处理器 处理收到信息之后的回显
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/29 3:05    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("chatMessageHandler")
public class ChatMessageHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);

    public ChatMessageHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.error("ChatMessageHandler is starting !!!!");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMessage.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMessage.Message protoMsg = (ProtoMessage.Message) msg;
        ProtoMessage.HeadType type = protoMsg.getType();
        if (!type.equals(ProtoMessage.HeadType.MESSAGE_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMessage.MessageRequest messageRequest = protoMsg.getMessageRequest();
        String content = messageRequest.getContent();
        String uid = messageRequest.getFrom();

        System.out.println("收到来自 " + uid + " 发送的信息： " + content);
    }
}
