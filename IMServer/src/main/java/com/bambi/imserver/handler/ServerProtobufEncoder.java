package com.bambi.imserver.handler;

import com.bambi.imcommon.common.config.ProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 *
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/11 16:06    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class ServerProtobufEncoder extends MessageToByteEncoder<ProtoMessage.Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMessage.Message message, ByteBuf byteBuf) throws Exception {

    }
}
