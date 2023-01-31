package com.bambi.imclient.protoConverter;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;

/**
 * 描述：
 *
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/28 14:52    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class HeartBeatConverter extends BaseConverter{
    private final User user;

    public HeartBeatConverter(User user, ClientSession clientSession) {
        super(ProtoMessage.HeadType.HEART_BEAT,clientSession);
        this.user = user;
    }

    public ProtoMessage.Message build(){
        ProtoMessage.Message.Builder outerBuilder = getOuterBuilder(-1);
        ProtoMessage.MessageHeartBeat.Builder builder = ProtoMessage.MessageHeartBeat.newBuilder()
                .setSeq(0)
                .setJson("{\"from\":\"client\"}")
                .setUid(user.getUid());
        return outerBuilder.setHeartBeat(builder).build();
    }
}
