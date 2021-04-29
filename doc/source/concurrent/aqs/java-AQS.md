# AQS (AbstractQueuedSynchronizer)

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
> 
> 
> 
> 


## AQS 等待队列

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210409130154.png)



AbstractQueuedSynchronizer 的实现类
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

![](IdeaProjects/java-thinking/img/AQS的实现类.jpg)


```
1. 内部含有两条 Queue(Sync Queue, Condition Queue)
2. AQS 内部定义获取锁(acquire), 释放锁(release)的主逻辑, 子类实现响应的模版方法即可
3. 支持共享和独占两种模式(共享模式时只用 Sync Queue, 独占模式有时只用 Sync Queue, 但若涉及 Condition, 则还有 Condition Queue); 独占是排他的.
4. 支持 不响应中断获取独占锁(acquire), 响应中断获取独占锁(acquireInterruptibly), 超时获取独占锁(tryAcquireNanos);
       不响应中断获取共享锁(acquireShared), 响应中断获取共享锁(acquireSharedInterruptibly), 超时获取共享锁(tryAcquireSharedNanos);
5. 在子类的 `tryAcquire`, `tryAcquireShared` 中实现公平与非公平的区分
```

为什么使用 unsafe 类？

主要使用的 是 unsafe类的 cas操作



## ReentrantLock



### 公平锁和非公平锁只有两处不同

1. 非公平锁在调用 lock() 后会调用`CAS`进行一次抢锁,如果这个时候恰巧没有被占用,那么就直接获取到锁并返回了
2. 非公平锁在 `CAS` 失败后,和公平锁一样会进入到 `tryAcquire()`,在`tryAcquire()` 中, 
   如果发现锁在这个时候被释放了,非公平锁就会 CAS 直接进行抢锁
   但是公平锁会调用 `hasQueuedPredecessors()` 判断同步队列里面是否有节点在等待锁,有就排队
   


## Condition


![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210409223647.png)

1. 条件队列和阻塞队列的节点，都是 Node 的实例，因为条件队列的节点是需要转移到阻塞队列中去的；
2. 我们知道一个 ReentrantLock 实例可以通过多次调用 `newCondition()` 来产生多个 Condition 实例，这里对应 condition1 和 condition2. *注意:ConditionObject 只有两个属性 firstWaiter 和 lastWaiter*
3. 每个 condition 有一个关联的条件队列,如线程 1 调用 `condition1.await()` 即可将当前线程 1 包装成 Node 后加入到条件队列中,然后阻塞在这里,不继续往下执行,条件队列是一个单向链表
4. 调用 `condition1.signal()`触发一次唤醒,此时唤醒的是队头,会将condition1 对应的条件队列的 firstWaiter(队头) 移到阻塞队列的队尾,等待获取锁,获取锁后 await() 才能返回,继续往下执行
   
























-------------------------------
[从ReentrantLock的实现看AQS的原理及应用](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html)
[](https://www.cnblogs.com/dennyzhangdd/p/7218510.html)



