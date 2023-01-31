package com.bambi.imclient.commond.impt;

import com.bambi.imclient.commond.IBaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * 描述：
 * 登录命令收集类
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
@Service("LoginConsoleCommand")
public class LoginConsoleCommand implements IBaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(LoginConsoleCommand.class);

    public static final String KEY = "1";

    private String username;
    private String password;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTips() {
        return "用户登录";
    }

    /**
     * 执行用户登录逻辑
     * 对用户输入进行校验，并将用户名和密码属性赋值
     *
     * @param scanner
     */
    @Override
    public void exec(Scanner scanner) {
        // 传入用户输入，开始用户名校验等
        System.out.println("请输入用户名和密码,格式：用户名&密码");
        String[] userInput = null;
        while (true) {
            String next = scanner.next();
            userInput = next.split("&");
            if (userInput.length != 2) {
                System.err.println("请以正确格式输入用户名和密码");
            } else {
                break;
            }
        }
        username = userInput[0];
        password = userInput[1];
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
