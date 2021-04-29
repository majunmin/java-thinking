# JMM


jdk8 内存模型图
![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210408223411.png)

JDK 8 开始把类的元数据放到本地堆内存(native heap)中,这一块区域就叫 Metaspace(元空间)

JDK8之前的内存模型图
![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210408223505.png)

> hotSpotVM 中 , 永久代用于存放 类和方法的元信息,以及常量池(Class method 字符串常量池 常量), 每当类被加载的时候,他的元数据都会存放到 永久代.
> 永久代大小是有限制的,如果加载的类过多,会导致永久代溢出,即万恶的 ` java.lang.OutOfMemoryError: PermGen`,为此需要进行 JVM调优

jdk8 为什么移除了 永久代的概念?
1. 由于 PermGen 内存经常会溢出，引发恼人的 `java.lang.OutOfMemoryError: PermGen`,因此 JVM 的开发者希望这一块内存可以更灵活地被管理,不要再经常出现这样的 OOM
2. 移除 PermGen 可以促进 HotSpot JVM 与 JRockit VM 的融合，因为 JRockit 没有永久代.
3. 字符串存放在永久代,容易出现溢出和性能问题
4. 永久代为GC带来不必要的复杂度,且永久代的回收效率偏低

根据上面的各种原因,PermGen 最终被移除, **方法区移至 Metaspace，字符串常量移至 Java Heap**

## 虚拟机栈


## java Heap



## 方法区



## 直接内存

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210408224902.png)



## volatile

volatile语义
1. 禁止指令重排序
2. 一个线程对变量的修改对其他线程是立即可见的

> `内存屏障(Memory Barrier)`
> 内存屏障,又称内存栅栏,是一个CPU指令, 它的作用有两个
> 一是保证特定操作的执行顺序
> 二是保证某些变量的内存可见性(利用该特性实现volatile的内存可见性)
> 
> 由于编译器和处理器都能执行指令重排优化.如果在指令间插入一条Memory Barrier则会告诉编译器和CPU,不管什么指令都不能和这条Memory Barrier指令重排序, 也就是说通过插入内存屏障禁止在内存屏障前后的指令执行重排序优化。
> Memory Barrier的另外一个作用是强制刷出各种CPU的缓存数据,因此任何CPU上的线程都能读取到这些数据的最新版本.
> 总之,volatile变量正是通过内存屏障实现其在内存中的语义,即可见性和禁止重排优化





## 概念区分
1. http://tangxman.github.io/2015/07/27/the-difference-of-java-string-pool/
2. https://blog.csdn.net/qq_26222859/article/details/73135660























