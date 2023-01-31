package com.bambi.imserver.processer.impl;

import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.processer.IServerProcesser;
import com.bambi.imserver.session.ServerSession;
import com.bambi.imserver.session.SessionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 * 处理消息的转发
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/29 21:19    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@Service("chatRedirectProcesser")
public class ChatRedirectProcesser implements IServerProcesser {
    private static Logger logger = LoggerFactory.getLogger(ChatRedirectProcesser.class);

    @Override
    public ProtoMessage.HeadType type() {
        return ProtoMessage.HeadType.MESSAGE_REQUEST;
    }

    /**
     * 判断接收方是否都正常登录，如果登录，则发送消息
     *
     * @param serverSession
     * @param msg
     * @return
     */
    @Override
    public boolean action(ServerSession serverSession, ProtoMessage.Message msg) {
        ProtoMessage.MessageRequest messageRequest = msg.getMessageRequest();
        logger.info("GetChatMsg| from {} , to {} ,  content = {}"
                , messageRequest.getFrom()
                , messageRequest.getTo(),
                messageRequest.getContent());
        String to = messageRequest.getTo();
        List<ServerSession> sessionByUserId = SessionMap.getInstance().getSessionByUserId(to);
        if (sessionByUserId == null || sessionByUserId.size() == 0) {
            // 表明接收方没有登录
            logger.info("对方{},没有登录", to);
        } else {
            // 将消息发送
            sessionByUserId.forEach((session) -> {
                serverSession.writeAndFlush(msg);
            });
        }
        return true;
    }
}
