package com.bambi.imclient;

import com.bambi.imclient.config.SystemConfig;
import com.bambi.imclient.handler.ExceptionHandler;
import com.bambi.imclient.handler.LoginResponseHandler;
import com.bambi.imclient.sender.LoginSender;
import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.codec.SimpleProtobufDec;
import com.bambi.imcommon.codec.SimpleProtobufEnc;
import com.bambi.imcommon.common.bean.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * 描述：
 *      测试登录以及消息发送
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/31 12:44    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ImClientApplication.class)
@ContextConfiguration(classes = SystemConfig.class)
public class TestLoginAndMsgSender {
    private static Logger logger = LoggerFactory.getLogger(TestLoginAndMsgSender.class);
    @Autowired
    private LoginSender loginSender;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private LoginResponseHandler responseHandler;
    @Autowired
    private ExceptionHandler exceptionHandler;

    private Channel channel;

    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup g = new NioEventLoopGroup(1);

    /**
     * 初始化
     */
    private void initBootStrap(){
        bootstrap.group(g);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.remoteAddress(systemConfig.getHost(), systemConfig.getPort());
    }

    GenericFutureListener<ChannelFuture> connectListener = (ChannelFuture f)->{
        final EventLoop eventLoop = f.channel().eventLoop();

        if(!f.isSuccess()){
            logger.info("信息发送失败");
        }else {
            logger.info("信息发送成功");
            channel= f.channel();
            ClientSession clientSession = new ClientSession(channel);
            clientSession.setConnected(true);
        }
    };

    @Test
     public void testConnectServer(){
         initBootStrap();
         bootstrap.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast("decoder",new SimpleProtobufDec());
                 ch.pipeline().addLast("encoder",new SimpleProtobufEnc());
             }
         });
         logger.info("测试用例： 客户端开始链接BAMBI IM");
        ChannelFuture connect = bootstrap.connect();
        connect.addListener(connectListener);
        try {
            connect.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            connect.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("测试用例完成");
        connect.channel().close();
    }

    // 如果没有问题，则发起链接正常

    // 测试登录
    GenericFutureListener<ChannelFuture> listener2 = (ChannelFuture future)->{
        if(!future.isSuccess()){
            logger.info("链接失败，等待重试");
        }else {
            logger.info("链接成功，测试登录");
            channel = future.channel();
            ClientSession clientSession = new ClientSession(channel);
            clientSession.setConnected(true);
            startLogin(clientSession);
        }
    };
    private void startLogin(ClientSession clientSession){
        User user = new User();
        user.setUid("1");
        user.setToken(UUID.randomUUID().toString());
        user.setDevId(UUID.randomUUID().toString());
        loginSender.setUser(user);
        loginSender.setClientSession(clientSession);
        loginSender.sendLoginMsg();
    }

    /**
     * 在建立链接之后进行监听，将在监听器中根据结果调用对应的login函数
     */
    @Test
    public void startLoginSender(){
        initBootStrap();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder",new SimpleProtobufDec());
                ch.pipeline().addLast("encoder",new SimpleProtobufEnc());
            }
        });
        ChannelFuture connect = bootstrap.connect();
        connect.addListener(listener2);
        try {
            connect.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        connect.channel().close();
    }

    @Test
    public void testResponse(){
        initBootStrap();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("encoder",new SimpleProtobufEnc());
                ch.pipeline().addLast("decoder",new SimpleProtobufDec());
                ch.pipeline().addLast(responseHandler);
                ch.pipeline().addLast(exceptionHandler);
            }
        });

        ChannelFuture connect = bootstrap.connect();
        connect.addListener(listener2);
        try {
            connect.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        connect.channel().close();
    }
}








