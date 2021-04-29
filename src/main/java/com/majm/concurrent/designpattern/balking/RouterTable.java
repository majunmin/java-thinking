package com.majm.concurrent.designpattern.balking;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 3:20 下午
 * @since
 */
public class RouterTable {

    //Key: 接口名
    //Value: 路由集合
    Map<String, CopyOnWriteArraySet<Router>> rt = new ConcurrentHashMap<>();

    // 路由表是否发生变化
    volatile boolean changed;

    //  将路由表写入本地文件的线程池
    ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

    // 启动定时任务
    // 将变更后的路由表写入本地文件
    public void startLocalServer() {
        threadPool.scheduleAtFixedRate(this::autoSave, 1, 1, TimeUnit.MINUTES);
    }

    // 保存路由表到本地文件
    private void autoSave() {
        // ...
        if (!changed) {
            return;
        }
        changed = false;
        // 将路由表写入本地文件,做备份
        // 省略其方法实现
        doSave2Local();
    }

    private void doSave2Local() {
        // ...
    }

    public void remove(Router router) {
        Set<Router> routers = rt.get(router.getIface());
        if (Objects.nonNull(routers)) {
            routers.remove(router);
            // 路由表发生变化
            changed = true;
        }
    }

    public void add(Router router) {
        CopyOnWriteArraySet<Router> routers = rt.computeIfAbsent(router.getIface(), r -> new CopyOnWriteArraySet<>());
        routers.add(router);
        // 路由表发生变化
        changed = true;
    }


}
