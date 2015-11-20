package com.flaiker.zero.services.rmtasks;

/**
 * Created by Flaiker on 27.12.2014.
 */
public abstract class AbstractTask {
    private boolean executedInOwnThread;
    private float   propableTime;
    private String  taskDescription;

    public AbstractTask(float propableTime, boolean executedInOwnThread, String taskDescription) {
        this.propableTime = propableTime;
        this.executedInOwnThread = executedInOwnThread;
        this.taskDescription = taskDescription;
    }

    public abstract void run();

    public abstract boolean isDone();

    public abstract float getPercentageCompleted();

    public float getPropableTime() {
        return propableTime;
    }

    public boolean isExecutedInOwnThread() {
        return executedInOwnThread;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}
