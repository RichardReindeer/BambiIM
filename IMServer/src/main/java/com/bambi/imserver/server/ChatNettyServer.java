package com.bambi.imserver.server;

import com.bambi.imcommon.codec.SimpleProtobufDec;
import com.bambi.imcommon.codec.SimpleProtobufEnc;
import com.bambi.imserver.handler.ExceptionHandler;
import com.bambi.imserver.handler.ServerLoginRequestHandler;
import com.bambi.imserver.handler.ServerProtobufDecoder;
import com.bambi.imserver.handler.ServerProtobufEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * 描述：
 * Netty服务器端
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/11 12:52    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@Service("chatNettyServer")
public class ChatNettyServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatNettyServer.class);

    @Value("${server.port}")
    private int port;

    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    // 逻辑处理器开始
    @Autowired
    private ServerLoginRequestHandler serverLoginRequestHandler;
    @Autowired
    private ExceptionHandler exceptionHandler;
    // 逻辑处理器结束

    public void runServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 流水线装配
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //handler
                    socketChannel.pipeline().addLast(new SimpleProtobufDec());
                    socketChannel.pipeline().addLast(new SimpleProtobufEnc());
                    socketChannel.pipeline().addLast("login",serverLoginRequestHandler);
                    socketChannel.pipeline().addLast(exceptionHandler);
                }
            });

            // 一直等待，直到通道关闭
            ChannelFuture sync = serverBootstrap.bind().sync();
            ChannelFuture channelFuture = sync.channel().closeFuture();
            channelFuture.sync();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
