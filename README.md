# Modern-Java---Multithreading-in-Java-using-Virtual-Threads


# 1. Introduction to Virtual Threads
Virtual Threads (Project Loom) are lightweight threads that revolutionize concurrent programming in Java. Unlike platform threads (traditional threads), virtual threads are managed by the JVM, not the OS kernel.

# Key Characteristics:
- Lightweight: Can have millions of virtual threads (vs thousands of platform threads)
- Non-blocking: Blocking operations don't block OS threads
- Managed by JVM: Scheduled on platform threads (carrier threads)
- Compatible: Same API as java.lang.Thread

# Key Takeaways:
- Use Virtual Threads for I/O-bound workloads: They excel when tasks spend time waiting
- Avoid thread-local variables: They can cause memory leaks with virtual threads
- Use structured concurrency: For better error handling and resource management
- Prefer ReentrantLock over synchronized: Prevents pinning virtual threads
- Monitor performance: Virtual threads reduce memory usage but may increase CPU overhead
- Backward compatible: Existing code works, but may need adjustments for optimal performance


  

What is a Java Thread, Why do we need them ?
- Starting Java 21, we have two types of Threads:
  - Platform Threads (aka Java Threads until 20)
  - Virtual Threads

- Platform Threads in Java are used to run tasks in the background.
- This allows a program to execute multiple things at the same time withoutinterrupting the main thread.

```java
public class ExploreThreads {
    public static void doSomeWork() {
        log("started doSomeWork");
        CommonUtil.sleep(1000);
        log("finished doSomeWork");
    }

    public static void main(String[] args) {
        Thread.Builder.OfPlatform thread1 = Thread.ofPlatform().name("t1");
        thread1.start(() -> log("Run Task1 in the background"));

        Thread.Builder.OfPlatform thread2 = Thread.ofPlatform().name("t2");
        thread2.start(ExploreThreads::doSomeWork);
        log("Program completed");
    }
}
```

```java
public class Lec01ExploreThreads {
    public static void doSomeWork() {
        log("started doSomeWork");
        CommonUtil.sleep(1000);
        log("finished doSomeWork");
    }

    public static void main(String[] args) {
        Thread.Builder.OfPlatform thread1 = Thread.ofPlatform().name("t1");
        thread1.start(() -> log("Run Task1 in the background"));

        Thread.Builder.OfPlatform thread2 = Thread.ofPlatform().name("t2");
        thread2.start(Lec01ExploreThreads::doSomeWork);

        Thread thread3 = Thread.ofPlatform()
                .name("t3")
                .unstarted(() -> log("Run Task3 in the background"));
        thread3.start();

        log("Program completed");
    }
}
```

```java
public class Lec02VirtualThreadCreation {
    public static void main(String[] args) {
        Thread virtualThread = Thread.ofVirtual()
                .name("virtual-thread-1")
                .start(() -> {
                    System.out.println("Running in virtual thread");
                    System.out.println("Is virtual ? " + Thread.currentThread().isVirtual());
                });

        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

```java
public class Lec03VirtualThreadExecutor {

    public static final int THRAD_COUNTS = 10_000;

