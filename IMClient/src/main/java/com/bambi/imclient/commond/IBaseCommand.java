package com.bambi.imclient.commond;

import java.util.Scanner;

/**
 * 描述：
 *      命令通用接口
 *      声明通用方法 : 1. 获取用户输入的KEY
 *                   2. 获取提示的详情信息
 *                   3. 从控制台获取业务数据
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
public interface IBaseCommand {

    /**
     * 获取命令Key
     * @return
     */
    public String getKey();

    /**
     * 获取命令提示
     * @return
     */
    public String getTips();

    /**
     * 从控制台获取对应的业务数据
     * @param scanner
     */
    void exec(Scanner scanner);
}
