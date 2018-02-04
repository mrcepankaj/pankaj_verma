package com.arity.pveru.sensorrecorder.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class manage Executor service instance.
 */
public class ExecutorHelper {

    private static final Map<String, ExecutorService> sExecutorServiceMap = new HashMap<>();

    /**
     * Gets the executor instance represented by the tag. If no executor service exists, the system will create one.
     *
     * @param tag name of executor service
     * @return the executor service by the tag name
     */
    public static ExecutorService getExecutorInstance(String tag) {
        ExecutorService service = sExecutorServiceMap.get(tag);
        if (service == null || service.isShutdown() || service.isTerminated()) {
            service = Executors.newSingleThreadExecutor();
            sExecutorServiceMap.put(tag, service);
        }

        return service;
    }

    public static ExecutorService getLogExecutorInstance() {
        return getExecutorInstance("LogExecutor");
    }

    public static ExecutorService getActivityExecutorInstance() {
        return getExecutorInstance("ActivityExecutor");
    }

    public static ExecutorService getLocationExecutorInstance() {
        return getExecutorInstance("LocationExecutor");
    }

    public static ExecutorService getMEMSExecutorInstance() {
        return getExecutorInstance("MemsExecutor");
    }

    public static ExecutorService getFileProcessExecutorInstance() {
        return getExecutorInstance("FileProcessExecutor");
    }

    public static ExecutorService getGenericExecutorInstance() {
        return getExecutorInstance("GenericExecutor");
    }


}
