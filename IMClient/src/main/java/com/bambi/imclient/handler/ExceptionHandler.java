package com.bambi.imclient.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      异常处理Handler
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 15:35    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@ChannelHandler.Sharable
@Service("exceptionHandler")
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

}
