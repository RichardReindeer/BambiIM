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
 *  001699   2022/12/4 2:41    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("LogoutConsoleCommand")
public class LogoutConsoleCommand implements IBaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(LogoutConsoleCommand.class);

    public static final String KEY = "3";
    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTips() {
        return "用户登出";
    }

    /**
     * 执行对应逻辑处理
     * @param scanner
     */
    @Override
    public void exec(Scanner scanner) {

    }
}
