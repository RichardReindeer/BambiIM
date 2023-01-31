package com.bambi.imclient.commond.impt;

import com.bambi.imclient.commond.IBaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 描述：
 * 用户登录菜单
 * 在输入登录信息之前，用户所选择的菜单是登录的选项。最开始的时候，客户端通过
 * ClientCommandMenu菜单展示类展示出一个命令菜单，以供用户选择。
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/4 2:33    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("ClientCommandMenu")
public class ClientCommandMenu implements IBaseCommand {
    Logger logger = LoggerFactory.getLogger(ClientCommandMenu.class);

    /**
     * 展示菜单对应的KEY -- > 0
     */
    public static final String KEY = "0";

    private String allCommandsShow;
    private String commandInput;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTips() {
        return "展示所有命令 show All tips";
    }

    /**
     * 获取指令
     *
     * @param scanner
     */
    @Override
    public void exec(Scanner scanner) {
        logger.debug("exec in ClientCommandMenu is starting !!!");

        System.err.println("请输入某个指令: " + allCommandsShow);
        commandInput = scanner.nextLine();
    }

    public String getAllCommandsShow() {
        return allCommandsShow;
    }

    /**
     * 设置命令展示
     * 将所有命令进行拼接，用于在控制台对用户显示
     *
     * @param allCommandsMap 存放所有命令的key 与命令类的hashMap
     */
    public void setAllCommands(Map<String, IBaseCommand> allCommandsMap) {
        Set<Map.Entry<String, IBaseCommand>> entries = allCommandsMap.entrySet();
        Iterator<Map.Entry<String, IBaseCommand>> iterator = entries.iterator();

        StringBuilder menu = new StringBuilder();
        menu.append("[菜单 Menu] ");
        // 组装 StringBuilder
        while (iterator.hasNext()) {
            IBaseCommand value = iterator.next().getValue();

            menu.append(value.getKey())
                    .append("-> ")
                    .append(value.getTips())
                    .append(" | ");
        }

        allCommandsShow = menu.toString();
    }

    public String getCommandInput() {
        return commandInput;
    }

    public void setCommandInput(String commandInput) {
        this.commandInput = commandInput;
    }

    public void setAllCommandsShow(String allCommandsShow) {
        this.allCommandsShow = allCommandsShow;
    }
}
