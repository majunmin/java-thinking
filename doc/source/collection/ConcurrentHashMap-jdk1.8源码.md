# ConcurrentHashMap

jdk1.7  ConcurrentHashMap 采用分段锁Segment机制,实现并发更新,提高并发度,底层数据结构采用 数组 + 链表
jdk1.8 放弃了分段锁的实现使用 Node,采用 Synchronized + CAS 来实现,保证并发的安全,底层采用 数组 + 链表 + 红黑树

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210318130057.png)


## 阅读源码之前需要理解的概念

ConcurrentHashMap 不允许 null key  null value

1. table    : default null初始化发生在第一次插入操作,默认大小为16的数组,用来存储Node节点数据,扩容时大小总是2的幂次方
2. nextTable: 扩容时用到,新生成的数组,大小为原来的table 的 2 倍
  - sizeCtl :用来控制表初始化和扩容的,默认值为0,当在初始化的时候指定了大小,这会将这个大小保存在sizeCtl中,大小为数组的0.75,
             当为负的时候,说明表正在初始化或扩容
    * -1 代表table正在初始化
    * -(n+1) 代表有n个线程正在进行扩容操作
4. Node: 保存key value hash(key)值 的数据结构
```java
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val; // volatile 保证并发可见性
        volatile Node<K,V> next; // volatile 保证并发可见性
    }       
```

5. ForwardingNode:   一个特殊的Node节点,hash值为 -1，其中存储nextTable的引用
  - 只有table发生扩容的时候,`ForwardingNode`才会发挥作用,作为一个占位符放在table中表示当前节点为null或则已经被移动
 
 
6. Node  构成每个元素的基本类
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K,V> next;
}
```

7. TreeNode 红黑树节点

```java
static final class TreeNode<K,V> extends Node<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
}
```

8.  TreeBin

只存储 root 节点和 头结点 first, 不存储 key value

```java
static final class TreeBin<K,V> extends Node<K,V> {
    TreeNode<K,V> root;
    volatile TreeNode<K,V> first;
    volatile Thread waiter;
    volatile int lockState;
    // values for lockState  读写锁状态
    static final int WRITER = 1; // set while holding write lock
    static final int WAITER = 2; // set when waiting for write lock
    static final int READER = 4; // increment value for setting read lock
}
```

9. ForwardingNode

ForwardingNode 在转移的时候放在头部的节点,是一个空节点

```java
/**
 * A node inserted at head of bins during transfer operations.
 */
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
    ForwardingNode(Node<K,V>[] tab) {
        super(MOVED, null, null, null);
        this.nextTable = tab;
    }
}
```

10. ConcurrentHashMap 中几个重要的方法

```java
    // 返回  table[i] 的节点的原子操作
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
    
    // cas 设置 table[i]的值 = v
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                        Node<K,V> c, Node<K,V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
    
    // 原子的设置table[i] 的值 = v
    static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }

