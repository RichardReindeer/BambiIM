package com.bambi.imserver.processer.impl;

import com.bambi.imcommon.common.ProtoInstant;
import com.bambi.imcommon.common.bean.User;
import com.bambi.imcommon.common.config.ProtoMessage;
import com.bambi.imserver.processer.IServerProcesser;
import com.bambi.imserver.protoConverter.LoginResponseConverter;
import com.bambi.imserver.session.ServerSession;
import com.bambi.imserver.session.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *      登录处理器
 *      用于通过数据库或者远程token验证，验证用户登录请求
 *
 *      完成服务器端的登录校验，并将校验的结果组成一个登录响应数据包写回给客户端
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/12 0:08    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("LoginRequestProcesser")
public class LoginRequestProcesser implements IServerProcesser {


    @Autowired
    LoginResponseConverter loginResponseConverter;

    @Override
    public ProtoMessage.HeadType type() {
        return ProtoMessage.HeadType.LOGIN_REQUEST;
    }

    /**
     *
     * @param serverSession
     * @param msg
     * @return
     */
    @Override
    public boolean action(ServerSession serverSession, ProtoMessage.Message msg) {

        // 将token 取出并进行验证
        ProtoMessage.LoginRequest loginRequest = msg.getLoginRequest();
        long sequence = msg.getSequence();

        User user = User.fromMsg(loginRequest);
        System.out.println(user.toString());
        //检验用户
        boolean isValidUser = checkUser(user);

        if(!isValidUser){
            ProtoInstant.ResultCodeEnum resultCodeEnum = ProtoInstant.ResultCodeEnum.NO_TOKEN;
            ProtoMessage.Message responseMsg = loginResponseConverter.doBuild(resultCodeEnum, sequence, "-1");
            serverSession.writeAndFlush(responseMsg);
            return false;
        }

        serverSession.setUser(user);

        //服务器Session与channel 绑定
        serverSession.reverseBind();
        ProtoInstant.ResultCodeEnum success = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMessage.Message responseMessage = loginResponseConverter.doBuild(success, sequence, "-1");
        serverSession.writeAndFlush(responseMessage);

        return true;
    }


    /**
     * 用户校验
     *  核对密码等逻辑， 过于冗杂暂时返回true
     * @param user
     * @return
     */
    private boolean checkUser(User user) {

        // 如果已经登录过，则返回false；
        if(SessionMap.getInstance().hasLogin(user)){
            return false;
        }

        // 此处是省略校验逻辑
        // 调用远程用户的restfull 校验服务
        // 或调用数据库接口进行校验
        // 可以使用ThreadLocal记录函数执行时间对其进行相应优化

        return true;
    }
}
