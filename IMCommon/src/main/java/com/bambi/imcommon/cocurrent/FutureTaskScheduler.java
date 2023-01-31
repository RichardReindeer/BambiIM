package com.bambi.imcommon.cocurrent;

import com.bambi.imcommon.utils.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述：
 * 使用自建的线程池来处理耗时操作
 *      TODO 为了测试功能，所以使用的是Executors来创建大小固定的线程池，如果投入生产，需优化选择
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/7 15:47    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
public class FutureTaskScheduler {
    static ThreadPoolExecutor mixPool = null;

    static {
        mixPool = ThreadUtil.getMixedTargetThreadPool();
    }

    private static FutureTaskScheduler init = new FutureTaskScheduler();

    private FutureTaskScheduler() {
    }

    /**
     *
     */
    public static void add(ExecuteTask executeTask) {
        mixPool.submit(() -> {
            executeTask.execute();
        });
    }
}
