package com.bambi.imserver.protoConverter;

import com.bambi.imcommon.common.ProtoInstant;
import com.bambi.imcommon.common.config.ProtoMessage;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      创建登录响应信息ProtobufBuf
 *      含有public方法 doBuild() ， 返回组装好的登录响应Message
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/22 1:41    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
@Service("LoginResponseBuilder")
public class LoginResponseConverter {

    /**
     *
     * @param en
     * @param seqId
     * @param sessionId
     * @return
     */
    public ProtoMessage.Message doBuild(ProtoInstant.ResultCodeEnum en , long seqId, String sessionId){
        ProtoMessage.Message.Builder outerBuilder = ProtoMessage.Message.newBuilder();
        outerBuilder.setType(ProtoMessage.HeadType.LOGIN_RESPONSE);
        outerBuilder.setSequence(seqId);
        outerBuilder.setSessionId(sessionId);

        ProtoMessage.LoginResponse.Builder builder = ProtoMessage.LoginResponse.newBuilder();
        builder.setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        outerBuilder.setLoginResponse(builder.build());
        return outerBuilder.build();
    }
}
