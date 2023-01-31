package com.bambi.imclient;

import com.bambi.imclient.commond.IBaseCommand;
import com.bambi.imclient.commond.impt.ChatConsoleCommand;
import com.bambi.imclient.commond.impt.ClientCommandMenu;
import com.bambi.imclient.commond.impt.LoginConsoleCommand;
import com.bambi.imclient.commond.impt.LogoutConsoleCommand;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 描述：
 *      测试client控制台指令
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/2/1 4:06    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class TestClientLoginCommand {
    Map<String , IBaseCommand> commandMap;
    ClientCommandMenu clientCommandMenu = new ClientCommandMenu();

    /**
     * 如果测试的控制台无法输入，请在VM Options中添加指令<br>
     * -Deditable.java.test.console=true
     */
    @Test
    public void testLoginCommand(){
        LoginConsoleCommand consoleCommand = new LoginConsoleCommand();
        while (true){
            Scanner scanner = new Scanner(System.in);
            consoleCommand.exec(scanner);
            System.out.println("本次输入的username :"+consoleCommand.getUsername());
            System.out.println("本次输入的password为： "+consoleCommand.getPassword());
        }
    }

    @Test
    public void testClientCommandMenu(){
        ClientCommandMenu clientCommandMenu1 = new ClientCommandMenu();
        clientCommandMenu1.setAllCommandsShow("[menu] 0->show 所有命令 | 1->登录 | ...");
        Scanner scanner = new Scanner(System.in);
        while (true){
            clientCommandMenu1.exec(scanner);
            System.out.println("本次输入 : " + clientCommandMenu1.getCommandInput());
        }
    }

    @Test
    public void testCommandController(){
        initCommandMap();
        while (true){
            Scanner scanner = new Scanner(System.in);
            clientCommandMenu.exec(scanner);
            String commandInput = clientCommandMenu.getCommandInput();
            IBaseCommand command = commandMap.get(commandInput);
            if(command == null){
                System.out.println("无法识别");
                return;
            }

            switch (commandInput){
                case LoginConsoleCommand.KEY:
                    command.exec(scanner);
                    System.out.println("本次输入的username :"+((LoginConsoleCommand)command).getUsername());
                    System.out.println("本次输入的password为： "+((LoginConsoleCommand)command).getPassword());
                    break;
                case ChatConsoleCommand.KEY:
                    break;
                case LogoutConsoleCommand.KEY:
                    break;
            }
        }
    }

    public void initCommandMap() {
        commandMap = new HashMap<>();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        LogoutConsoleCommand logoutConsoleCommand = new LogoutConsoleCommand();
        ChatConsoleCommand chatConsoleCommand = new ChatConsoleCommand();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);

        clientCommandMenu.setAllCommands(commandMap);
    }
}
