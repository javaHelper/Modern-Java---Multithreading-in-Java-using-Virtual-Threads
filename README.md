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
