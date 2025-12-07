package org.example.virtualthreads;


import org.example.util.CommonUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.example.util.LoggerUtil.log;


public class PinnedVirtualThreads {

    static AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) {
        int MAX_THREADS = 10;
        IntStream.rangeClosed(1, MAX_THREADS)
                .forEach((i) -> {
                    //Thread.ofVirtual().start(() -> new Counter().getAndIncrement(i));
                    Thread.ofVirtual().start(() -> new Counter().getAndIncrementUsingRentrantLock(i));
                    atomicInteger.incrementAndGet();
                    log("No of threads : " + atomicInteger.get());
                });

        log("Program Completed!");

        CommonUtil.sleep(10000);

    }
}
