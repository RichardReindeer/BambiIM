package com.bambi.imserver;

import com.bambi.imcommon.codec.SimpleProtobufDec;
import com.bambi.imcommon.codec.SimpleProtobufEnc;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imcommon.utils.ThreadUtil;
import com.bambi.imserver.handler.ServerLoginRequestHandler;
import com.bambi.imserver.handler.ServerProtobufEncoder;
import com.bambi.imserver.starter.ImServerApplication;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.ByteOrder;
import java.util.UUID;

/**
 * 描述：
 *      登录相关测试类
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/28 7:47    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImServerApplication.class)
public class TestLogin {
    private static Logger logger = LoggerFactory.getLogger(TestLogin.class);
    @Autowired
    private ServerLoginRequestHandler loginRequestHandler;

    @Test
    public void testLoginProcess(){
        //todo
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<>() {            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new SimpleProtobufDec());
                ch.pipeline().addLast("login",loginRequestHandler);
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);
        User user = new User();
        ProtoMessage.Message message = buildLoginMsg(user);
        ByteBuf order = Unpooled.buffer(1024).order(ByteOrder.BIG_ENDIAN);
        SimpleProtobufEnc.encode0(message,order);
        embeddedChannel.writeInbound(order);
        embeddedChannel.flush();

        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);

    }

    private ProtoMessage.Message buildLoginMsg(User user) {
        ProtoMessage.Message.Builder outer = ProtoMessage.Message.newBuilder()
                .setType(ProtoMessage.HeadType.LOGIN_REQUEST)
                .setSessionId(UUID.randomUUID().toString())
                .setSequence(-1);

        ProtoMessage.LoginRequest.Builder lb =
                ProtoMessage.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatForm().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());
        return outer.setLoginRequest(lb).build();
    }
}
