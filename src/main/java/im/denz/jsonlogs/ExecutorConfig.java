package im.denz.jsonlogs;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ExecutorConfig {
    public static final Integer MIN_THREADS_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    public static ForkJoinPool factoryPool(int threadCount, String name){
        return new ForkJoinPool(threadCount, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                final ForkJoinWorkerThread workerThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                workerThread.setName(name);
                return workerThread;
            }
        },null,true);
    }
}
