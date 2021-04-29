# Java 锁的使用



## 乐观锁 vs 悲观锁

### AtomicInteger



```java
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;


    private volatile int value;
    
    public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }

    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }
    ----------------------
    
    // Unsafe.java
    public final int getAndAddInt(Object o, long offset, int delta) {
       int v;
       do {
           v = getIntVolatile(o, offset);
       } while (!compareAndSwapInt(o, offset, v, v + delta));
       return v;
    }


}
```
`unsafe`:  获取并操作内存的数据
`valueOffset`: 存储value在AtomicInteger中的偏移量
`value`: 存储AtomicInteger的int值,该属性需要借助volatile关键字保证其在线程间是可见的




CAS 虽然很高效,但是也会有以下三个问题

- ABA问题
  cas再操作值的时候会检查与内存值相比是否有变化,内存值有变化才会执行操作, 如果 内存值变化路径为  A -> B -> A,
  CAS 检查值没有发生变化,但实际上是有变化的。 解决方案是加上版本号  1A -> 2B -> 3A
  * JDK从1.5开始提供了 `AtomicStampedReference` 类来解决ABA问题,具体操作封装在`compareAndSet()`中.`compareAndSet()`首先检查当前引用和当前标志与预期引用和预期标志是否相等,如果都相等,则以原子方式将引用值和标志的值设置为给定的更新值          
- 循环时间长开销大 耗费cpu性能
  CAS 操作如果一直不成功,就会一直自旋循环等待,耗费CPU性能
- 只能 保证一个共享变量的 线程安全
  对于一个共享变量,CAS能够保证原子性,对于对多个共享变量操作,cas是无法保证原子性的
  * jdk1.5 提供了 AtomicReference, 可以将多个共享变量封装到一个对象里面，对这一个对象进行CAS操作











## 自旋锁 vs 适应性自旋锁





## 无锁  偏向锁  轻量级锁  重量级锁


锁可以升级但不能降级,意味着偏向锁升级成轻量级锁后不能降级成偏向锁.这种锁升级却不能降级的策略:目的是为了提高获得锁和释放锁的效率


![锁流程状态升级](IdeaProjects/java-thinking/img/锁流程状态.png)

偏向锁 通过 markWord 解决加锁问题,避免 CAS操作.
轻量级锁 通过 `CAS + 自旋` 解决加锁问题, 避免线程阻塞和唤醒 带来的开销
重量级锁 是除了拥有锁的线程以外其他线程都阻塞




## 公平锁 非公平锁



公平锁: 多个线程按照申请锁的顺序来获取锁,线程直接进入队列中排队,队列中的第一个线程才能获得锁
  - 公平锁的优点是等待锁的线程不会饿死
  - `缺点是整体吞吐效率相对非公平锁要低`,`等待队列中除第一个线程以外的所有线程都会阻塞`,`CPU唤醒阻塞线程的开销比非公平锁大`
非公平锁: 多个线程加锁时直接尝试获取锁,获取不到才会到等待队列的队尾等待.
  - 非公平锁的优点是可以`减少唤起线程的开销`,`整体的吞吐效率高`,因为线程有几率不阻塞直接获得锁,CPU不必唤醒所有线程。
  - 缺点是`处于等待队列中的线程可能会饿死`,或者等很久才会获得锁
  

![公平锁和非公平锁](IdeaProjects/java-thinking/img/公平锁和非公平锁.png)



## 可重入锁  不可重入锁

可重入锁又名递归锁,是指同一个线程在外层方法获取锁的时候,在进入该线程的内部方法会自动获得锁(前提锁对象得是同一个对象或者class),不会因为之前已经获取过还没释放而阻塞
Java中 `ReentrantLock` 和 `synchronized` 都是可重入锁,可重入锁的一个优点是可一定程度避免死锁







## 共享锁  排它锁

(可以同步对比下 mysql innodb的 共享锁 和 排它锁)
`ReentrantReadWriteLock`


ReentrantReadWriteLock.state 读写锁状态计数
![读写锁状态计数](IdeaProjects/java-thinking/img/读写锁状态.png)


```java
    protected final boolean tryAcquire(int acquires) {
        /*
         * Walkthrough:
         * 1. If read count nonzero or write count nonzero
         *    and owner is a different thread, fail.
         * 2. If count would saturate, fail. (This can only
         *    happen if count is already nonzero.)
         * 3. Otherwise, this thread is eligible for lock if
         *    it is either a reentrant acquire or
         *    queue policy allows it. If so, update state
         *    and set owner.
         */
        Thread current = Thread.currentThread();
        int c = getState();           // 获取当前锁个数
        int w = exclusiveCount(c);    // 获取 排它锁的 个数
        if (c != 0) {                 // 如果有线程持有了锁
            // (Note: if c != 0 and w == 0 then shared count != 0)
            if (w == 0 || current != getExclusiveOwnerThread())      // 如果写线程数w==0(换言之存在读锁) 或者持有锁的线程不是当前线程就返回失败
                return false;
            if (w + exclusiveCount(acquires) > MAX_COUNT)    // 如果写入锁的数量大于最大数(65535 = 2^16-1)就抛出一个Error
                throw new Error("Maximum lock count exceeded");
            // Reentrant acquire
            setState(c + acquires);
            return true;
        }
        if (writerShouldBlock() ||                    //// 如果当且写线程数w==0,并且当前线程需要阻塞那么就返回失败;或者如果通过CAS增加写线程数失败也返回失败
            !compareAndSetState(c, c + acquires))
            return false;
        setExclusiveOwnerThread(current);     // 如果c=0，w=0或者c>0，w>0(重入), 则设置当前线程或锁的拥有者
        return true;
    }


    // java 的位运算  
    static final int SHARED_SHIFT   = 16;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

    /** Returns the number of shared holds represented in count  */
    static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
    /** Returns the number of exclusive holds represented in count  */
    static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```






































------------
[不可不说的Java"锁"事](https://tech.meituan.com/2018/11/15/java-lock.html)