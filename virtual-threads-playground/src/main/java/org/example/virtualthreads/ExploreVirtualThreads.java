package org.example.virtualthreads;


import org.example.util.CommonUtil;

import static org.example.util.LoggerUtil.log;


public class ExploreVirtualThreads {

    public static void doSomeWork() {
        log("started doSomeWork");
        CommonUtil.sleep(1000);
        log("finished doSomeWork");
    }

    public static void main(String[] args) {

        log("Program Completed!");

        CommonUtil.sleep(2000);


    }
}
