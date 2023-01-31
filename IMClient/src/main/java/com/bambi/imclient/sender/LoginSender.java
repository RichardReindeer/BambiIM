package com.bambi.imclient.sender;

import com.bambi.imclient.protoConverter.LoginMsgConverter;
import com.bambi.imcommon.common.config.ProtoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      登录数据发送器(Sender)
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/8 14:23    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("loginSender")
public class LoginSender extends BaseSender{
    private static final Logger logger = LoggerFactory.getLogger(LoginSender.class);

    public void sendLoginMsg() {
        if(!isConnected()){
            logger.error("未成功连接，请重试");
            return;
        }

        logger.info("开始构建信息");
        ProtoMessage.Message build = LoginMsgConverter.build(getUser(), getClientSession());

        logger.info("开始发送信息");
        super.sendMessage(build);
    }
}
