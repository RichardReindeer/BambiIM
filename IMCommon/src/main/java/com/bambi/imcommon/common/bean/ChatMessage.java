package com.bambi.imcommon.common.bean;

import com.bambi.imcommon.common.config.ProtoMessage;
import org.apache.commons.lang.StringUtils;

/**
 * 描述：
 *      定义发送的消息格式
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/29 15:08    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class ChatMessage {

    // 1.文本 ， 2. 音频， 3.视频， 4.地理位置 ， 5：其他
    public enum MSG_TYPE{
        TEXT,
        AUDIO,
        VIDEO,
        POS,
        OTHER
    }

    private User user;

    private long msgId;
    private String from;
    private String to;
    private long time;
    private MSG_TYPE msgType;
    private String content;
    private String url;          //多媒体地址
    private String property;     //附加属性
    private String fromNick;     //发送者昵称
    private String json;         //附加的json串

    public ChatMessage(User user) {
        if(user == null){
            return;
        }
        this.user = user;
        this.setTime(System.currentTimeMillis());
        this.setFrom(user.getUid());
        this.setFromNick(user.getNickName());
    }

    /**
     *  拼装数据包
     * @param builder
     */
    public void buildMsg(ProtoMessage.MessageRequest.Builder builder){
        if(msgId > 0){
            builder.setMsgId(msgId);
        }
        if(StringUtils.isNotEmpty(from)){
            builder.setFrom(from);
        }
        if(StringUtils.isNotEmpty(to)){
            builder.setTo(to);
        }
        if(StringUtils.isNotEmpty(content)){
            builder.setContent(content);
        }
        if(StringUtils.isNotEmpty(url)){
            builder.setUrl(url);
        }
        if(StringUtils.isNotEmpty(property)){
            builder.setProperty(property);
        }
        if(StringUtils.isNotEmpty(fromNick)){
            builder.setFromNick(fromNick);
        }
        if(StringUtils.isNotEmpty(json)){
            builder.setJson(json);
        }
        if(time > 0){
            builder.setTime(time);
        }
        if(msgType != null){
            builder.setMsgType(msgType.ordinal());
        }

    }

    // Getter and Setter Start
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MSG_TYPE getMsgType() {
        return msgType;
    }

    public void setMsgType(MSG_TYPE msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
