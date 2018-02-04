package com.arity.pveru.sensorrecorder.common;


import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;


public class FileManager {

    protected static final String TAG = "FileManager";

    public static Map<String, SoftReference<FileManager>> dictFileManager = new HashMap<>();
    private final String mFilePath;
    private ExecutorService mExecutor;

    private FileManager(String filePath, ExecutorService executor) {
        mFilePath = filePath;
        mExecutor = executor;
    }

    public static FileManager getInstance(String file, ExecutorService executor) {
        SoftReference<FileManager> managerReference = dictFileManager.get(file);
        FileManager manager = null;
        if (managerReference != null) {
            manager = managerReference.get();
        }
        if (manager == null) {
            manager = new FileManager(file, executor);
            dictFileManager.put(file, new SoftReference<>(manager));

        } else {
            manager.mExecutor = executor;
        }

        return manager;
    }

    public static FileManager getInstance(String file) {
        return getInstance(file, ExecutorHelper.getFileProcessExecutorInstance());
    }


    /**
     * @param text
     * @param append
     */
    public synchronized void writeData(String text, boolean append) {

        try {
            mExecutor.execute(new WriteRunnable(text, append));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getLocalizedMessage());
            Utils.putErrorLog(ex.getLocalizedMessage());
        }
    }

    private class WriteRunnable implements Runnable {

        private String mData;
        private boolean mAppend;

        WriteRunnable(String data, boolean append) {
            mData = data;
            mAppend = append;
        }

        @Override
        public void run() {
            try {
                File file = new File(mFilePath);

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                FileWriter fileWriter = new FileWriter(file, mAppend);
                BufferedWriter out = new BufferedWriter(fileWriter);
                out.write(mData);

                out.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", e.getLocalizedMessage());
                Utils.putErrorLog(e.getLocalizedMessage());
            }
        }
    }
}
