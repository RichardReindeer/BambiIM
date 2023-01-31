package com.bambi.imserver.processer;

import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.session.ServerSession;

/**
 * 描述：
 *
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2022/12/22 1:14    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public interface IServerProcesser {
    /**
     * 获取对应消息头类型
     * @return
     */
    ProtoMessage.HeadType type();

    boolean action(ServerSession serverSession , ProtoMessage.Message msg);
}