    public static void main(String[] args) {
        // Create an executor service with virtual threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Submit multiple tasks
            for (int i = 0; i < THRAD_COUNTS; i++) {
                int taskId = i;
                executor.submit(() -> {
                    try {
                        // Simulate I/O operation
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Task " + taskId + " running on thread: " + Thread.currentThread().threadId());
                });
            }

            // Wait for completion
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

```

```java
public class Lec04VirtualThreadsVsPlatformThreads {

    public static void heavyComputation() {
        // CPU-intensive work
        long result = 0;
        for (int i = 0; i < 1_000_000; i++) {
            result += (long) Math.sqrt(i);
        }
        System.out.println("Computation result: " + result);
    }

    public static void ioOperation() throws InterruptedException {
        // Simulating I/O operation
        Thread.sleep(1000);
        System.out.println("I/O operation completed");
    }

    private static void testPlatformThreads() throws Exception {
        long start = System.currentTimeMillis();

        Thread t1 = Thread.ofPlatform().start(() -> heavyComputation());
        Thread t2 = Thread.ofPlatform().start(() -> {
            try {
                ioOperation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.join();
        t2.join();
        System.out.println("Platform threads time: " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void testVirtualThreads() throws Exception {
        long start = System.currentTimeMillis();

        Thread t1 = Thread.ofVirtual().start(() -> heavyComputation());
        Thread t2 = Thread.ofVirtual().start(() -> {
            try {
                ioOperation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.join();
        t2.join();
        System.out.println("Virtual threads time: " + (System.currentTimeMillis() - start) + "ms");
    }


    public static void main(String[] args) throws Exception {
        System.out.println("=== Platform Threads ===");
        testPlatformThreads();

        System.out.println("\n=== Virtual Threads ===");
        testVirtualThreads();
    }
}
```

```java
public class Lec05StructuredConcurrencyExample {


    public record UserData(String userInfo, String orderInfo) {

    }

    private static String fetchUserInfo(String userId) throws InterruptedException {
        Thread.sleep(500); // Simulate API call
        return "User details for " + userId;
    }

    private static String fetchOrderInfo(String userId) throws InterruptedException {
        Thread.sleep(700); // Simulate API call
        return "Orders for " + userId;
    }

    public static UserData fetchUserAndOrderData(String userId) throws InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork subtasks
            StructuredTaskScope.Subtask<String> userTask = scope.fork(() -> fetchUserInfo(userId));
            StructuredTaskScope.Subtask<String> orderTask = scope.fork(() -> fetchOrderInfo(userId));

            // Join both subtasks
            scope.join();
            scope.throwIfFailed();

            return new UserData(userTask.get(), orderTask.get());

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            UserData result = fetchUserAndOrderData("user123");
            System.out.println("User: " + result.userInfo());
            System.out.println("Orders: " + result.orderInfo());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

```java
public class Lec05ThreadPoolsAndExecutors {
    public static class VirtualThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            return Thread.ofVirtual()
                    .name("virtual-thread-" + counter++)
                    .unstarted(r);
        }
    }

    public static void main(String[] args) {
        // Custom thread factory
        ThreadFactory factory = new VirtualThreadFactory();

        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {

            // Submit tasks
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("Task " + taskId + " executed by: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            executor.shutdown();
        }
    }
}

```

```java
public class Lec06ThreadLocalExample {
    private static final ThreadLocal<String> userContext = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        // ThreadLocal works with virtual threads but can cause memory issues
        Thread vThread = Thread.ofVirtual()
                .name("user-thread")
                .start(() -> {
                    userContext.set("User123");
                    System.out.println("Context set: " + userContext.get());
                    // Important: Clean up thread-local to prevent memory leaks
                    userContext.remove();
                });

        vThread.join();
    }
}
```

```java
public class Lec07SynchronizationAndPinPrevention {
    // Using ReentrantLock instead of synchronized blocks
    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    public void increment() {
        lock.lock();
        try {
            counter++;
            System.out.println("Counter: " + counter + " Thread: " + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }

    // BAD: This can pin the virtual thread to carrier thread
    public synchronized void badIncrement() {
        counter++;
    }

    public static void main(String[] args) throws InterruptedException {
        Lec07SynchronizationAndPinPrevention example = new Lec07SynchronizationAndPinPrevention();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                executor.submit(example::increment);
            }
        }
    }
}

```

```java
public class Lec08PerformanceComparison {
    private static final int TASK_COUNT = 10_000;
    private static final AtomicInteger completedTasks = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Performance Comparison ===");

        // Test with platform threads (thread pool)
        System.out.println("\n1. Platform Thread Pool (100 threads):");
        testWithExecutor(Executors.newFixedThreadPool(100));

        completedTasks.set(0);

        // Test with virtual threads
        System.out.println("\n2. Virtual Threads:");
        testWithExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    private static void testWithExecutor(ExecutorService executor)
            throws InterruptedException {

        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(TASK_COUNT);

        for (int i = 0; i < TASK_COUNT; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // Simulate mixed workload
                    if (taskId % 3 == 0) {
                        cpuIntensiveWork();
                    } else {
                        ioSimulation();
                    }

                    completedTasks.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Task failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks
        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        System.out.println("Completed tasks: " + completedTasks.get());
        System.out.println("Total time: " + (endTime - startTime) + "ms");
        System.out.println("Memory used: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "MB");
    }

    private static void cpuIntensiveWork() {
        long sum = 0;
        for (int i = 0; i < 10_000; i++) {
            sum += i * i;
        }
    }

    private static void ioSimulation() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
    }
}
```

```java
public class Lec09DebuggingAndMonitoring {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Virtual Thread Monitoring ===");

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Monitor before tasks
            printThreadStats(threadBean, "Before tasks");

            // Submit tasks
            for (int i = 0; i < 1000; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            Thread.sleep(1000); // Wait a bit

            // Monitor during execution
            printThreadStats(threadBean, "During execution");

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            // Monitor after completion
            printThreadStats(threadBean, "After completion");
        }
    }

    private static void printThreadStats(ThreadMXBean threadBean, String phase) {
        System.out.println("\n--- " + phase + " ---");
        System.out.println("Total threads: " + threadBean.getThreadCount());
        System.out.println("Peak threads: " + threadBean.getPeakThreadCount());

        // Get virtual thread count (Java 21+)
        System.out.println("Daemon threads: " + threadBean.getDaemonThreadCount());
    }
}
```


