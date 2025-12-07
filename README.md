# Modern-Java---Multithreading-in-Java-using-Virtual-Threads

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
