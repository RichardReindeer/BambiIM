package com.bambi.imclient.protoConverter;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述：
 *      登录信息构造器
 *      在设计proto文件时，设计了信息头的不同类型，通过程序的构建，指定类型，方便服务器进行识别和处理
 *
 *      获取当前user对象以及其对应的clientSession , 组装对应的protobuf消息类并返回
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/8 16:33    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */

public class LoginMsgConverter extends BaseConverter{
    private static Logger logger = LoggerFactory.getLogger(LoginMsgConverter.class);
    private final User user;

    public LoginMsgConverter(ClientSession session, User user) {
        super(ProtoMessage.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    /**
     * 调用getOuterBuilder获取外层Message的builder
     * 封装LoginRequest , 并构建LoginRequest数据
     * @return
     */
    public ProtoMessage.Message build(){
        logger.debug("LoginMsgConverter's build is starting !!!");

        ProtoMessage.Message.Builder outerBuilder = getOuterBuilder(-1);

        ProtoMessage.LoginRequest.Builder builder = ProtoMessage.LoginRequest.newBuilder()
                .setPlatform(user.getPlatForm().ordinal())
                .setDeviceId(user.getDevId())
                .setToken(user.getToken())
                .setUid(user.getUid());
        ProtoMessage.Message loginRequestMessage = outerBuilder.setLoginRequest(builder).build();

        return loginRequestMessage;
    }

    public static ProtoMessage.Message build(User user , ClientSession clientSession){
        LoginMsgConverter loginMsgConverter = new LoginMsgConverter(clientSession,user);
        return loginMsgConverter.build();
    }
}
