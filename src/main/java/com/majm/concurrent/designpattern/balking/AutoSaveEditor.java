package com.majm.concurrent.designpattern.balking;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 3:09 下午
 * @since
 */
public class AutoSaveEditor {

    // 文件是否被修改过
    volatile boolean changed = false;

    // 定时任务线程池
    ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

    public void startAutoSave() {
        threadPool.scheduleAtFixedRate(this::autoSave, 5, 5, TimeUnit.SECONDS);
    }

    // 自动存盘操作
    public void autoSave() {
        if (!changed) {
            return;
        }
        changed = false;
        // 执行存盘操作
        this.doAutoSave();
    }

    private void doAutoSave() {
        //...
    }

    // 编辑操作
    void edit() {
        // 省略编辑逻辑
        changed = true;
    }


}
