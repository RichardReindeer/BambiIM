package com.bambi.imclient.commond.impt;

import com.bambi.imclient.commond.IBaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * 描述：
 *
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/4 2:40    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("ChatConsoleCommand")
public class ChatConsoleCommand implements IBaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(ChatConsoleCommand.class);

    public static final String KEY = "2";
    public String toUserId;
    public String message;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTips() {
        return "开始聊天";
    }

    /**
     * 提示输入聊天内容，根据指定符号切割
     * @param scanner
     */
    @Override
    public void exec(Scanner scanner) {
        System.out.println("请输入聊天信息(格式: id:message)");
        String[] info = null;
        while (true){
            String next = scanner.next();
            info = next.split(":");
            if(info.length!=2){
                System.out.println("请输入聊天信息(格式: id:message)");
            }else{
                break;
            }
        }
        toUserId = info[0];
        message = info[1];

    }


    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