```

##  ConcurrentHashMap 实例化

```java
public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {

    if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    if (initialCapacity < concurrencyLevel)   // Use at least as many bins
        initialCapacity = concurrencyLevel;   // as estimated threads
    long size = (long)(1.0 + (long)initialCapacity / loadFactor);
    int cap = (size >= (long)MAXIMUM_CAPACITY) ?
        MAXIMUM_CAPACITY : tableSizeFor((int)size);
    this.sizeCtl = cap;
}
```

注意:  ConcurrentHashMap在构造函数中只会初始化`sizeCtl`值,并不会直接初始化table,*而是延缓到第一次put操作*



## ConcurrentHashMap.tab 初始化

```java
    /**
     * 初始化 table,   concurrentHashMap 可以并发执行 如何保证仅执行一次?
     * 通过 while 循环 + cas 仅执行一次 初始化操作 ,值得借鉴
     * Initializes table, using the size recorded in sizeCtl.
     */
    private final Node<K,V>[] initTable() {

        Node<K,V>[] tab; int sc;

        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0) // sizeCtl 小于 0表示有其他线程在初始化表或扩容
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {  // cas 设置 sizeCtl = -1, 仅有一个线程会成功设置
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);  // sc 是扩容容量限制threshold   n - n/4 = 3n/4 = 0.75n
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```

`initTable()` 初始化 table,多线程并发执行initTable,保证仅有一个线程 执行初始化操作 (cas + while)
通过 cas 设置 sc = -1, 只有一个线程会设置成功,其他线程 会让出cpu时间片
其他线程会 执行 `Thread.yield()` 让出cpu时间片,之后退出循环




## 1. put

```java
    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // ConcurrentHashMap 不允许 nullKey nullValue
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;

        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            // 如果 tab 位置第一次插入元素,      否则 同步(synchronized)的插入到 链表 或者 红黑树
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))  //   通过cas设置 tab[0]的值
                    break;                   // no lock when adding to empty bin
            }
            // f == -1,当前节点是 ForwardingNode,  表示当前 有其他线程 正在扩容, 那么帮助进行扩容
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                // 如果以上条件都不满足,表示存在hash冲突, 采用同步的方式插入元素 (链表 or 红黑树)
                synchronized (f) {
                    ...
                }
                // binCount ！= 0 表示 向链表或者 红黑树插入节点成功,
                // binCount == 0 表示 put一个新节点是所在的 某个桶的首节点
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null) // 不会调用到下面的addCount (扩容)
                        return oldVal;
                    break;
                }
            }
        }
        // 只有新增1个元素的时候才会调用这个方法
        addCount(1L, binCount); //统计size，CAS更新baseCount,并且检查是否需要扩容
        return null;
    }

    // 获取  tab在索引 i 处的值
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }

    
    // cas 设置 tab[i] = v(if c = tab[i] == null )
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                        Node<K,V> c, Node<K,V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
```


1. `spread()` 计算 hash值

2. 获取 tab中索引位置
  `(n - 1) & hash)`

3. `tabAt()` 获取table中对应索引的元素f(Node)
  Doug Lea采用`Unsafe.getObjectVolatile`来获取，也许有人质疑,直接table[index]不可以么,为什么要这么复杂?
  
  在 JMM 中, 我们已经知道每个线程都有一个工作内存,里面存储着table的副本,虽然table是volatile修饰的,
  但不能保证线程每次都拿到table中的最新元素,`Unsafe.getObjectVolatile`可以直接获取指定内存的数据,保证了每次拿到数据都是最新的
  
  volatile修饰引用对象,表示这个引用 是 volatile保护的, 引用所指向的值 改变是不受volatile保护的, 
  所以一般情况下, volatile 修饰的值 是基本类型
  
4. 如果 (f = tabAt(tab, i = (n - 1) & hash)) == null,说明table中这个位置第一次插入元素,利用`Unsafe#compareAndSwapObject`插入Node节点
  - 如果CAS成功,说明Node节点已经插入,随后 `addCount(1L, binCount)`会检查当前容量是否需要进行扩容
  - 如果CAS失败,说明有其它线程提前插入了节点，自旋重新尝试在这个位置插入节点
5. 如果f的hash值为-1，说明当前f是`ForwardingNode`节点,意味有其它线程正在扩容,则一起进行扩容操作
6. 其余情况把新的Node节点按链表或红黑树的方式插入到合适的位置,这个过程采用同步内置锁实现并发,代码如下:
```java
    synchronized (f) {
        if (tabAt(tab, i) == f) {  // 判断f是否改变 f如果改变再次循环
            if (fh >= 0) {  // fh > 0 表示该节点是 链表 (红黑树或者正在转移都为负数)
                binCount = 1;
                for (Node<K,V> e = f;; ++binCount) {
                    K ek;
                    //这里涉及到相同的key进行put就会覆盖原先的value
                    if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                        oldVal = e.val;
                        if (!onlyIfAbsent)
                            e.val = value;
                        break;
                    }
                    Node<K,V> pred = e;
                    if ((e = e.next) == null) {
                        pred.next = new Node<K,V>(hash, key, value, null);
                        break;
                    }
                }
            }
            else if (f instanceof TreeBin) {
                Node<K,V> p;
                binCount = 2;
                // 红黑树 旋转插入
                if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key, value)) != null) {
                    oldVal = p.val;
                    if (!onlyIfAbsent)
                        p.val = value;
                }
            }
        }
    }
```

7. `addCount(1L, binCount)` 方法会检查当前容量是否需要进行扩容





## 2. tab 扩容


当 table 容量不足的时候,即table 的元素数量达到了容量阈值 sizeCtl,需要对 table 扩容.扩容阶段可以分为两部分
  - 1. 构建一个 nextTable,容量为 table的 2 倍
  - 2. 把table的数据复制到 nextTable 中

