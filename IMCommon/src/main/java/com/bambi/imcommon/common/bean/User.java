package com.bambi.imcommon.common.bean;

import com.bambi.imcommon.common.config.ProtoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述：
 * 用户实体类
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 20:21    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private static final AtomicInteger NO = new AtomicInteger(1);
    String uid = String.valueOf(NO.getAndIncrement());
    String devId = UUID.randomUUID().toString();
    String token = UUID.randomUUID().toString();
    String nickName = "NickName"; // 暂简单写死
    PLATTYPE platForm = PLATTYPE.WINDOWS;

    /**
     * 平台类型枚举类
     */
    public enum PLATTYPE {
        WINDOWS, MAC, IOS, ANDROID, WEB, OTHER;
    }

    private String sessionID;

    public void setPlatform(int platform) {
        PLATTYPE[] values = PLATTYPE.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].ordinal() == platform) {
                this.platForm = values[i];
            }
        }
    }

    /**
     * 获取并组装对应的user信息
     *
     * @param info
     * @return
     */
    public static User fromMsg(ProtoMessage.LoginRequest info) {
        User user = new User();
        user.setUid(info.getUid());
        user.setToken(info.getToken());
        user.setPlatform(info.getPlatform());
        logger.info("登陆中 {}", user.toString());
        return user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public PLATTYPE getPlatForm() {
        return platForm;
    }

    public void setPlatForm(PLATTYPE platForm) {
        this.platForm = platForm;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", devId='" + devId + '\'' +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                ", platForm=" + platForm +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }

}
