package com.bambi.imcommon.codec;

import com.bambi.imcommon.common.ProtoInstant;
import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述：
 *      TODO 简易编码器
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/28 7:51    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class SimpleProtobufEnc extends MessageToByteEncoder<ProtoMessage.Message> {
    private static Logger logger = LoggerFactory.getLogger(SimpleProtobufEnc.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMessage.Message message, ByteBuf byteBuf) throws Exception {
        encode0(message, byteBuf);
    }


    public static void encode0(ProtoMessage.Message message, ByteBuf byteBuf) {
        byteBuf.writeShort(ProtoInstant.MAGIC_CODE);
        byteBuf.writeShort(ProtoInstant.VERSION_CODE);
        byte[] bytes = message.toByteArray();

        int length = bytes.length;
        logger.debug("消息当前长度为: {}", length);
        byteBuf.writeInt(length);
        byteBuf.writeBytes(bytes);
    }


}















