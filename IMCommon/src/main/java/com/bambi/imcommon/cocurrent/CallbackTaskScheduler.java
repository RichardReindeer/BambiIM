package com.bambi.imcommon.cocurrent;

import com.bambi.imcommon.utils.ThreadUtil;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * 描述：
 *      使用自建线程池处理耗时操作
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID     DATE          PERSON          REASON
 *  1      2023/1/27 15:02    Bambi        Create
 * ****************************************************************************
 * </pre>
 *
 * @author Bambi
 * @since 1.0
 */
public class CallbackTaskScheduler {
    static ListeningExecutorService guavaPool = null;

    static {
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        guavaPool = MoreExecutors.listeningDecorator(jPool);
    }


    private CallbackTaskScheduler() {
    }

    /**
     * 添加任务
     * @param executeTask
     */
    public static <R> void add(CallbackTask<R> executeTask) {


        ListenableFuture<R> future = guavaPool.submit(new Callable<R>() {
            public R call() throws Exception {

                R r = executeTask.execute();
                return r;
            }

        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });


    }

}
