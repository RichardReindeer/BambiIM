package com.bambi.imclient.sender;

import com.bambi.imclient.protoConverter.ChatMsgConverter;
import com.bambi.imcommon.common.bean.ChatMessage;
import com.bambi.imcommon.common.config.ProtoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      聊天信息发送
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/29 15:04    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@Service("ChatSender")
public class ChatSender extends BaseSender{
    private static Logger logger = LoggerFactory.getLogger(ChatSender.class);

    /**
     * 发送消息
     * 将信息Pojo进行组装，并调用build进行转换
     * @param receiveUserId 接收用户id
     * @param content
     */
    public void sendChat(String receiveUserId,String content){
        logger.debug("sendChat is starting !!!!");
        ChatMessage chatMessage = new ChatMessage(getUser());
        chatMessage.setContent(content);
        chatMessage.setTo(receiveUserId);
        chatMessage.setMsgType(ChatMessage.MSG_TYPE.TEXT);
        chatMessage.setTo(receiveUserId);
        chatMessage.setMsgId(System.currentTimeMillis());
        ProtoMessage.Message message = ChatMsgConverter.build(chatMessage, getUser(), getClientSession());

        super.sendMessage(message);
    }

    @Override
    protected void sendFailed(ProtoMessage.Message message) {
        logger.error("Chat 聊天信息发送失败 ~~~ ");
        // logger.info("消息内容: {}",message.getMessageRequest().getContent());
    }

    @Override
    protected void sendSuccess(ProtoMessage.Message message) {
        logger.info("消息发送成功 {}",message.getMessageRequest().getContent());
    }
}
















