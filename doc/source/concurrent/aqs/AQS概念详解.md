# AQS 概念

> 前置知识
> 
> 1. unsafe
> 2. CAS + 自旋锁
> 3. CLH 队列
> 4. LockSupport#park()  LockSupport#unpark()
> 5. 线程中断 interupt()
> 



Java 中大部分的同步类 都是通过 AQS 实现的(Lock, Semaphore, ReentrantLock)都是基于AbstractQueuedSynchronizer(简称为AQS)实现的.

> AQS是一种提供了原子式管理同步状态、阻塞和唤醒线程功能以及队列模型的简单框架
> `AbstractQueuedSynchronizer` 是JUC 中通过 Sync Queue(并发安全的 CLH Queue), Condition Queue(普通的 list) ,
>  volatile 变量 `state` 提供的 控制线程获取统一资源(state) 的 Synchronized 工具.
>
> 内部维护了一个变量 state, 需要通过 CAS 的方式设值
> AbstractQueuedSynchronizer 子类应该作为非public一个内部帮助类
> 支持排它锁和共享锁两种模式(exclusive mode and a shared mode)
> 该类的一个内部类 AbstractQueuedSynchronizer.ConditionObject,仅能用于排它锁模式下
>
> 用法:
> * tryAcquire(int)
> * tryRelease(int)
> * tryAcquireShared(int)
> * tryReleaseShared(int)
> * isHeldExclusively()
>


## 2. AbstractQueuedSynchronized 属性
```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {


    /**
     * DummyNode  
     * 同步队列的头结点
     * 仅可以通过 setHead() 方法修改
     */
    private transient volatile Node head;

    /**
     * 同步队列的尾结点
     * 仅可以通过 enq() 添加新的 节点
     */
    private transient volatile Node tail;

    /**
     * 通过状态 state 
     * 可以通过 getState(), setState() and/or compareAndSetState()
     * 其含义  不是有 AQS维护, 可以由子类定义  (可重入...| 读写锁... | ...)
     */
    private volatile int state;

}
```


## 3. AbstractQueuedSynchronized 基本的方法


### acquire()

```java

    /**
     * Acquires in exclusive mode, ignoring interrupts.  Implemented
     * by invoking at least once {@link #tryAcquire},
     * returning on success.  Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link
     * #tryAcquire} until success.  This method can be used
     * to implement method {@link Lock#lock}.
     *
     * 排它锁的 获取流程
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     */
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```


###   addWaiter()

```java

    /**
     * Creates and enqueues node for current thread and given mode.
     *  创建一个节点 放入同步队列(enq())
     *
     *  共享模式下: nextWaiter = mode = Node.SHARED
     *  排他模式下: nextWaiter = mode = Node.EXCLUSIVE
     *
     * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
     * @return the new node
     */
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        // 走到这里说明  队列为空(tail == null) || cas失败
        enq(node);
        return node;
    }
```


###  enque()

```java

    /**
     * 走到这里有两种情况
     * 1. 等待队列为空: 初始哈队列
     * 2. cas 竞争入队失败:  自旋的方式入队(CAS设置tail过程中竞争一次竞争不到,我就多次竞争,总会排到的)
     *
     * Inserts node into queue, initializing if necessary. See picture above.
     * @param node the node to insert
     * @return node's predecessor
     */
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```


### aquireQueued()

```java

    /**
     *  排它锁调用 申请入队
     *  线程挂起和 被唤醒后去获取锁的逻辑
     *
     * Acquires in exclusive uninterruptible mode for thread already in
     * queue. Used by condition wait methods as well as acquire.
     *
     * @param node the node
     * @param arg the acquire argument
     * @return {@code true} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                // 如果 node的前驱节点是 head, 就是尝试去获取锁, 获取锁成功后就将其前驱节点(头结点打掉)
                // why? 性能
                // 如果头结点是 head, 去竞争获取锁,如果获取成功就减少了一次 入队阻塞操作
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC   孤立头结点  快速GC
                    failed = false;
                    return interrupted;
                }
                // 到这里，说明上面的if分支没有成功，要么当前node本来就不是队头，
                // 要么就是tryAcquire(arg) 没有抢锁成功
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            // 什么时候 failed 会为 true???
            // tryAcquire() 抛异常的情况
            if (failed)
                cancelAcquire(node);
        }
    }
```


