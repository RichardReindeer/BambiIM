package com.bambi.imclient.controller;

import com.bambi.imclient.commond.IBaseCommand;
import com.bambi.imclient.commond.impt.ChatConsoleCommand;
import com.bambi.imclient.commond.impt.ClientCommandMenu;
import com.bambi.imclient.commond.impt.LoginConsoleCommand;
import com.bambi.imclient.commond.impt.LogoutConsoleCommand;
import com.bambi.imclient.sender.ChatSender;
import com.bambi.imclient.sender.LoginSender;
import com.bambi.imclient.session.ClientSession;
import com.bambi.imcommon.cocurrent.FutureTaskScheduler;
import com.bambi.imcommon.common.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 * 命令台控制器类
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/4 23:08    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
@Service("commandController")
public class CommandController {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    // 菜单命令收集类
    @Autowired
    ClientCommandMenu clientCommandMenu;

    // 聊天命令收集类
    @Autowired
    ChatConsoleCommand chatConsoleCommand;

    // 登录命令收集类
    @Autowired
    LoginConsoleCommand loginConsoleCommand;

    // 登出命令收集类
    @Autowired
    LogoutConsoleCommand logoutConsoleCommand;

    @Autowired
    ChatNettyClient chatNettyClient; //Netty客户端

    private Map<String, IBaseCommand> commandMap;
    private String menuString;

    private boolean connectFlag = false; // 连接标志位
    private Channel channel;

    private User user;
    private ClientSession clientSession;

    @Autowired
    private LoginSender loginSender;
    @Autowired
    private ChatSender chatSender;
    /**
     * 通道关闭监听器
     */
    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        logger.info("进入closeListener回调");
        channel = f.channel();
        ClientSession clientSession = channel.attr(ClientSession.SESSION_KEY).get();
        clientSession.close();
        notifyCommandThread();
    };

    /**
     * 通道连接事件监听器
     */
    GenericFutureListener<ChannelFuture> connectListener = (ChannelFuture f) -> {
        EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            // 连接失败
            logger.debug("连接失败! 在10s 之后重新尝试连接");
            eventLoop.schedule(() -> chatNettyClient.clientConnect(), 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;
            logger.debug("BambiSingleIM 连接成功");
            channel = f.channel();
            //创建用户会话
            clientSession = new ClientSession(channel);
            clientSession.setConnected(true);
            channel.closeFuture().addListener(closeListener);

            notifyCommandThread();
        }
    };


    /**
     * 唤醒命令控制台线程
     */
    private synchronized void notifyCommandThread() {
        this.notify();
    }

    public synchronized void waitCommandThread() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化命令映射
     * 并调用ClientCommandMenu 将映射传入
     */
    public void initCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);

        clientCommandMenu.setAllCommands(commandMap);
    }

    /**
     * 启动命令线程
     */
    public void commandTreadRunning() {
        logger.debug("commandTreadRunning is starting !!!!!");
        Thread.currentThread().setName("Command Thread");

        // 循环逻辑
        while (true) {
            while (!connectFlag) {
                startConnectServer();
                waitCommandThread();
            }
            while (null != clientSession) {
                // 连接成功
                Scanner scanner = new Scanner(System.in);
                clientCommandMenu.exec(scanner);
                String commandInput = clientCommandMenu.getCommandInput();
                IBaseCommand baseCommand = commandMap.get(commandInput);
                if (baseCommand == null) {
                    System.err.println("指令错误，无法识别[command  " + commandInput + "  ] 请重新输入");
                    continue;
                }

                switch (commandInput) {
                    // 根据key的不同实现对应逻辑
                    case LoginConsoleCommand.KEY:
                        baseCommand.exec(scanner);
                        startLogin((LoginConsoleCommand) baseCommand);
                        break;
                    case ChatConsoleCommand.KEY:
                        baseCommand.exec(scanner);
                        startSingleChat((ChatConsoleCommand) baseCommand);
                        break;
                    case LogoutConsoleCommand.KEY:
                        baseCommand.exec(scanner);
                        //todo 登出
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 组装user对象，并调用LoginSender 将消息发送给服务器
     *
     * @param loginConsoleCommand
     */
    private void startLogin(LoginConsoleCommand loginConsoleCommand) {
        if (!isConnectFlag()) {
            logger.error("连接失败");
            return;
        }
        // 包装数据
        User user = new User();
        user.setUid(loginConsoleCommand.getUsername());
        user.setToken(loginConsoleCommand.getPassword());
        user.setDevId("qwertyui");
        this.user = user;
        clientSession.setUser(user);
        loginSender.setUser(user);
        loginSender.setClientSession(clientSession);
        loginSender.sendLoginMsg();
    }

    /**
     * 点对点聊天
     */
    private void startSingleChat(ChatConsoleCommand chatConsoleCommand) {
        if (!isLogin()) {
            logger.info("请先登录");
            return;
        }
        chatSender.setUser(user);
        chatSender.setClientSession(clientSession);
        chatSender.sendChat(chatConsoleCommand.getToUserId(), chatConsoleCommand.getMessage());

    }

    /**
     * 尝试连接服务器
     */
    private void startConnectServer() {
        FutureTaskScheduler.add(() -> {
            chatNettyClient.setConnectedListener(connectListener);
            chatNettyClient.clientConnect();
        });
    }

    private boolean isLogin() {
        if (clientSession == null) {
            logger.info("session is Null !!!!!");
            return false;
        }
        return clientSession.isLogin();
    }

    public Map<String, IBaseCommand> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(Map<String, IBaseCommand> commandMap) {
        this.commandMap = commandMap;
    }

    public String getMenuString() {
        return menuString;
    }

    public void setMenuString(String menuString) {
        this.menuString = menuString;
    }

    public boolean isConnectFlag() {
        return connectFlag;
    }

    public void setConnectFlag(boolean connectFlag) {
        this.connectFlag = connectFlag;
    }
}
