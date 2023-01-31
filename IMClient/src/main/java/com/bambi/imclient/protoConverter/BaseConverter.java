package com.bambi.imclient.protoConverter;

import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.common.config.ProtoMessage;

/**
 * 描述：
 *  基础构造类
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
public class BaseConverter {

    private ProtoMessage.HeadType headType;
    private ClientSession session;
    private long seqId;

    public BaseConverter(ProtoMessage.HeadType headType, ClientSession session) {
        this.headType = headType;
        this.session = session;
    }

    /**
     * 通过外层builder的buildPartial()方法获取对应Message对象；
     * buildPartial()获取到的message如果必填字段是空，也不会报错
     * 但是proto3 里删除了required , 不知道这个方法是否仍然有意义?
     * 这是一个proto2的遗留方法
     * @param seqId
     * @return
     */
    public ProtoMessage.Message builderOuter(long seqId){
        return getOuterBuilder(seqId).buildPartial();
    }

    /**
     * 获取外层消息builder
     * @param seq
     * @return
     */
    public ProtoMessage.Message.Builder getOuterBuilder(long seq){
        this.seqId = seq;

        ProtoMessage.Message.Builder builder = ProtoMessage.Message.newBuilder()
                .setType(headType)
                .setSessionId(session.getSessionID())
                .setSequence(seqId);
        return builder;
    }
}