### shouldParkAfterFailedAcquire(Node pred, Node node)

```java

    /**
     * Checks and updates status for a node that failed to acquire.
     * Returns true if thread should block. This is the main signal
     * control in all acquire loops.  Requires that pred == node.prev.
     *
     * @param pred node's predecessor holding status
     * @param node the node
     * @return {@code true} if thread should block
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             */
            return true;
        if (ws > 0) {
            /**
             * waitStatus > 0 表示前驱节点取消了排队,那么就需要将前驱节点跳过(往前遍历找个好爹)
             *
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /**
             * 走到这里说明 前驱节点的waitStatus = 0, 那么需要将前驱节点的 waitStatus = Node.SIGNAL
             *
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
```


###  parkAndCheckInterrupt()

```java

    /**
     * Convenience method to park and then check if interrupted
     *
     * @return {@code true} if interrupted
     */
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
```



### release()

```java

    /**
     * Releases in exclusive mode.  Implemented by unblocking one or
     * more threads if {@link #tryRelease} returns true.
     * This method can be used to implement method {@link Lock#unlock}.
     * <p>
     * 释放锁的流程:
     *   <p>1. 将 AQS持有锁线程置为 null
     *   <p>2. 将 AQS state 置为 0
     *   <p>3. 唤醒同步队列头结点的下一个节点(唤醒正在等待线程)
     *
     * @param arg the release argument.  This value is conveyed to
     *        {@link #tryRelease} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @return the value returned from {@link #tryRelease}
     */
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                // 要唤醒那个节点就把 哪个节点的前驱节点传入(这里传入头结点)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```


### unparkSuccessor(Node node)

```java

    /**
     * Wakes up node's successor, if one exists.
     * <p>
     * 唤醒node的 后继节点, 一般情况下 node 是 head节点
     *
     * @param node the node
     */
    private void unparkSuccessor(Node node) {
        /*
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling.  It is OK if this
         * fails or if status is changed by waiting thread.
         */
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        /**
         * Thread to unpark is held in successor, which is normally
         * just the next node.  But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor.
         * <p>
         *
         *   有可能头结点的后继节点取消了等待(waitStatus == 1)
         *    从后往前找,找到waitStatus < 0 的 所有节点中排在最前面的
         */
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            // 从尾结点向前找到 最后一个waitStatus <= 0 的节点
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            // 真正的唤醒线程
            LockSupport.unpark(s.thread);
    }
```



### hasQueuedPredecessors()

```java
     /**
     * @return {@code true} if there is a queued thread preceding the
     *         current thread, and {@code false} if the current thread
     *         is at the head of the queue or the queue is empty
     * @since 1.7
     */
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }
```



## 4. waitQueue 同步队列  条件队列

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210409130154.png)

队列节点 Node

```java
    static final class Node {
        /** flag 共享模式 */
        static final Node SHARED = new Node();
        /**flag 排他模式 */
        static final Node EXCLUSIVE = null;

        /** waitStatus  取消 */
        static final int CANCELLED =  1;
        /** waitStatus 表示当前节点的后继节点等待被唤醒 */
        static final int SIGNAL    = -1;
        /** waitStatus 是以条件节点 ConditionNode */
        static final int CONDITION = -2;
        /**
         * waitStatus 释放共享锁时 的一个传播的概念
         */
        static final int PROPAGATE = -3;

        volatile int waitStatus;

        /**
         * 前驱节点 维护一个双向链表
         */
        volatile Node prev;

        /**
         * 后继节点 维护一个双向链表
         */
        volatile Node next;

        /**
         * 当前节点 对应的线程
         */
        volatile Thread thread;

        /**
         * 条件队列 维护一个 链表
         * Condition 仅能在排他模式下使用
         */
        Node nextWaiter;

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```


Condition



## 5. Java 中断  and InterruptedException


> 在之前的文章中，我们接触了大量的中断，这边算是个总结吧。如果你完全熟悉中断了，没有必要再看这节，本节为新手而写。

