package com.bambi.imcommon.codec;

import com.bambi.imcommon.common.ProtoInstant;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imcommon.common.exception.InvalidFrameException;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 描述：
 *      TODO 简易解码器
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
public class SimpleProtobufDec extends ByteToMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(SimpleProtobufDec.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        Object outMsg = decode0(channelHandlerContext,byteBuf);
        if(outMsg!=null){
            // 将业务信息装填
            list.add(outMsg);
        }
    }

    private static Object decode0(ChannelHandlerContext channelHandlerContext,
                                  ByteBuf byteBuf) throws InvalidFrameException, InvalidProtocolBufferException {
        byteBuf.markReaderIndex();
        if(byteBuf.readableBytes()<8){
            return null;
        }

        short magicWord = byteBuf.readShort();
        if(magicWord != ProtoInstant.MAGIC_CODE){
            String errorCode = "客户端口令出错 "+ channelHandlerContext.channel().remoteAddress();
            throw new InvalidFrameException(errorCode);
        }
        short version = byteBuf.readShort();
        if(ProtoInstant.VERSION_CODE != version){
            String errorCode = "协议版本不对: "+channelHandlerContext.channel().remoteAddress();
            throw new InvalidFrameException(errorCode);
        }
        int length = byteBuf.readInt();
        if(length<0){
            channelHandlerContext.close();
        }

        if(length>byteBuf.readableBytes()){ //半包
            byteBuf.resetReaderIndex();
            return null;
        }
        logger.info("decoder length = ---> "+byteBuf.readableBytes());
        byte[] array;
        if(byteBuf.hasArray()){
            //使用堆缓冲....
            // TODO 发现处理逻辑一致暂不做区分，可后期根据需求更改
        }
        array = new byte[length];
        byteBuf.readBytes(array,0,length);
        ProtoMessage.Message message = ProtoMessage.Message.parseFrom(array);

        return message;
    }
}
