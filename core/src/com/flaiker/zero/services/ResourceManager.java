/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.flaiker.zero.services.rmtasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourceManager {
    public static final String LOG = ResourceManager.class.getSimpleName();

    private AssetManager    assetManager;
    private ExecutorService singleThreadExecutor;

    private List<AbstractTask> taskQueue;
    private AbstractTask       currentTask;
    private float              calculatedTime;

    public ResourceManager() {
        taskQueue = new ArrayList<>();

        assetManager = new AssetManager();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private void runTask(final AbstractTask task) {
        if (task.isExecutedInOwnThread()) {
            Thread taskThread = new Thread(() -> {
                currentTask = task;
                task.run();
                currentTask = null;
            });
            singleThreadExecutor.submit(taskThread);
        } else {
            singleThreadExecutor.shutdown();
            Thread waitingThread = new Thread(() -> {
                try {
                    while (!singleThreadExecutor.isTerminated()) {
                        Thread.sleep(100);
                    }
                    currentTask = task;
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            waitingThread.start();
        }
    }

    public void addTaskToQueue(AbstractTask task) {
        taskQueue.add(task);
    }

    public void runThroughTaskQueue() {
        for (AbstractTask task : taskQueue) {
            calculatedTime += task.getPropableTime();
        }

        taskQueue.forEach(this::runTask);
    }

    public float getLoadingPercent() {
        float done = 0f;
        for (AbstractTask task : taskQueue) {
            if (task == currentTask) {
                done += task.getPercentageCompleted() * task.getPropableTime();
                break;
            } else {
                done += task.getPropableTime();
            }
        }
        Gdx.app.log(LOG, "Done: " + done / calculatedTime);
        return done / calculatedTime;
    }

    public boolean isDoneLoading() {
        if (taskQueue.size() == 0 || taskQueue.get(taskQueue.size() - 1).isDone()) {
            taskQueue.clear();
            singleThreadExecutor = Executors.newSingleThreadExecutor();
            return true;
        } else return false;
    }

    public String getCurrentTaskDescription() {
        return currentTask == null ? "Doing nothing" : currentTask.getTaskDescription();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
