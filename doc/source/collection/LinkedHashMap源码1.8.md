# LinkedHashMap 源码

LinkedHashMap  是一个关联数组 哈希表,他是非线程安全的,允许 nullKey nullValue

`LinkedHashMap extends HashMap`, 内部维护了一个双向链表,在每次`插入数据`或者`访问/修改数据`,时都会`增加节点`或者`调整链表的节点顺序`
以决定迭代输出时的顺序

for default,遍历时是按照结点插入顺序,这也是与hashMap 最大的区别
也可以通过构造函数时传入 accessOrder参数,使得其遍历顺序是按照范文的顺序输出

因继承自HashMap,所以HashMap上文分析的特点,除了输出无序,其他LinkedHashMap都有,比如`扩容的策略`,`哈希桶长度`一定是2的N次方等等

> 通过设置 accessOrder=true 可以实现一个 lruCache
>



## 2. putVal

LinkedHashMap 并没有重写 HashMap的 put方法,而是 重写了 `newNode()` 创建节点的方法
`newNode()` 会在 `HashMap#putVal` 里面调用,
`LinkedHashMap#newNode()`,在每次构建新节点时,通过`linkNodeLast(p)` 将新节点链接在内部双向链表的尾部

```java
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
            new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }

    // link at the end of list
    // 将 新加入的节点插入到双向链表尾部
    private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        // last == null 表示原来集合是空的, 直接将    
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }
```


LinkedHashMap 实现了 HashMap中的 三个回调方法
```java
   // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }
```


```java
    // 当节点 从 hashMap 中的 entry中删除后,也需要从双向链表中移除
    void afterNodeRemoval(Node<K,V> e) { // unlink
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.before = p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a == null)
            tail = b;
        else
            a.before = b;
    }

    void afterNodeInsertion(boolean evict) { // possibly remove eldest
        LinkedHashMap.Entry<K,V> first;
        // accessOrder = true 时, head节点即为 最老被访问的
        // removeEldestEntry(first) 当 removeEldestEntry 返回true时 会移除头结点
        // 通常实现 LRUCache 时,需要 将 1. accessOrder = true 2. 实现 removeEldestEntry()  当容量达到阈值的时候 返回true
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
    
    // 节点被访问后 执行
    void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap.Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }
```



## 3. get





## 4. containsValue

```java
// LinkedHashMap 遍历整个双向链表
    public boolean containsValue(Object value) {
        for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
            if (v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
    }
```

```java
// HashMap  遍历整个 table,
    public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }
```























-----------------------------------------
[面试必备：LinkedHashMap源码解析（JDK8）](https://blog.csdn.net/zxt0601/article/details/77429150)