### 线程中断

首先，我们要明白，中断不是类似 linux 里面的命令 kill -9 pid，不是说我们中断某个线程，这个线程就停止运行了。中断代表线程状态，每个线程都关联了一个中断状态，是一个 true 或 false 的 boolean 值，初始值为 false。

> Java 中的中断和操作系统的中断还不一样，这里就按照**状态**来理解吧，不要和操作系统的中断联系在一起

关于中断状态，我们需要重点关注 Thread 类中的以下几个方法：

```java
// Thread 类中的实例方法，持有线程实例引用即可检测线程中断状态
public boolean isInterrupted() {}

// Thread 中的静态方法，检测调用这个方法的线程是否已经中断
// 注意：这个方法返回中断状态的同时，会将此线程的中断状态重置为 false
// 所以，如果我们连续调用两次这个方法的话，第二次的返回值肯定就是 false 了
public static boolean interrupted() {}

// Thread 类中的实例方法，用于设置一个线程的中断状态为 true
public void interrupt() {}
```

我们说中断一个线程，其实就是设置了线程的 interrupted status 为 true，至于说被中断的线程怎么处理这个状态，那是那个线程自己的事。如以下代码：

```java
while (!Thread.interrupted()) {
   doWork();
   System.out.println("我做完一件事了，准备做下一件，如果没有其他线程中断我的话");
}
```

> 这种代码就是会响应中断的，它会在干活的时候先判断下中断状态，不过，除了 JDK 源码外，其他用中断的场景还是比较少的，毕竟 JDK 源码非常讲究。

当然，中断除了是线程状态外，还有其他含义，否则也不需要专门搞一个这个概念出来了。

如果线程处于以下三种情况，那么当线程被中断的时候，能自动感知到：

1. 来自 Object 类的 wait()、wait(long)、wait(long, int)，

   来自 Thread 类的 join()、join(long)、join(long, int)、sleep(long)、sleep(long, int)

   > 这几个方法的相同之处是，方法上都有: throws InterruptedException
   >
   > 如果线程阻塞在这些方法上（我们知道，这些方法会让当前线程阻塞），这个时候如果其他线程对这个线程进行了中断，那么这个线程会从这些方法中立即返回，抛出 InterruptedException 异常，同时重置中断状态为 false。

2. 实现了 InterruptibleChannel 接口的类中的一些 I/O 阻塞操作，如 DatagramChannel 中的 connect 方法和 receive 方法等

   > 如果线程阻塞在这里，中断线程会导致这些方法抛出 ClosedByInterruptException 并重置中断状态。

3. Selector 中的 select 方法，参考下我写的 NIO 的文章

   > 一旦中断，方法立即返回

对于以上 3 种情况是最特殊的，因为他们能自动感知到中断（这里说自动，当然也是基于底层实现），**并且在做出相应的操作后都会重置中断状态为 false**。

那是不是只有以上 3 种方法能自动感知到中断呢？不是的，如果线程阻塞在 LockSupport.park(Object obj) 方法，也叫挂起，这个时候的中断也会导致线程唤醒，但是唤醒后不会重置中断状态，所以唤醒后去检测中断状态将是 true。

### InterruptedException 概述

它是一个特殊的异常，不是说 JVM 对其有特殊的处理，而是它的使用场景比较特殊。通常，我们可以看到，像 Object 中的 wait() 方法，ReentrantLock 中的 lockInterruptibly() 方法，Thread 中的 sleep() 方法等等，这些方法都带有 `throws InterruptedException`，我们通常称这些方法为阻塞方法（blocking method）。

阻塞方法一个很明显的特征是，它们需要花费比较长的时间（不是绝对的，只是说明时间不可控），还有它们的方法结束返回往往依赖于外部条件，如 wait 方法依赖于其他线程的 notify，lock 方法依赖于其他线程的 unlock等等。

当我们看到方法上带有 `throws InterruptedException` 时，我们就要知道，这个方法应该是阻塞方法，我们如果希望它能早点返回的话，我们往往可以通过中断来实现。

