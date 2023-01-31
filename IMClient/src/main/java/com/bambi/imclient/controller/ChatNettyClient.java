package com.bambi.imclient.controller;

import com.bambi.imclient.handler.ExceptionHandler;
import com.bambi.imclient.handler.LoginResponseHandler;
import com.bambi.imcommon.codec.SimpleProtobufDec;
import com.bambi.imcommon.codec.SimpleProtobufEnc;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      Netty客户端
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/6 19:29    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("ChatNettyClient")
public class ChatNettyClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatNettyClient.class);

    @Value("${chat.server.port}")
    private int port;
    @Value("${chat.server.ip}")
    private String ip;
    Bootstrap bootstrap ;

    // 不管是客户端的bootStrap还是服务器端的ServerBootStrap都继承自抽象类AbstractBootstrap
    EventLoopGroup group;

    // todo 为什么指定1个线程?
    public ChatNettyClient() {
        group = new NioEventLoopGroup(1);
    }

    private GenericFutureListener<ChannelFuture> connectedListener; // 连接成功时的事件监听

    @Autowired
    private LoginResponseHandler loginResponseHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;
    /**
     * 连接服务器
     */
    public void clientConnect(){
        System.out.println("clientConnect is starting !!!");
        bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .remoteAddress(ip,port);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 编码器与解码器一般会放在最前面
                    // 编解码
                    socketChannel.pipeline().addLast("decoder",new SimpleProtobufDec());
                    socketChannel.pipeline().addLast("encoder",new SimpleProtobufEnc());

                    // 逻辑处理
                    socketChannel.pipeline().addLast(loginResponseHandler);
                    // 一般会在最后装填一个异常处理类，负责处理连接异常或中断
                    socketChannel.pipeline().addLast(exceptionHandler);
                }
            });

            logger.info("客户端开始尝试连接【BambiIM】");
            ChannelFuture connect = bootstrap.connect();
            connect.addListener(connectedListener);
        } catch (Exception e) {
            logger.error("客户端连接失败！"+e.getMessage());
        }
    }

    public void close(){
        group.shutdownGracefully();
    }


    public GenericFutureListener<ChannelFuture> getConnectedListener() {
        return connectedListener;
    }


    /**
     * 在CommandController中接收参数设置对应的回调监听器
     * 用来判断是否连接成功，并在连接成功时执行相应逻辑
     * @param connectedListener
     */
    public void setConnectedListener(GenericFutureListener<ChannelFuture> connectedListener) {
        this.connectedListener = connectedListener;
    }
}