ConcurrentHashMap 是支持多线程并发插入的,数据复制也支持多线程并发复制,性能自然也会高
ConcurrentHashMap 扩容是通过  transfer() 实现的 ,调用 transfer的地方有 `tryPresize()`  `helpTransfer()`  `addCount()`
- tryPresize() 是在`treefiybin()` 和`putAll()`中调用，`treeIfybin()`主要是在put添加元素完之后,判断该数组节点相关元素是不是已经超过8个的时候,如果超过则会调用这个方法来扩容数组或者把链表转为树
- helpTransfer() 是在当一个线程要对table中元素进行操作的时候,如果**检测到节点的HASH值为MOVED的时候**,就会调用`helpTransfer()`,在`helpTransfer()`中再调用 transfer()`来帮助完成数组的扩容
- addCount() 是在当对数组进行操作,使得数组中存储的元素个数发生了变化的时候会调用的方法


```java
    /**
     * Adds to count, and if table is too small and not already
     * resizing, initiates transfer. If already resizing, helps
     * perform transfer if work is available.  Rechecks occupancy
     * after a transfer to see if another resize is already needed
     * because resizings are lagging additions.
     *
     * @param x the count to add
     * @param check if <0, don't check resize, if <= 1 only check if uncontended
     */
    private final void addCount(long x, int check) {
        CounterCell[] as; long b, s;
        if ((as = counterCells) != null ||
            !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            CounterCell a; long v; int m;
            boolean uncontended = true;
            if (as == null || (m = as.length - 1) < 0 ||
                (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
                !(uncontended =
                  U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                fullAddCount(x, uncontended);
                return;
            }
            if (check <= 1)
                return;
            s = sumCount();
        }
        if (check >= 0) {
            Node<K,V>[] tab, nt; int n, sc;
           // 这个地方跟上面初始 tab的地方一样 ,通过 cas + sc 来保证仅有一个线程初始化 nextTable
            while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                   (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
                s = sumCount();
            }
        }
    }
```



返回  > c 的 2的幂次方

```java

    // tableSizeFor(size + (size >>> 1) + 1);
    // 如果初始容量 = 100那么 tableSizeFor(100 + 50 + 1) == 256
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```


## 3. get

```java
    public V get(Object key) {
        Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            }
            else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {
                if (e.hash == h &&
                    ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }
```


读取操作不需要同步
1. 判断 table 是否为空, 或者 `table[i]` 是否为空
2. 计算 hash(key),并获取指定table中指定位置的Node节点,通过遍历链表或则树结构找到对应的节点,返回 value


## 4. ConcurrentHashMap 的 同步机制


1. 读取操作,get()中根本没有使用同步机制,也没有使用unsafe方法,所以读操作是支持并发操作的
2. 写操作 扩容
  - 扩容的触发条件
    * 当一个hash桶中元素已经超过了 8个，但是 table的长度小于64,就会触发扩容
    * 当Map中的Node个数达到了 sizeCtl 时,会触发扩容 调用 transfer()
  - 扩容时可不可以进行读写操作
    * 当进行数组扩容时,这个hash桶还没有被处理(还没有被设置为ForwardingNode)时，可以进行设置操作
    * 如果当前hash桶已经被处理,那么当前线程也会进入到扩容操作中去(helpTransfer())
3. 多线程如何进行同步处理
  - 在ConcurrentHashMap中,同步处理主要通过Synchronized + unsafe(CAS) 两种方式来完成
  - 在取得sizeCtl,获取某个位置的Node的时候,使用的都是 unsafe(cas),达到并发安全的目的
  - 在需要设置某个节点的时候,会通过synchronized 同步机制来锁定该位置的节点
  - 在数组扩容的时候,通过处理步长和 fordingNode节点(Node.hash = MOVED)来达到并发安全的目的
  - 档把某个位置的节点 复制到 扩容后的table,通过 synchronized同步机制来保证线程安全





ConcurrentHashMap 是一个并发散列表的实现,它允许完全并发的读取,并且支持给定数量的并发更新
相比于hashTable or 同步包装的 hashMap, 使用一个全局锁来同步不同线程间的访问,同一个时间点只有一个线程持有锁,会导致容器的访问串行化

JDK1.7 中使用 segment + ReentrantLock 的实现方式,使多个线程在不同的Segment上并发写操作不会发现阻塞行为
Jdk1.8 中直接使用了 synchronized





------------------------------------
[ConcurrentHashMap源码分析(1.8)](https://www.cnblogs.com/zerotomax/p/8687425.html#go0)


















