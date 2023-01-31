package com.bambi.imclient.protoConverter;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.bean.ChatMessage;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;

/**
 * 描述：
 *      聊天信息Converter
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/29 17:15    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class ChatMsgConverter extends BaseConverter{
    private ChatMessage chatMessage;
    private User user;

    public ChatMsgConverter(ClientSession clientSession) {
        super(ProtoMessage.HeadType.MESSAGE_REQUEST,clientSession);
    }

    private ProtoMessage.Message build(ChatMessage chatMessage, User user){
        this.chatMessage = chatMessage;
        this.user = user;

        ProtoMessage.Message.Builder outerBuilder = getOuterBuilder(-1);
        ProtoMessage.MessageRequest.Builder builder = ProtoMessage.MessageRequest.newBuilder();

        this.chatMessage.buildMsg(builder);

        ProtoMessage.Message message = outerBuilder.setMessageRequest(builder).build();
        return message;
    }

    public static ProtoMessage.Message build(
            ChatMessage chatMessage,
            User user,
            ClientSession clientSession
    ){
        ChatMsgConverter chatMsgConverter = new ChatMsgConverter(clientSession);
        return chatMsgConverter.build(chatMessage,user);
    }
}