除了几个特殊类（如 Object，Thread等）外，感知中断并提前返回是通过轮询中断状态来实现的。我们自己需要写可中断的方法的时候，就是通过在合适的时机（通常在循环的开始处）去判断线程的中断状态，然后做相应的操作（通常是方法直接返回或者抛出异常）。当然，我们也要看到，如果我们一次循环花的时间比较长的话，那么就需要比较长的时间才能**感知**到线程中断了。

### 处理中断

一旦中断发生，我们接收到了这个信息，然后怎么去处理中断呢？本小节将简单分析这个问题。

我们经常会这么写代码：

```java
try {
    Thread.sleep(10000);
} catch (InterruptedException e) {
    // ignore
}
// go on 
```

当 sleep 结束继续往下执行的时候，我们往往都不知道这块代码是真的 sleep 了 10 秒，还是只休眠了 1 秒就被中断了。这个代码的问题在于，我们将这个异常信息吞掉了。（对于 sleep 方法，我相信大部分情况下，我们都不在意是否是中断了，这里是举例）

AQS 的做法很值得我们借鉴，我们知道 ReentrantLock 有两种 lock 方法：

```java
public void lock() {
    sync.lock();
}

public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
```

前面我们提到过，lock() 方法不响应中断。如果 thread1 调用了 lock() 方法，过了很久还没抢到锁，这个时候 thread2 对其进行了中断，thread1 是不响应这个请求的，它会继续抢锁，当然它不会把“被中断”这个信息扔掉。我们可以看以下代码：

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        // 我们看到，这里也没做任何特殊处理，就是记录下来中断状态。
        // 这样，如果外层方法需要去检测的时候，至少我们没有把这个信息丢了
        selfInterrupt();// Thread.currentThread().interrupt();
}
```

而对于 lockInterruptibly() 方法，因为其方法上面有 `throws InterruptedException` ，这个信号告诉我们，如果我们要取消线程抢锁，直接中断这个线程即可，它会立即返回，抛出 InterruptedException 异常。

在并发包中，有非常多的这种处理中断的例子，提供两个方法，分别为响应中断和不响应中断，对于不响应中断的方法，记录中断而不是丢失这个信息。如 Condition 中的两个方法就是这样的：

```java
void await() throws InterruptedException;
void awaitUninterruptibly();
```

> 通常，如果方法会抛出 InterruptedException 异常，往往方法体的第一句就是：
```java
public final void await() throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
 	...... 
}
```

熟练使用中断，对于我们写出优雅的代码是有帮助的，也有助于我们分析别人的源码。








## 看源码

- ReentrantLock
- CountDownLatch
- Semaphore
- ReentrantReadWriteLock

1. 公平锁和非公平锁
2. state 字段的理解
3. 同步队列的理解
4. 排它锁和共享锁的理解
5. 

```
AbstractOwnableSynchronizer (java.util.concurrent.locks)
    AbstractQueuedSynchronizer (java.util.concurrent.locks)
        Sync in CountDownLatch (java.util.concurrent)
        Worker in ThreadPoolExecutor (java.util.concurrent)
        Sync in ReentrantLock (java.util.concurrent.locks)
            FairSync in ReentrantLock (java.util.concurrent.locks)
            NonfairSync in ReentrantLock (java.util.concurrent.locks)
        Sync in ReentrantReadWriteLock (java.util.concurrent.locks)
            FairSync in ReentrantReadWriteLock (java.util.concurrent.locks)
            NonfairSync in ReentrantReadWriteLock (java.util.concurrent.locks)
        Sync in Semaphore (java.util.concurrent)
            FairSync in Semaphore (java.util.concurrent)
            NonfairSync in Semaphore (java.util.concurrent)

```


## 总结


并发环境下,加锁和解锁需要三个部件协调 

同步阻塞队列   :  FIFO队列(链表) CLH
锁状态(state)  :  
线程的阻塞和解阻塞: AQS 中采用了 LockSupport.park(thread) 来挂起线程，用 unpark 来唤醒线程。


![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210409212013.png)


--------------------

[一行一行源码分析清楚 AbstractQueuedSynchronizer (二)](https://javadoop.com/post/AbstractQueuedSynchronizer-2)
